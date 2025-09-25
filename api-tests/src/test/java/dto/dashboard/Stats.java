package dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    private int totalTests;
    private int activeTests;
    private int totalCandidates;
    private int pendingSessions;
    private int completedSessions;
}