package collector.service;

import collector.model.hub.HubEvent;
import collector.model.sensor.SensorEvent;

public interface CollectorService {
    void sendSensorEvent(SensorEvent event);

    void sendHubEvent(HubEvent event);
}
