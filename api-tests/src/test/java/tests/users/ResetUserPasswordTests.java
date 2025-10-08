package tests.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API Tests")
@Owner("Oleksiy Korniyenko")
@DisplayName("Reset user password (Settings(API)")
public class ResetUserPasswordTests extends BaseTest {

    private String id;

    @BeforeEach
    void ensurePresent() {
        id = Users.ensureUserExists(
                cookie,
                get("test.user.email"),
                get("test.user.fullName"),
                get("test.user.password"),
                get("test.user.role")
        );
    }

    @Test
    @DisplayName("Users: reset password")
    void resetPassword_returns200() {
        var resp = Users.resetUserPassword(cookie, id, get("test.user.newPassword"));
        assertEquals(200, resp.statusCode(), "Expected 200, body=" + resp.asString());
        assertEquals("Password reset successfully", resp.jsonPath().getString("message"));
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
