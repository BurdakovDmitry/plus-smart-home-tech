package aggregator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SnapshotAggregator {
    private final Map<String, SensorsSnapshotAvro> snapshotsMap = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro snapshot = snapshotsMap.getOrDefault(hubId,
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setTimestamp(event.getTimestamp())
                        .setSensorsState(new HashMap<>())
                        .build());
        snapshotsMap.put(hubId, snapshot);

        SensorStateAvro oldStateAvro = snapshot.getSensorsState().get(sensorId);

        if (oldStateAvro != null) {
            if (oldStateAvro.getTimestamp().isAfter(event.getTimestamp()) ||
                    oldStateAvro.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(sensorId, newStateAvro);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }
}
