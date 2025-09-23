package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Users: update role")
public class UpdateUserRoleTests extends BaseTest {

    @Test
    @DisplayName("PATCH /api/users/{id} -> 200 and role changes")
    void updateRole_toInterviewer_returns200() {
        String email = ConfigurationReader.get("test.user.email");
        String fullName = ConfigurationReader.get("test.user.fullName");
        String password = ConfigurationReader.get("test.user.password");
        String baseRole = ConfigurationReader.get("test.user.role");
        String newRole = ConfigurationReader.get("test.user.roleChangeTo");
        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, baseRole, true);
            assertEquals(201, create.statusCode(), "Предусловие: создать пользователя");
            id = String.valueOf(create.jsonPath().get("id"));
        }

        Response patch = Users.updateUserRole(cookie, id, newRole);

        assertEquals(200, patch.statusCode());
        assertEquals(newRole, patch.jsonPath().getString("role"));
        assertEquals(email, patch.jsonPath().getString("email"));
        assertEquals(fullName, patch.jsonPath().getString("fullName"));
    }
}
