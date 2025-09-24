package tests.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.SettingsPage;
import tests.BaseTest;
import utils.ConfigurationReader;

public class SettingsTests extends BaseTest {

    @Test
    @DisplayName("Create user")
    void createUser() {
        SettingsPage p = new SettingsPage(context).open();
        String email = ConfigurationReader.get("test.user.email");
        try {
            p.deleteUserIfExists(email);
            p.createUser();
            p.waitUserRowVisible(email);
            Assertions.assertTrue(p.rowByEmail(email).isVisible(), "User row not visible after creation");
        } finally {
            p.deleteUserIfExists(email);
        }
    }

    @Test
    @DisplayName("Change role")
    void changeRole() {
        SettingsPage p = new SettingsPage(context).open();
        String email = ConfigurationReader.get("test.user.email");
        String targetRole = ConfigurationReader.get("test.user.roleChangeTo");
        try {
            if (p.rowByEmail(email).count() == 0) p.createUser();
            p.selectRowRole(email, targetRole);
            p.waitRowRoleEquals(email, targetRole);
        } finally {
            p.deleteUserIfExists(email);
        }
    }

    @Test
    @DisplayName("Reset password")
    void resetPassword() {
        SettingsPage p = new SettingsPage(context).open();
        String email = ConfigurationReader.get("test.user.email");
        String newPass = ConfigurationReader.get("test.user.newPassword");
        try {
            if (p.rowByEmail(email).count() == 0) p.createUser();
            p.resetPasswordFor(email, newPass);
            p.waitToast("Пароль сброшен");
        } finally {
            p.deleteUserIfExists(email);
        }
    }

    @Test
    @DisplayName("Block / Activate user")
    void blockActivate() {
        SettingsPage p = new SettingsPage(context).open();
        String email = ConfigurationReader.get("test.user.email");
        try {
            if (p.rowByEmail(email).count() == 0) p.createUser();
            p.rowToggleStatusButton(email).click();
            p.waitRowToggleText(email, "Активировать");
            p.rowToggleStatusButton(email).click();
            p.waitRowToggleText(email, "Блокировать");
        } finally {
            p.deleteUserIfExists(email);
        }
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() {
        SettingsPage p = new SettingsPage(context).open();
        String email = ConfigurationReader.get("test.user.email");
        try {
            if (p.rowByEmail(email).count() == 0) p.createUser();
            p.rowDeleteButton(email).click();
            p.confirmDeleteInDialog();
            p.waitUserRowAbsent(email);
            Assertions.assertEquals(0, p.rowByEmail(email).count(), "User row still present after deletion");
        } finally {
            p.deleteUserIfExists(email);
        }
    }
}
