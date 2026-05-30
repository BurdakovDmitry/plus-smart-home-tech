package analyzer.client;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.HubEventDeserializer;
import ru.yandex.practicum.SnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;
import java.util.UUID;

@Configuration
public class AnalyzerClientConfiguration {
    @Bean
    public AnalyzerClient getClient() {
        return new AnalyzerClient() {
            private Consumer<String, HubEventAvro> consumerHub;
            private Consumer<String, SensorsSnapshotAvro> consumerSnapshot;

            @Override
            public Consumer<String, HubEventAvro> getConsumerHub() {
                if (consumerHub == null) {
                    initConsumerHub();
                }
                return consumerHub;
            }

            @Override
            public Consumer<String, SensorsSnapshotAvro> getConsumerSnapshot() {
                if (consumerSnapshot == null) {
                    initConsumerSnapshot();
                }
                return consumerSnapshot;
            }

            @Override
            @PreDestroy
            public void stop() {
                if (consumerHub != null) {
                    consumerHub.close();
                }

                if (consumerSnapshot != null) {
                    consumerSnapshot.close();
                }
            }

            private void initConsumerHub() {
                Properties config = new Properties();
                config.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
                config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);

                consumerHub = new KafkaConsumer<>(config);
            }

            private void initConsumerSnapshot() {
                Properties config = new Properties();
                config.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
                config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
                config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
                config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SnapshotDeserializer.class);

                consumerSnapshot = new KafkaConsumer<>(config);
            }
        };
    }
}
