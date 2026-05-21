package collector.controller;

import collector.client.CollectorClient;
import collector.mapper.CollectorMapper;
import collector.model.CollectorTopics;
import collector.serialiser.CollectorAvroSerializer;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@GrpcService
@RequiredArgsConstructor
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final CollectorMapper mapper;
    private final CollectorClient client;
    private final CollectorAvroSerializer avroSerializer;

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventAvro sensorAvro = mapper.mapToSensorAvro(request);
            byte[] avroByte = avroSerializer.serialize(CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC, sensorAvro);

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC,
                    request.getHubId(),
                    avroByte
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
            HubEventAvro eventAvro = mapper.mapToHubAvro(request);
            byte[] avroByte = avroSerializer.serialize(CollectorTopics.TELEMETRY_HUBS_V1_TOPIC, eventAvro);

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    CollectorTopics.TELEMETRY_HUBS_V1_TOPIC,
                    request.getHubId(),
                    avroByte
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
