package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class SettingsPage {
    public final TestContext context;

    public final Locator addUserButton;
    public final Locator fullNameInput;
    public final Locator emailInput;
    public final Locator passwordInput;
    public final Locator roleCombobox;
    public final Locator createUserBtn;


    public SettingsPage(TestContext context) {
        this.context = context;
        Page page = context.page;
        this.addUserButton = page.locator("[data-testid='add-user-button']");
        this.emailInput = page.locator("[data-testid='user-email-input']");
        this.fullNameInput = page.locator("[data-testid='user-fullname-input']");
        this.passwordInput = page.locator("[data-testid='user-password-input']");
        this.roleCombobox = page.locator("form:has(input[name='email']) button[role='combobox']");
        this.createUserBtn = page.locator("[data-testid='create-user-submit-button']");

    }

    @Step("Open Settings")
    public SettingsPage open() {
        context.page.navigate(ConfigurationReader.get("URL") + "dashboard/settings");
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    @Step("Open Add-User modal")
    private SettingsPage openAddUserModal() {
        addUserButton.click();
        return this;
    }

    @Step("Wait Add-User email visible")
    private void waitAddUserEmailInputVisible() {
        emailInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Step("Click Create User")
    private void clickCreateUser() {
        createUserBtn.click();
    }

    @Step("Click Cancel in Add-User modal")
    private void clickCancelCreateUser() {
        context.page.locator("[data-testid='create-user-cancel-button']").click();
    }

    private String toUiRoleLabel(String key) {
        switch (key.toLowerCase()) {
            case "recruiter":
                return "Рекрутер";
            case "interviewer":
                return "Интервьюер";
            case "admin":
                return "Администратор";
            default:
                return key;
        }
    }

    @Step("Select role by key: {roleKey}")
    private SettingsPage selectAddUserRoleByKey(String roleKey) {
        return selectAddUserRole(toUiRoleLabel(roleKey));
    }

    @Step("Select role: {roleName}")
    private SettingsPage selectAddUserRole(String roleName) {
        roleCombobox.click();
        context.page.getByRole(AriaRole.OPTION,
                new Page.GetByRoleOptions().setName(roleName)).click();
        return this;
    }

    public Locator rowByEmail(String email) {
        return context.page.locator("tr")
                .filter(new Locator.FilterOptions().setHasText(email))
                .first();
    }

    @Step("Wait user row: {email}")
    public void waitUserRowVisible(String email) {
        rowByEmail(email).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Step("Wait user row absent: {email}")
    public void waitUserRowAbsent(String email) {
        rowByEmail(email).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED));
    }

    @Step("Wait toast: {text}")
    public void waitToast(String text) {
        context.page
                .getByText(text, new Page.GetByTextOptions().setExact(true))
                .first()
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Step("Set row role: {roleNameOrKey} ({email})")
    public void selectRowRole(String email, String roleNameOrKey) {
        String label = toUiRoleLabel(roleNameOrKey);
        rowByEmail(email).locator("button[role='combobox']").first().click();
        context.page.getByRole(AriaRole.OPTION,
                new Page.GetByRoleOptions().setName(label)).click();
    }

    @Step("Wait row role: {roleNameOrKey} ({email})")
    public void waitRowRoleEquals(String email, String roleNameOrKey) {
        String label = toUiRoleLabel(roleNameOrKey);
        rowByEmail(email)
                .getByText(label, new Locator.GetByTextOptions().setExact(true))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Step("Row toggle-status button ({email})")
    public Locator rowToggleStatusButton(String email) {
        return rowByEmail(email).locator("[data-testid^='toggle-user-status-button']").first();
    }

    @Step("Wait toggle text: {text} ({email})")
    public void waitRowToggleText(String email, String text) {
        rowToggleStatusButton(email)
                .getByText(text, new Locator.GetByTextOptions().setExact(true))
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Step("Row reset button ({email})")
    private Locator rowResetPasswordButton(String email) {
        return rowByEmail(email).locator("[data-testid^='reset-password-button']").first();
    }

    @Step("Reset password (prompt): {email}")
    public void resetPasswordFor(String email, String newPassword) {
        context.page.onceDialog(d -> d.accept(newPassword)); // ввести пароль и ОК
        rowResetPasswordButton(email).click();
    }

    @Step("Row delete button ({email})")
    public Locator rowDeleteButton(String email) {
        return rowByEmail(email).locator("[data-testid^='delete-user-button']").first();
    }

    @Step("Confirm delete in dialog")
    public void confirmDeleteInDialog() {
        Locator confirm = context.page.locator("[data-testid^='confirm-delete-user-button']").first();
        confirm.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        confirm.click();
    }

    private void doCreateUser(String fullName, String email, String password, String roleKey) {
        openAddUserModal();
        waitAddUserEmailInputVisible();
        fullNameInput.fill(fullName);
        emailInput.fill(email);
        passwordInput.fill(password);
        selectAddUserRoleByKey(roleKey);
        clickCreateUser();
        waitToast("Пользователь создан");
        clickCancelCreateUser();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        waitUserRowVisible(email);
    }

    @Step("Create user (from config)")
    public void createUser() {
        String fullName = ConfigurationReader.get("test.user.fullName");
        String email = ConfigurationReader.get("test.user.email");
        String password = ConfigurationReader.get("test.user.password");
        String roleKey = ConfigurationReader.get("test.user.role");
        doCreateUser(fullName, email, password, roleKey);
    }

    @Step("Delete user if exists: {email}")
    public void deleteUserIfExists(String email) {
        if (rowByEmail(email).count() == 0) return;
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        rowDeleteButton(email).click();
        confirmDeleteInDialog();
        waitUserRowAbsent(email);
    }
}
