package analyzer.client;

import org.apache.kafka.clients.consumer.Consumer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface AnalyzerClient {
    Consumer<String, HubEventAvro> getConsumerHub();

    Consumer<String, SensorsSnapshotAvro> getConsumerSnapshot();

    void stop();
}
