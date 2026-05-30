package analyzer.service;

import analyzer.model.Action;
import analyzer.model.Condition;
import analyzer.model.Scenario;
import analyzer.model.ScenarioAction;
import analyzer.model.ScenarioCondition;
import analyzer.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterClient hubRouterClient;

    @Override
    public void analyzeSnapshot(String hubId, Map<String, Integer> sensorValues) {
        log.debug("Начало анализа снапшота для хаба {}.", hubId);

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        if (scenarios.isEmpty()) {
            log.debug("Сценарии для хаба {} в базе данных не обнаружено.", hubId);
            return;
        }

        for (Scenario scenario : scenarios) {
            boolean allConditionsOperation = true;

            // Проверяем каждое условие сценария
            for (ScenarioCondition scenarioCondition : scenario.getConditions()) {
                String sensorId = scenarioCondition.getSensor().getId();
                Condition condition = scenarioCondition.getCondition();

                // Ищем текущее значение конкретного датчика в пришедшем из Kafka снапшоте
                String key = getKey(sensorId, condition.getType());
                Integer currentSensorValue = sensorValues.get(key);

                if (currentSensorValue == null) {
                    log.debug("Датчик {} из сценария {} отсутствует в текущем снапшоте хаба.", sensorId, scenario.getName());
                    allConditionsOperation = false;
                    break;
                }

                // Вычисляем математическое соответствие (Текущее значение VS Опорное значение из БД)
                if (!isConditionOperation(currentSensorValue, condition.getOperation(), condition.getValue())) {
                    allConditionsOperation = false;
                    log.debug("Обнаружена неизвестная операция условия: {}", condition.getOperation());
                    break;
                }
            }

            if (allConditionsOperation) {
                log.info("Все условия сценария {} для хаба {} выполнены. Формируем gRPC команды.", scenario.getName(), hubId);

                // Перебираем список действий, заложенных в этот сценарий
                for (ScenarioAction scenarioAction : scenario.getActions()) {
                    String sensorId = scenarioAction.getSensor().getId();
                    Action action = scenarioAction.getAction();

                    // Отправляем команду через gRPC клиент в Hub Router
                    hubRouterClient.sendDeviceAction(
                            hubId,
                            scenario.getName(),
                            sensorId,
                            action.getType().name(),
                            action.getValue()
                    );
                }
            }
        }
    }

    // Метод сопоставляет текущее состояние прибора с правилами из базы данных.
    private boolean isConditionOperation(int currentValue, ConditionOperationAvro operation, int referenceValue) {
        return switch (operation) {
            case GREATER_THAN -> currentValue > referenceValue;
            case LOWER_THAN -> currentValue < referenceValue;
            case EQUALS -> currentValue == referenceValue;
        };
    }

    //  Метод для определения ключа
    private String getKey(String sensorId, ConditionTypeAvro conditionType) {
        return switch (conditionType) {
            case CO2LEVEL -> sensorId + "_CO2";
            case HUMIDITY -> sensorId + "_HUMIDITY";
            case TEMPERATURE -> sensorId + "_TEMPERATURE";
            default -> sensorId;
        };
    }
}
