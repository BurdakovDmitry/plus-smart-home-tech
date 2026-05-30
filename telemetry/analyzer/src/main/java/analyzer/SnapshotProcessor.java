package analyzer;

import analyzer.client.AnalyzerClient;
import analyzer.service.SnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor{
    private final SnapshotService snapshotService;
    private final AnalyzerClient client;
    private final String TELEMETRY_SNAPSHOTS_V1_TOPIC = "telemetry.snapshots.v1";

    public void start() {
        Consumer<String, SensorsSnapshotAvro> consumer = client.getConsumerSnapshot();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            log.info("Подписываемся на топик: {}", TELEMETRY_SNAPSHOTS_V1_TOPIC);
            consumer.subscribe(List.of(TELEMETRY_SNAPSHOTS_V1_TOPIC));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        SensorsSnapshotAvro snapshot = record.value();
                        Map<String, SensorStateAvro> sensorState = snapshot.getSensorsState();
                        String hubId = record.key();

                        if (hubId == null && snapshot.getHubId() != null) {
                            hubId = snapshot.getHubId();
                        }

                        Map<String, Integer> currentSensorValues = new HashMap<>();

                        if (sensorState != null) {
                            sensorState.forEach((sensorId, deviceState) -> {
                                Object sensorData = deviceState.getData();

                                if (sensorData instanceof TemperatureSensorAvro tempSensor) {
                                    currentSensorValues.put(sensorId, tempSensor.getTemperatureC());
                                } else if (sensorData instanceof LightSensorAvro lightSensor) {
                                    currentSensorValues.put(sensorId, lightSensor.getLuminosity());
                                } else if (sensorData instanceof MotionSensorAvro motionSensor) {
                                    currentSensorValues.put(sensorId, motionSensor.getMotion() ? 1 : 0);
                                } else if (sensorData instanceof SwitchSensorAvro switchSensor) {
                                    currentSensorValues.put(sensorId, switchSensor.getState() ? 1 : 0);
                                } else if (sensorData instanceof ClimateSensorAvro climateSensor) {
                                    // Делаем составной ключ в зависимости от типа показаний
                                    currentSensorValues.put(sensorId + "_TEMPERATURE", climateSensor.getTemperatureC());
                                    currentSensorValues.put(sensorId + "_HUMIDITY", climateSensor.getHumidity());
                                    currentSensorValues.put(sensorId + "_CO2", climateSensor.getCo2Level());
                                }
                            });
                        }

                        snapshotService.analyzeSnapshot(hubId, currentSensorValues);

                    } catch (Exception e) {
                        log.error("Ошибка при обработке отдельной записи снапшота: ", e);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.info("Фиксация смещений консьюмера");
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
