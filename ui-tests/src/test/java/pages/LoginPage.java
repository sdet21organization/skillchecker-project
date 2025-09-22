package pages;

import context.TestContext;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class LoginPage {
    TestContext context;

    public Locator usernameInput;
    public Locator passwordInput;
    public Locator loginButton;

    public LoginPage(TestContext context) {
        this.context = context;

        // Стабильные CSS-селекторы
        this.usernameInput = context.page.locator("input[name='email']");
        this.passwordInput = context.page.locator("input[name='password']");
        this.loginButton   = context.page.locator("button[type='submit']");
    }

    @Step("Login with {email} / {password}")
    public LoginPage login(String email, String password) {
        usernameInput.fill(email);
        passwordInput.fill(password);
        loginButton.click();
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