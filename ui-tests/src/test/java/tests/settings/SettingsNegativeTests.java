package tests.settings;

import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.SettingsPage;
import tests.BaseTest;
import utils.ConfigurationReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("UI Tests")
@Owner("Oleksiy Korniyenko")
@DisplayName("Settings Negative Tests UI")
@Tag("negative")
public class SettingsNegativeTests extends BaseTest {

    private final String fullName = ConfigurationReader.get("test.user.fullName");
    private final String password = ConfigurationReader.get("test.user.password");

    @Test
    @DisplayName("Empty Email field when adding a user")
    void emptyEmailField() {
        SettingsPage p = new SettingsPage(context).open().openAddUserModal();

        p.fullNameInput.fill(fullName);
        p.emailInput.fill("");
        p.passwordInput.fill(password);
        p.createUserBtn.click();
        assertTrue(p.hasInlineError("Please enter a valid email address."));
    }

    @Test
    @DisplayName("Empty Full Name field when adding a user")
    void emptyFullNameField() {
        SettingsPage p = new SettingsPage(context).open().openAddUserModal();

        String email = ConfigurationReader.get("test.user.email");
        String password = ConfigurationReader.get("test.user.password");

        p.fullNameInput.fill("");
        p.emailInput.fill(email);
        p.passwordInput.fill(password);
        p.createUserBtn.click();
        assertTrue(p.hasInlineError("Full name must be at least 2 characters."));
    }

    @Test
    @DisplayName("Empty Password field when adding a user")
    void emptyPasswordField() {
        SettingsPage p = new SettingsPage(context).open().openAddUserModal();

        String fullName = ConfigurationReader.get("test.user.fullName");
        String email = ConfigurationReader.get("test.user.email");

        p.fullNameInput.fill(fullName);
        p.emailInput.fill(email);
        p.passwordInput.fill("");
        p.createUserBtn.click();
        assertTrue(p.hasInlineError("Password must be at least 8 characters."));
    }

    @Test
    @DisplayName("Cannot delete own user (Delete disabled)")
    void cannotDeleteOwnUser() {
        SettingsPage p = new SettingsPage(context).open();

        String ownEmail = ConfigurationReader.get("email");
        p.waitUserRowVisible(ownEmail);
        assertTrue(
                p.rowDeleteButton(ownEmail).isDisabled(),
                "Delete button for own user must be disabled"
        );
    }
}
