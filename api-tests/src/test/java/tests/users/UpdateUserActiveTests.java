package tests.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.*;

@Epic("API Tests")
@Owner("Oleksiy Korniyenko")
@DisplayName("Block & unblock user (Settings(API)")
@Tag("positive")
public class UpdateUserActiveTests extends BaseTest {

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
    @DisplayName("Users: block & unblock")
    void block_then_unblock_returns200() {
        var block = Users.updateUserActive(cookie, id, false);
        assertEquals(200, block.statusCode(), "Expected 200, body=" + block.asString());
        assertFalse(block.jsonPath().getBoolean("active"));

        var unb = Users.updateUserActive(cookie, id, true);
        assertEquals(200, unb.statusCode(), "Expected 200, body=" + unb.asString());
        assertTrue(unb.jsonPath().getBoolean("active"));
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
