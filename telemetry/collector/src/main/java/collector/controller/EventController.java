package collector.controller;

import collector.client.CollectorClient;
import collector.model.CollectorTopics;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final CollectorClient client;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventProto.PayloadCase payloadCase = request.getPayloadCase();
            switch (payloadCase) {
                case LIGHT_SENSOR:
                    System.out.println("Получено событие датчика освещённости");
                    // получаем данные датчика освещённости
                    LightSensorProto lightSensor = request.getLightSensor();
                    System.out.println("Уровень освещённости: " + lightSensor.getLuminosity());
                    break;
                case CLIMATE_SENSOR:
                    System.out.println("Получено событие климатического датчика");
                    // получаем данные климатического датчика
                    ClimateSensorProto climateSensor = request.getClimateSensor();
                    System.out.println("Влажность воздуха: " + climateSensor.getHumidity());
                    break;
                case MOTION_SENSOR:
                    System.out.println("Получено событие датчика движения");
                    // получаем данные датчика движения
                    MotionSensorProto motionSensor = request.getMotionSensor();
                    System.out.println("Фиксация движения: " + motionSensor.getMotion());
                    System.out.println("Качество связи: " + motionSensor.getLinkQuality());
                    break;
                case TEMPERATURE_SENSOR:
                    System.out.println("Получено событие температурного датчика");
                    // получаем данные температурного датчика
                    TemperatureSensorProto temperatureSensor = request.getTemperatureSensor();
                    System.out.println("Температура (C): " + temperatureSensor.getTemperatureC());
                    break;
                case SWITCH_SENSOR:
                    System.out.println("Получено событие датчика-переключателя");
                    // получаем данные датчика-переключателя
                    SwitchSensorProto switchSensor = request.getSwitchSensor();
                    System.out.println("Состояние переключателя: " + switchSensor.getState());
                    break;
                default:
                    System.out.println("Получено событие неизвестного типа: " + payloadCase);
            }

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC,
                    request.getHubId(),
                    request.toByteArray()
            );

            client.getProducer().send(record);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventProto.PayloadCase payloadCase = request.getPayloadCase();
            switch (payloadCase) {
                case DEVICE_ADDED:
                    System.out.println("Получено событие добавления устройства");
                    DeviceAddedEventProto deviceAdded = request.getDeviceAdded();
                    System.out.println("ID устройства: " + deviceAdded.getId());
                    System.out.println("Тип устройства: " + deviceAdded.getType());
                    break;
                case DEVICE_REMOVED:
                    System.out.println("Получено событие удаления устройства");
                    DeviceRemovedEventProto deviceRemoved = request.getDeviceRemoved();
                    System.out.println("Устройства с id: " + deviceRemoved.getId() + " удалено");
                    break;
                case SCENARIO_ADDED:
                    System.out.println("Получено событие добавления сценария");
                    ScenarioAddedEventProto scenarioAdded = request.getScenarioAdded();
                    System.out.println("Название сценария: " + scenarioAdded.getName());
                    break;
                case SCENARIO_REMOVED:
                    System.out.println("Получено событие удаления сценария");
                    ScenarioRemovedEventProto scenarioRemoved = request.getScenarioRemoved();
                    System.out.println("Название удаленного сценария: " + scenarioRemoved.getName());
                    break;
                default:
                    System.out.println("Получено событие неизвестного типа: " + payloadCase);
            }

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    CollectorTopics.TELEMETRY_HUBS_V1_TOPIC,
                    request.getHubId(),
                    request.toByteArray()
            );

            client.getProducer().send(record);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
