package analyzer.service;

import java.util.Map;

public interface SnapshotService {
    void analyzeSnapshot(String hubId, Map<String, Integer> sensorValues);
}
