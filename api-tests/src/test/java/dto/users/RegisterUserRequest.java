package dto.users;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String email;
    private String fullName;
    private String password;
    private String role;
    private boolean active;
}
