package collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    private ActionTypeProto type;
    private Integer value;
}
