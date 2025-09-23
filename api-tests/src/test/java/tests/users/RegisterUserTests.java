package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.*;
import static wrappers.Users.deleteByEmailIfExists;

public class RegisterUserTests extends BaseTest {

    @Test
    @DisplayName("Users: register")
    void registerUser_fromConfig_returns201() {
        String fullName = ConfigurationReader.get("test.user.fullName");
        String email = ConfigurationReader.get("test.user.email");
        String password = ConfigurationReader.get("test.user.password");
        String role = ConfigurationReader.get("test.user.role");

        deleteByEmailIfExists(cookie, email);

        Response resp = Users.registerUser(cookie, email, fullName, password, role, true);

        assertEquals(201, resp.statusCode(),
                "Expected 201 on register, body=" + resp.asString());
        assertEquals(email, resp.jsonPath().getString("email"), "email mismatch");
        assertEquals(role, resp.jsonPath().getString("role"), "role mismatch");
        assertTrue(resp.jsonPath().getBoolean("active"), "active should be true");
        assertNotNull(resp.jsonPath().getString("id"), "id should be present");
    }

    @Test
    @DisplayName("Users: register duplicate - negative")
    void registerDuplicate_returns400() {
        String fullName = ConfigurationReader.get("test.user.fullName");
        String email = ConfigurationReader.get("test.user.email");
        String password = ConfigurationReader.get("test.user.password");
        String role = ConfigurationReader.get("test.user.role");

        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, role, true);
            assertEquals(201, create.statusCode(), "Precondition failed: body=" + create.asString());
        }

        Response dup = Users.registerUser(cookie, email, fullName, password, role, true);
        assertEquals(400, dup.statusCode(), "Expected 400, body=" + dup.asString());
        String msg = dup.jsonPath().getString("message");
        assertNotNull(msg, "error message should be present");
        assertFalse(msg.isBlank(), "error message should not be blank");
    }

    @AfterEach
    void cleanupUser() {
        try {
            deleteByEmailIfExists(cookie, get("test.user.email"));
        } catch (Exception ignore) {
        }
    }
}
