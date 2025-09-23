package tests.users;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Users: register")
public class RegisterUserTests extends BaseTest {

    @Test
    @DisplayName("POST /api/register -> 201 (config data)")
    void registerUser_fromConfig_returns201() {
        String fullName = ConfigurationReader.get("test.user.fullName");
        String email = ConfigurationReader.get("test.user.email");
        String password = ConfigurationReader.get("test.user.password");
        String role = ConfigurationReader.get("test.user.role");
        String existingId = Users.findUserIdByEmail(cookie, email);
        if (existingId != null) {
            Response del = Users.deleteUserById(cookie, existingId);
            assertTrue(del.statusCode() >= 200 && del.statusCode() < 300,
                    "Предочистка: ожидали 2xx при удалении, получили " + del.statusCode());
        }

        Response create = Users.registerUser(cookie, email, fullName, password, role, true);
        assertEquals(201, create.statusCode(), "Ожидаем 201 при успешной регистрации");
        assertEquals(email, create.jsonPath().getString("email"));
        assertEquals(role, create.jsonPath().getString("role"));
        assertTrue(create.jsonPath().getBoolean("active"));
        assertNotNull(create.jsonPath().getString("id"), "id должен быть в ответе");
    }
}
