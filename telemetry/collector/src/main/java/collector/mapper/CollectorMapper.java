package collector.mapper;

import collector.model.enums.DeviceEventType;
import collector.model.enums.SensorEventType;
import collector.model.hub.DeviceAction;
import collector.model.hub.DeviceAddedEvent;
import collector.model.hub.DeviceRemovedEvent;
import collector.model.hub.HubEvent;
import collector.model.hub.ScenarioAddedEvent;
import collector.model.hub.ScenarioCondition;
import collector.model.hub.ScenarioRemovedEvent;
import collector.model.sensor.ClimateSensorEvent;
import collector.model.sensor.LightSensorEvent;
import collector.model.sensor.MotionSensorEvent;
import collector.model.sensor.SensorEvent;
import collector.model.sensor.SwitchSensorEvent;
import collector.model.sensor.TemperatureSensorEvent;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.List;

@Component
public class CollectorMapper {
    public SensorEventAvro mapToSensorAvro(SensorEvent event) {
        SpecificRecordBase payload;
        switch (event.getType()) {
            case SensorEventType.CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent sensorEvent = (ClimateSensorEvent) event;
                payload = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(sensorEvent.getTemperatureC())
                        .setCo2Level(sensorEvent.getCo2Level())
                        .setHumidity(sensorEvent.getHumidity())
                        .build();
            }
            case SensorEventType.LIGHT_SENSOR_EVENT -> {
                LightSensorEvent sensorEvent = (LightSensorEvent) event;
                payload = LightSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setLuminosity(sensorEvent.getLuminosity())
                        .build();
            }
            case SensorEventType.MOTION_SENSOR_EVENT -> {
                MotionSensorEvent sensorEvent = (MotionSensorEvent) event;
                payload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setMotion(sensorEvent.isMotion())
                        .setVoltage(sensorEvent.getVoltage())
                        .build();
            }
            case SensorEventType.SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent sensorEvent = (SwitchSensorEvent) event;
                payload = SwitchSensorAvro.newBuilder()
                        .setState(sensorEvent.isState())
                        .build();
            }
            case SensorEventType.TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent sensorEvent = (TemperatureSensorEvent) event;
                payload = TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(sensorEvent.getTemperatureC())
                        .setTemperatureF(sensorEvent.getTemperatureF())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестное событие датчика: " + event);
        }

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public HubEventAvro mapToHubAvro(HubEvent event) {
        SpecificRecordBase payload;
        switch (event.getType()) {
            case DeviceEventType.DEVICE_ADDED -> {
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;
                payload = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(deviceAddedEvent.getDeviceType())
                        .build();
            }
            case DeviceEventType.DEVICE_REMOVED -> {
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) event;
                payload = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
            }
            case DeviceEventType.SCENARIO_ADDED -> {
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
                List<DeviceActionAvro> actions = scenarioAddedEvent.getActions()
                        .stream()
                        .map(this::mapDeviceActionAvro)
                        .toList();
                List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditions()
                        .stream()
                        .map(this::mapScenarioCondition)
                        .toList();
                payload = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setActions(actions)
                        .setConditions(conditions)
                        .build();
            }
            case DeviceEventType.SCENARIO_REMOVED -> {
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) event;
                payload = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестное событие хаба: " + event);
        }
        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    private DeviceActionAvro mapDeviceActionAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(action.getType())
                .setValue(action.getValue())
                .build();
    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setOperation(condition.getOperation())
                .setValue(condition.getValue())
                .setType(condition.getType())
                .build();
    }
}
