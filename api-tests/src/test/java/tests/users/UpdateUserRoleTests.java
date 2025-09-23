package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static wrappers.Users.deleteByEmailIfExists;

public class UpdateUserRoleTests extends BaseTest {

    @Test
    @DisplayName("Users: update role")
    void updateRole_toInterviewer_returns200() {
        String email = ConfigurationReader.get("test.user.email");
        String fullName = ConfigurationReader.get("test.user.fullName");
        String password = ConfigurationReader.get("test.user.password");
        String baseRole = ConfigurationReader.get("test.user.role");
        String newRole = ConfigurationReader.get("test.user.roleChangeTo");

        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, baseRole, true);
            assertEquals(201, create.statusCode(), "Precondition failed: expected 201, body=" + create.asString());
            id = String.valueOf(create.jsonPath().getInt("id"));
        }

        Response patch = Users.updateUserRole(cookie, id, newRole);

        assertEquals(200, patch.statusCode(), "Expected 200, body=" + patch.asString());
        assertEquals(newRole, patch.jsonPath().getString("role"), "role mismatch");
        assertEquals(email, patch.jsonPath().getString("email"), "email mismatch");
        assertEquals(fullName, patch.jsonPath().getString("fullName"), "fullName mismatch");
    }

    @AfterEach
    void cleanupUser() {
        try {
            deleteByEmailIfExists(cookie, get("test.user.email"));
        } catch (Exception ignore) {
        }
    }
}
