package collector.client;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public interface CollectorClient {
    Producer<String, byte[]> getProducer();

    Consumer<String, SensorEventAvro> getConsumer();

    void stop();
}
