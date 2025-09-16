package pages;

import context.TestContext;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;

public class LoginPage {
    TestContext context;

    public Locator usernameInput;
    public Locator passwordInput;
    public Locator loginButton;

    public LoginPage(TestContext context){
        this.context = context;
        this.usernameInput = context.page.locator("input[name=\"email\"]");
        this.passwordInput = context.page.locator("input[name=\"password\"]");
        this.loginButton = context.page.locator("button[type=\"submit\"]");
    }

    @Step("Login with {email} and {password}")
    public LoginPage login(String email, String password){
        usernameInput.fill(email);
        passwordInput.fill(password);
        loginButton.click();
        return this;
    }
}
