package dto.users;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}
