package pages;

import context.TestContext;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class LoginPage {
    private final TestContext context;

    public final Locator usernameInput;
    public final Locator passwordInput;
    public final Locator loginButton;

    public LoginPage(TestContext context){
        this.context = context;
        // если есть data-testid — лучше так:
        // this.usernameInput = context.page.getByTestId("login-email");
        // this.passwordInput = context.page.getByTestId("login-password");
        // this.loginButton   = context.page.getByTestId("login-submit");

        // Фоллбэк на стабильные CSS:
        this.usernameInput = context.page.locator("input[name='email']");
        this.passwordInput = context.page.locator("input[name='password']");
        this.loginButton   = context.page.locator("button[type='submit']");
    }

    @Step("Login with {email} / {password}")
    public LoginPage login(String email, String password){
        usernameInput.fill(email);
        passwordInput.fill(password);
        loginButton.click();
        return this;
    }

    /** Проверяем, что мы на странице логина */
    public boolean isAtLoginPage(String baseUrl) {
        String url = context.page.url();
        return url.equals(baseUrl) || url.endsWith("/login");
    }

    /** Удобно: перейти на / */
    public LoginPage open() {
        context.page.navigate(ConfigurationReader.get("URL"));
        return this;
    }
}