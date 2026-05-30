package analyzer.service;

import analyzer.exception.NotFoundException;
import analyzer.model.Action;
import analyzer.model.Condition;
import analyzer.model.Scenario;
import analyzer.model.ScenarioAction;
import analyzer.model.ScenarioCondition;
import analyzer.model.Sensor;
import analyzer.repository.ActionRepository;
import analyzer.repository.ConditionRepository;
import analyzer.repository.ScenarioActionRepository;
import analyzer.repository.ScenarioConditionRepository;
import analyzer.repository.ScenarioRepository;
import analyzer.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    // Добавляем новый датчик
    @Override
    public void addSensor(String hubId, String sensorId) {
        log.info("Добавление датчика {} для хаба {}", sensorId, hubId);

        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseGet(() -> {
                    Sensor s = new Sensor();
                    s.setId(sensorId);
                    return s;
                });

        sensor.setHubId(hubId);
        sensorRepository.save(sensor);

        log.info("Датчик {} для хаба {} успешно добавлен", sensorId, hubId);
    }

    // Удаляем датчик
    @Override
    public void removeSensor(String sensorId) {
        log.info("Удаление датчика {} из хаба", sensorId);

        if (sensorRepository.existsById(sensorId)) {
            sensorRepository.deleteById(sensorId);
        }

        log.info("Датчик {} успешно удален из хаба", sensorId);
    }

    // Добавляем сценарий
    public void addScenario(String hubId, String scenarioName,
                             List<ScenarioConditionAvro> conditionsAvro,
                             List<DeviceActionAvro> actionsAvro) {
        log.info("Сохранение сценария {} для хаба {}", scenarioName, hubId);

        // Находим существующий сценарий или создаем новый
        Scenario scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseGet(() -> {
                    Scenario newScenario = new Scenario();
                    newScenario.setHubId(hubId);
                    newScenario.setName(scenarioName);
                    return scenarioRepository.save(newScenario);
                });

        // Обработка условий
        if (conditionsAvro != null) {
            log.info("Приступаем к обработке условий сценария {}", scenarioName);

            for (ScenarioConditionAvro condAvro : conditionsAvro) {
                String sensorId = condAvro.getSensorId();

                // Проверка триггера на существование датчика в БД с переданным hubId
                Sensor sensor = checkSensor(sensorId, hubId);

                // Создаем и сохраняем условие
                Condition condition = new Condition();
                condition.setType(condAvro.getType());
                condition.setOperation(condAvro.getOperation());

                Object avroValue = condAvro.getValue();
                if (avroValue instanceof Integer) {
                    condition.setValue((Integer) avroValue);
                } else if (avroValue instanceof Boolean) {
                    condition.setValue((Boolean) avroValue ? 1 : 0);
                }

                condition = conditionRepository.save(condition);

                // Создаем связь через @EmbeddedId
                ScenarioCondition scenarioCondition = new ScenarioCondition();
                scenarioCondition.setScenario(scenario);
                scenarioCondition.setSensor(sensor);
                scenarioCondition.setCondition(condition);

                scenarioCondition.setId(new ScenarioCondition.ScenarioConditionId(
                        scenario.getId(),
                        sensor.getId(),
                        condition.getId()
                ));

                scenarioConditionRepository.save(scenarioCondition);
            }
            log.info("Обработка условий для сценария {} успешно завершена", scenarioName);
        }

        // Обработка действий
        if (actionsAvro != null) {
            log.info("Приступаем к обработке действий сценария {}", scenarioName);

            for (DeviceActionAvro actAvro : actionsAvro) {
                String sensorId = actAvro.getSensorId();

                // Проверка триггера на существование датчика в БД с переданным hubId
                Sensor sensor = checkSensor(sensorId, hubId);

                // Создаем и сохраняем действие
                Action action = new Action();
                action.setType(actAvro.getType());

                if (actAvro.getValue() != null) {
                    action.setValue(actAvro.getValue());
                }

                action = actionRepository.save(action);

                // Создаем связь через @EmbeddedId
                ScenarioAction scenarioAction = new ScenarioAction();
                scenarioAction.setScenario(scenario);
                scenarioAction.setSensor(sensor);
                scenarioAction.setAction(action);

                scenarioAction.setId(new ScenarioAction.ScenarioActionId(
                        scenario.getId(),
                        sensor.getId(),
                        action.getId()
                ));

                scenarioActionRepository.save(scenarioAction);
            }
            log.info("Обработка действий для сценария {} успешно завершена", scenarioName);
        }
        log.info("Сценарий {} успешно сохранен.", scenarioName);
    }

    // Удаляем сценарий
    @Override
    public void removeScenario(String hubId, String scenarioName) {
        log.info("Удаление сценария {} для хаба {}", scenarioName, hubId);

        scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .ifPresent(scenario -> {
                    scenarioConditionRepository.deleteByScenarioId(scenario.getId());
                    scenarioActionRepository.deleteByScenarioId(scenario.getId());
                    scenarioRepository.delete(scenario);
                });

        log.info("Сценарий {} для хаба {} успешно удален", scenarioName, hubId);
    }

    private Sensor checkSensor(String sensorId, String hubId) {
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new NotFoundException("Датчик с id=" + sensorId + " не найден"));

        if (!hubId.equals(sensor.getHubId())) {
            throw new IllegalStateException("Датчик с id=" + sensorId + " уже привязан к другому хабу!");
        }

        return sensor;
    }
}
