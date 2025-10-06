package tests.users;

import helpers.ConfigurationReader;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Users;

import static org.junit.jupiter.api.Assertions.*;

@Epic("API Tests")
@Owner("Oleksiy Korniyenko")
@Tag("negative")
@DisplayName("Users Negative API Tests")

public class UsersNegativeApiTests extends BaseTest {

    @Test
    @DisplayName("Cannot delete own user")
    void cannotDeleteOwnUser() {
        String ownEmail = ConfigurationReader.get("email");
        String ownId = Users.findUserIdByEmail(cookie, ownEmail);
        assertNotNull(ownId, "Own user id not found for email: " + ownEmail);

        Response r = Users.deleteUserById(cookie, ownId);

        assertEquals(400, r.statusCode(), "Unexpected status: " + r.statusCode());
        assertTrue(r.asString().contains("You cannot delete yourself"),
                "Unexpected body: " + r.asString());
    }

    @Test
    @DisplayName("Invalid password when resetting own password")
    void invalidPasswordOnOwnReset() {
        String ownEmail = ConfigurationReader.get("email");
        String invalid = ConfigurationReader.get("test.user.invalidPassword");
        String ownId = Users.findUserIdByEmail(cookie, ownEmail);

        Response r = Users.resetUserPassword(cookie, ownId, invalid);

        assertEquals(400, r.statusCode());
        assertTrue(r.asString().contains("Password must be at least 8 characters"));
    }

    @Test
    @DisplayName("Cannot change own role")
    void cannotChangeOwnRole() {
        String ownEmail = ConfigurationReader.get("email");
        String ownId = Users.findUserIdByEmail(cookie, ownEmail);

        String targetRole = ConfigurationReader.get("test.user.roleChangeTo");
        Response r = Users.updateUserRole(cookie, ownId, targetRole);

        assertEquals(400, r.statusCode());
        assertTrue(r.asString().contains("Cannot update your own account"));
    }

    @Test
    @DisplayName("Cannot block own user")
    void cannotBlockOwnUser() {
        String ownEmail = ConfigurationReader.get("email");
        String ownId = Users.findUserIdByEmail(cookie, ownEmail);

        Response r = Users.updateUserActive(cookie, ownId, false);

        assertEquals(400, r.statusCode());
        assertTrue(r.asString().contains("Cannot update your own account"));
    }
}
