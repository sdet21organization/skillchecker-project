package tests.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Delete user (Settings(API))")
public class DeleteUserTests extends BaseTest {

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
    @DisplayName("Users: delete")
    void deleteUser_returns200() {
        var del = Users.deleteUserById(cookie, id);
        assertEquals(200, del.statusCode(), "Expected 200, body=" + del.asString());
        assertEquals("User deleted", del.jsonPath().getString("message"));
        assertNull(Users.findUserIdByEmail(cookie, get("test.user.email")));
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
