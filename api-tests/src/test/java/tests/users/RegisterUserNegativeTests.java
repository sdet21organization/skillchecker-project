package tests.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterUserNegativeTests extends BaseTest {

    @BeforeEach
    void ensurePresent() {
        Users.ensureUserExists(
                cookie,
                get("test.user.email"),
                get("test.user.fullName"),
                get("test.user.password"),
                get("test.user.role")
        );
    }

    @Test
    @DisplayName("Users: register duplicate -> 400")
    void registerDuplicate_returns400() {
        var resp = Users.registerUser(
                cookie,
                get("test.user.email"),
                get("test.user.fullName"),
                get("test.user.password"),
                get("test.user.role"),
                true
        );
        assertEquals(400, resp.statusCode(), "Expected 400, body=" + resp.asString());
        String msg = resp.jsonPath().getString("message");
        assertNotNull(msg);
        assertFalse(msg.isBlank());
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
