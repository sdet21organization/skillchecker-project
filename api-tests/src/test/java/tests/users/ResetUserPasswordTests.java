package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResetUserPasswordTests extends BaseTest {

    @Test
    @DisplayName("Users: reset password")
    void resetPassword_returns200() {
        String email = ConfigurationReader.get("test.user.email");
        String fullName = ConfigurationReader.get("test.user.fullName");
        String password = ConfigurationReader.get("test.user.password");
        String baseRole = ConfigurationReader.get("test.user.role");
        String newPassword = ConfigurationReader.get("test.user.newPassword");

        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, baseRole, true);
            assertEquals(201, create.statusCode(), "Precondition failed: expected 201, body=" + create.asString());
            id = String.valueOf(create.jsonPath().get("id"));
        }

        Response resp = Users.resetUserPassword(cookie, id, newPassword);

        assertEquals(200, resp.statusCode(), "Expected 200, body=" + resp.asString());
        assertEquals("Password reset successfully", resp.jsonPath().getString("message"), "message mismatch");
    }
}
