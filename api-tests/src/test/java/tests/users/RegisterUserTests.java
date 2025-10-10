package tests.users;

import dto.users.RegisterUserRequest;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API Tests")
@Owner("Oleksiy Korniyenko")
@DisplayName("Register user (Settings(API)")
@Tag("positive")
public class RegisterUserTests extends BaseTest {

    @BeforeEach
    void ensureAbsent() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }

    @Test
    @DisplayName("Users: register")
    void registerUser_returns201() {
        RegisterUserRequest req = new RegisterUserRequest();
        req.setEmail(get("test.user.email"));
        req.setFullName(get("test.user.fullName"));
        req.setPassword(get("test.user.password"));
        req.setRole(get("test.user.role"));
        req.setActive(true);

        var resp = Users.registerUser(cookie, req);

        assertEquals(201, resp.statusCode(), "Expected 201, body=" + resp.asString());
        assertEquals(req.getEmail(), resp.jsonPath().getString("email"));
        assertEquals(req.getRole(), resp.jsonPath().getString("role"));
        assertTrue(resp.jsonPath().getBoolean("active"));
        assertNotNull(resp.jsonPath().getString("id"));
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
