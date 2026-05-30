package analyzer.repository;

import analyzer.model.ScenarioCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioCondition.ScenarioConditionId> {
    @Query("DELETE FROM ScenarioCondition sc WHERE sc.id.scenarioId = :scenarioId")
    void deleteByScenarioId(Long scenarioId);
}
