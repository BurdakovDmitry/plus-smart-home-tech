package collector.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;
import java.util.List;

@Component
public class CollectorMapper {
    public SensorEventAvro mapToSensorAvro(SensorEventProto event) {
        SpecificRecordBase payload;
        switch (event.getPayloadCase()) {
            case CLIMATE_SENSOR -> {
                ClimateSensorProto sensorEvent = event.getClimateSensor();
                payload = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(sensorEvent.getTemperatureC())
                        .setCo2Level(sensorEvent.getCo2Level())
                        .setHumidity(sensorEvent.getHumidity())
                        .build();
            }
            case LIGHT_SENSOR -> {
                LightSensorProto sensorEvent = event.getLightSensor();
                payload = LightSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setLuminosity(sensorEvent.getLuminosity())
                        .build();
            }
            case MOTION_SENSOR -> {
                MotionSensorProto sensorEvent = event.getMotionSensor();
                payload = MotionSensorAvro.newBuilder()
                        .setLinkQuality(sensorEvent.getLinkQuality())
                        .setMotion(sensorEvent.getMotion())
                        .setVoltage(sensorEvent.getVoltage())
                        .build();
            }
            case SWITCH_SENSOR -> {
                SwitchSensorProto sensorEvent = event.getSwitchSensor();
                payload = SwitchSensorAvro.newBuilder()
                        .setState(sensorEvent.getState())
                        .build();
            }
            case TEMPERATURE_SENSOR -> {
                TemperatureSensorProto sensorEvent = event.getTemperatureSensor();
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
                .setTimestamp(Instant.ofEpochSecond(
                        event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }

    public HubEventAvro mapToHubAvro(HubEventProto proto) {
        SpecificRecordBase payload;
        switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> {
                DeviceAddedEventProto deviceAddedEvent = proto.getDeviceAdded();
                payload = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getType().name()))
                        .build();
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEventProto deviceRemovedEvent = proto.getDeviceRemoved();
                payload = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEventProto scenarioAddedEvent = proto.getScenarioAdded();
                List<DeviceActionAvro> actions = scenarioAddedEvent.getActionList()
                        .stream()
                        .map(this::mapDeviceActionAvro)
                        .toList();
                List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditionList()
                        .stream()
                        .map(this::mapScenarioCondition)
                        .toList();
                payload = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setActions(actions)
                        .setConditions(conditions)
                        .build();
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEventProto scenarioRemovedEvent = proto.getScenarioRemoved();
                payload = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
            }
            default -> throw new IllegalArgumentException("Неизвестное событие хаба: " + proto);
        }
        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(
                        proto.getTimestamp().getSeconds(),
                        proto.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }

    private DeviceActionAvro mapDeviceActionAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    private ScenarioConditionAvro mapScenarioCondition(ScenarioConditionProto condition) {
        ScenarioConditionAvro.Builder conditionBuilder = ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()));

        switch (condition.getValueCase()) {
            case BOOL_VALUE -> conditionBuilder.setValue(condition.getBoolValue());
            case INT_VALUE -> conditionBuilder.setValue(condition.getIntValue());
            case VALUE_NOT_SET -> throw new IllegalArgumentException("Значение условия сценария не задано");
        }

        return conditionBuilder.build();
    }
}
