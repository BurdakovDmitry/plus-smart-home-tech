package collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;

@Getter
@Setter
@ToString
public class ScenarioCondition {
    private String sensorId;
    private ConditionTypeProto type;
    private ConditionOperationProto operation;
    private Object value;
}
