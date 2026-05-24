package aggregator;

import collector.client.CollectorClient;
import collector.model.CollectorTopics;
import collector.serialiser.CollectorAvroSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final CollectorClient client;
    private final CollectorAvroSerializer avroSerializer;
    private final SnapshotAggregator aggregator;

    public void start() {
        Consumer<String, SensorEventAvro> consumer = client.getConsumer();
        Producer<String, byte[]> producer = client.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            log.info("Подписываемся на топик: {}", CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC);
            consumer.subscribe(List.of(CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();

                    Optional<SensorsSnapshotAvro> updatedSnapshot = aggregator.updateState(event);

                    updatedSnapshot.ifPresent(snapshot -> {
                        byte[] snapshotBytes = avroSerializer.serialize(CollectorTopics.TELEMETRY_SNAPSHOTS_V1_TOPIC, snapshot);

                        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord<>(
                                CollectorTopics.TELEMETRY_SNAPSHOTS_V1_TOPIC,
                                snapshot.getHubId(),
                                snapshotBytes
                        );

                        producer.send(producerRecord);

                    });
                }

                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Сброса данных в буффере у продюсера");
                producer.flush();
                log.info("Фиксация смещений консьюмера");
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
