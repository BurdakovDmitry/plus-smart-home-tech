package collector.model.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Getter
@Setter
@ToString
public class DeviceAction {
    private String sensorId;
    private ActionTypeAvro type;
    private int value;
}
