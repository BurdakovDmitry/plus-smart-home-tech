package analyzer.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "scenario_actions")
@Data
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id = new ScenarioActionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    private Action action;


    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScenarioActionId implements Serializable {
        @Column(name = "scenario_id")
        private Long scenarioId;

        @Column(name = "sensor_id")
        private String sensorId;

        @Column(name = "action_id")
        private Long actionId;
    }
}
