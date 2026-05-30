package analyzer.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

import java.time.Instant;

@Slf4j
@Service
public class HubRouterClient {
    private final HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterClient(@GrpcClient("hub-router")
                           HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendDeviceAction(String hubId, String scenarioName, String sensorId, String actionType, Integer value) {
        try {
            Instant now = Instant.now();
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            ActionTypeProto protoType = ActionTypeProto.valueOf(actionType);

            DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                    .setSensorId(sensorId)
                    .setType(protoType);

            if (value != null) {
                actionBuilder.setValue(value);
            }

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setAction(actionBuilder.build())
                    .setTimestamp(timestamp)
                    .build();

            Empty response = hubRouterClient.handleDeviceAction(request);
            log.info("gRPC команда для устройства {} успешно доставлена в Hub Router.", sensorId);
        } catch (IllegalArgumentException e) {
            log.error("Тип действия {} не найден в ActionTypeProto", actionType);
        } catch (Exception e) {
            log.error("Ошибка gRPC при отправке команды в Hub Router: ", e);
        }
    }
}
