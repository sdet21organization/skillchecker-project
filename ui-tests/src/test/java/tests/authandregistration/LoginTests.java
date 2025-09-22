package tests.authandregistration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.components.Toast;
import tests.BaseTest;
import utils.ConfigurationReader;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Auth")
@Feature("Login")
@DisplayName("Login tests")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("SS-T30: Валидные email/пароль → редирект на /dashboard")
    void successfulLogin_redirectsToDashboard() {
        String base = ConfigurationReader.get("URL");
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        new LoginPage(context).open().login(email, password);
        context.page.waitForURL(base + "dashboard");

        assertEquals(base + "dashboard", context.page.url(),
                "После успешного логина ожидаем попасть на /dashboard");
    }

    @Test
    @DisplayName("SS-T32: Неверный пароль → toast 'Ошибка входа', остаёмся на логине")
    void wrongPassword_showsErrorToast() {
        String base = ConfigurationReader.get("URL");
        String email = ConfigurationReader.get("email");

        new LoginPage(context).open().login(email, "DefinitelyWrong#123");

        Toast toast = new Toast(context.page).waitOpen();
        assertEquals("Ошибка входа", toast.titleText());

        String body = toast.bodyText();
        assertTrue(body.contains("401") && body.contains("Invalid credentials"),
                "Ожидали '401' и 'Invalid credentials' в тексте тоста, получили: " + body);

        assertTrue(new LoginPage(context).isAtLoginPage(base),
                "Ожидали остаться на странице логина, сейчас: " + context.page.url());
    }
}