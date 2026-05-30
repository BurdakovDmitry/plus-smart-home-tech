package analyzer;

import analyzer.client.AnalyzerClient;
import analyzer.service.HubEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final HubEventService hubEventService;
    private final AnalyzerClient client;
    private static final String TELEMETRY_HUBS_V1_TOPIC = "telemetry.hubs.v1";

    @Override
    public void run() {
        Consumer<String, HubEventAvro> consumer = client.getConsumerHub();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            log.info("Подписываемся на топик: {}", TELEMETRY_HUBS_V1_TOPIC);
            consumer.subscribe(List.of(TELEMETRY_HUBS_V1_TOPIC));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        HubEventAvro event = record.value();
                        String hubId = event.getHubId();
                        Object payload = event.getPayload();

                        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
                            hubEventService.addSensor(hubId, deviceAdded.getId());
                        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
                            hubEventService.removeSensor(deviceRemoved.getId());
                        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
                            hubEventService.addScenario(
                                    hubId,
                                    scenarioAdded.getName(),
                                    scenarioAdded.getConditions(),
                                    scenarioAdded.getActions()
                            );
                        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
                            hubEventService.removeScenario(hubId, scenarioRemoved.getName());
                        }
                    } catch (Exception e) {
                        log.error("Ошибка при обработке записи хаба: ", e);
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
