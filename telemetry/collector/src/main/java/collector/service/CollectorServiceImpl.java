package collector.service;

import collector.client.CollectorClient;
import collector.mapper.CollectorMapper;
import collector.model.CollectorTopics;
import collector.model.hub.HubEvent;
import collector.model.sensor.SensorEvent;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final CollectorClient client;
    private final CollectorMapper mapper;

    @Override
    public void sendSensorEvent(SensorEvent event) {
        SensorEventAvro eventAvro = mapper.mapToSensorAvro(event);

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                CollectorTopics.TELEMETRY_SENSORS_V1_TOPIC,
                event.getHubId(),
                eventAvro
        );
        client.getProducer().send(record);
    }

    @Override
    public void sendHubEvent(HubEvent event) {
        HubEventAvro eventAvro = mapper.mapToHubAvro(event);

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                CollectorTopics.TELEMETRY_HUBS_V1_TOPIC,
                event.getHubId(),
                eventAvro
        );
        client.getProducer().send(record);
    }
}
