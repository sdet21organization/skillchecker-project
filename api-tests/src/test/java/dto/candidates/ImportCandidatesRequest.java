package dto.candidates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ImportCandidatesRequest {
    private List<Candidate> candidates;

    @Data
    @NoArgsConstructor
    public static class Candidate {
        @JsonProperty("Email")
        public String email;
        @JsonProperty("Name")
        public String name;
        @JsonProperty("Position")
        public String position;
    }
}