package analyzer.repository;

import analyzer.model.ScenarioAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioAction.ScenarioActionId> {
    @Query("DELETE FROM ScenarioAction sa WHERE sa.id.scenarioId = :scenarioId")
    void deleteByScenarioId(Long scenarioId);
}
