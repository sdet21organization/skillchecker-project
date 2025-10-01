package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import context.TestContext;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class LoginPage {
    private final TestContext context;

    public final Locator usernameInput;
    public final Locator passwordInput;
    public final Locator loginButton;
    public final Locator emailError;
    public final Locator passwordError;

    public LoginPage(TestContext context) {
        this.context = context;
        this.usernameInput = context.page.locator("input[name='email']");
        this.passwordInput = context.page.locator("input[name='password']");
        this.loginButton   = context.page.locator("button[type='submit']");
        this.emailError    = context.page.locator("text=Некорректная электронная почта");
        this.passwordError = context.page.locator("text=Пароль обязателен");
    }

    public Locator loginButton() {
        return loginButton;
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
        String base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return url.equals(base) || url.startsWith(base + "login") || url.contains("/login?");
    }

    @Step("Открыть страницу логина")
    public LoginPage open() {
        context.page.navigate(ConfigurationReader.get("URL"));
        return this;
    }
}