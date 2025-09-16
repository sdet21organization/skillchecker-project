package dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginSuccessResponse {
    private Long id;
    private Long organizationId;
    private String fullName;
    private String role;
    private String email;
    private boolean active;
    private String lastLogin;
}