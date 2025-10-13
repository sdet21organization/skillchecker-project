package dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelfRegisterRequest {
    private String email;
    private String fullName;
    private String password;
    private String organizationName;
    private String code;
}