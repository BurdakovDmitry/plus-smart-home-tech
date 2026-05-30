package analyzer.model;

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
import lombok.ToString;

import java.io.Serializable;

@Entity
@Table(name = "scenario_conditions")
@Data
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id = new ScenarioConditionId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    @ToString.Exclude
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    @ToString.Exclude
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    @ToString.Exclude
    private Condition condition;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScenarioConditionId implements Serializable {
        @Column(name = "scenario_id")
        private Long scenarioId;

        @Column(name = "sensor_id")
        private String sensorId;

        @Column(name = "condition_id")
        private Long conditionId;
    }
}
