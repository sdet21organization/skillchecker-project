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

public class UpdateUserActiveTests extends BaseTest {

    @Test
    @DisplayName("Users: block & unblock")
    void block_then_unblock_returns200() {
        String email = ConfigurationReader.get("test.user.email");
        String fullName = ConfigurationReader.get("test.user.fullName");
        String password = ConfigurationReader.get("test.user.password");
        String baseRole = ConfigurationReader.get("test.user.role");

        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, baseRole, true);
            assertEquals(201, create.statusCode(), "Precondition failed: body=" + create.asString());
            id = String.valueOf(create.jsonPath().getInt("id"));
        }

        Response block = Users.updateUserActive(cookie, id, false);
        assertEquals(200, block.statusCode(), "Expected 200, body=" + block.asString());
        assertFalse(block.jsonPath().getBoolean("active"), "active should be false");

        Response unblock = Users.updateUserActive(cookie, id, true);
        assertEquals(200, unblock.statusCode(), "Expected 200, body=" + unblock.asString());
        assertTrue(unblock.jsonPath().getBoolean("active"), "active should be true");
    }

    @AfterEach
    void cleanupUser() {
        try {
            deleteByEmailIfExists(cookie, get("test.user.email"));
        } catch (Exception ignore) {
        }
    }
}
