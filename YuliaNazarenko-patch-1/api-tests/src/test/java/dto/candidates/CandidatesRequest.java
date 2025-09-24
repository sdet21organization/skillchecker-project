package dto.candidates;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CandidatesRequest {
    private String name;
    private String email;
    private String phone;
    private String position;
}
