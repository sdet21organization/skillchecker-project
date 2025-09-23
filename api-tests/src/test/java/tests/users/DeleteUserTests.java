package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeleteUserTests extends BaseTest {

    @Test
    @DisplayName("Users: delete")
    void deleteUser_returns200() {
        String email = ConfigurationReader.get("test.user.email");
        String fullName = ConfigurationReader.get("test.user.fullName");
        String password = ConfigurationReader.get("test.user.password");
        String role = ConfigurationReader.get("test.user.role");

        String id = Users.findUserIdByEmail(cookie, email);
        if (id == null) {
            Response create = Users.registerUser(cookie, email, fullName, password, role, true);
            assertEquals(201, create.statusCode(), "Precondition failed: body=" + create.asString());
            int newId = create.jsonPath().getInt("id");
            id = String.valueOf(newId);
        }

        Response del = Users.deleteUserById(cookie, id);
        assertEquals(200, del.statusCode(), "Expected 200, body=" + del.asString());
        assertEquals("User deleted", del.jsonPath().getString("message"));
        assertNull(Users.findUserIdByEmail(cookie, email), "User should be absent after delete");
    }
}
