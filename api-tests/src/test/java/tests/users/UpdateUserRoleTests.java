package tests.users;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static helpers.ConfigurationReader.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateUserRoleTests extends BaseTest {

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
    @DisplayName("Users: update role")
    void updateRole_returns200() {
        String newRole = get("test.user.roleChangeTo");
        var patch = Users.updateUserRole(cookie, id, newRole);
        assertEquals(200, patch.statusCode(), "Expected 200, body=" + patch.asString());
        assertEquals(newRole, patch.jsonPath().getString("role"));
    }

    @AfterEach
    void cleanup() {
        Users.deleteByEmailIfExists(cookie, get("test.user.email"));
    }
}
