package dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecentActivity {

    private int sessionId;
    private int candidateId;
    private String candidateName;
    private String testName;
    private String status;
    private Boolean passed;
    private String date;
}