package pages;

import com.microsoft.playwright.options.LoadState;
import context.TestContext;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class LoginPage {
    TestContext context;

    public Locator usernameInput;
    public Locator passwordInput;
    public Locator loginButton;
    public Locator emailError;
    public Locator passwordError;

    public LoginPage(TestContext context) {
        this.context = context;
        this.usernameInput = context.page.locator("input[name='email']");
        this.passwordInput = context.page.locator("input[name='password']");
        this.loginButton   = context.page.locator("button[type='submit']");
        this.emailError    = context.page.locator("text=Некорректная электронная почта");
        this.passwordError = context.page.locator("text=Пароль обязателен");
    }

    @Step("Login with {email} / {password}")
    public LoginPage login(String email, String password) {
        usernameInput.fill(email);
        passwordInput.fill(password);
        loginButton.click();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    @Step("Проверяем, что открыта страница логина")
    public boolean isAtLoginPage(String baseUrl) {
        String url = context.page.url();
        return url.equals(baseUrl) || url.endsWith("/login");
    }

    @Step("Открыть страницу логина")
    public LoginPage open() {
        context.page.navigate(ConfigurationReader.get("URL"));
        return this;
    }
}