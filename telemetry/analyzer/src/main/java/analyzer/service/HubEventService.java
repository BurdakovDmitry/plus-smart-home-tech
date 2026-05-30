package analyzer.service;

import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

public interface HubEventService {
    void addSensor(String hubId, String sensorId);

    void removeSensor(String sensorId);

    void addScenario(String hubId, String scenarioName, List<ScenarioConditionAvro> conditionsAvro,
                      List<DeviceActionAvro> actionsAvro);

    void removeScenario(String hubId, String scenarioName);
}
