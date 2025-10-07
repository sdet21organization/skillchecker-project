package tests.settings;

import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;
import pages.SettingsPage;
import tests.BaseTest;
import utils.ConfigurationReader;

@Epic("UI Tests")
@Owner("Oleksiy Korniyenko")
@DisplayName("Settings tests UI")
@Tag("positive")
public class SettingsTests extends BaseTest {

    private final String email = ConfigurationReader.get("test.user.email");

    @Test
    @DisplayName("Create user")
    void createUser() {
        SettingsPage p = new SettingsPage(context).open();
        p.deleteUserIfExists(email);
        p.createUser();
        p.waitUserRowVisible(email);
        Assertions.assertTrue(p.rowByEmail(email).isVisible(), "User row not visible after creation");
    }

    @Test
    @DisplayName("Change role")
    void changeRole() {
        SettingsPage p = new SettingsPage(context).open();
        if (p.rowByEmail(email).count() == 0) p.createUser();
        String targetRole = ConfigurationReader.get("test.user.roleChangeTo");
        p.selectRowRole(email, targetRole);
        p.waitRowRoleEquals(email, targetRole);
    }

    @Test
    @DisplayName("Reset password")
    void resetPassword() {
        SettingsPage p = new SettingsPage(context).open();
        if (p.rowByEmail(email).count() == 0) p.createUser();
        String newPass = ConfigurationReader.get("test.user.newPassword");
        p.resetPasswordFor(email, newPass);
        p.waitToast("Пароль сброшен");
    }

    @Test
    @DisplayName("Block / Activate user")
    void blockActivate() {
        SettingsPage p = new SettingsPage(context).open();
        if (p.rowByEmail(email).count() == 0) p.createUser();
        p.rowToggleStatusButton(email).click();
        p.waitRowToggleText(email, "Активировать");
        p.rowToggleStatusButton(email).click();
        p.waitRowToggleText(email, "Блокировать");
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() {
        SettingsPage p = new SettingsPage(context).open();
        if (p.rowByEmail(email).count() == 0) p.createUser();
        p.rowDeleteButton(email).click();
        p.confirmDeleteInDialog();
        p.waitUserRowAbsent(email);
        Assertions.assertEquals(0, p.rowByEmail(email).count(), "User row still present after deletion");
    }

    @AfterEach
    void cleanupUser() {
        SettingsPage p = new SettingsPage(context).open();
        p.deleteUserIfExists(email);
    }
}
