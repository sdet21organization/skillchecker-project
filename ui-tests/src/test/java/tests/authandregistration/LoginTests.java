package tests.authandregistration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.components.Toast;
import tests.BaseTest;
import utils.ConfigurationReader;

import static org.junit.jupiter.api.Assertions.*;
@Disabled("Временно отключаем все тесты класса")
@Epic("Auth")
@Feature("Login")
@DisplayName("Login tests")

public class LoginTests extends BaseTest {

    @Override
    protected boolean needAuthCookie() {
        return false;
    }

    @Test
    @DisplayName("SS-T30: Валидные email/пароль → редирект на /dashboard")
    void successfulLogin_redirectsToDashboard() {
        String base = ConfigurationReader.get("URL");
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        new LoginPage(context).open().login(email, password);
        context.page.waitForURL(base + "dashboard");

        assertEquals(base + "dashboard", context.page.url());
    }

    @Test
    @DisplayName("SS-T31: Неверный логин → toast 'Ошибка входа'")
    void wrongLogin_showsErrorToast() {
        String base = ConfigurationReader.get("URL");

        new LoginPage(context).open().login("not.exist.user@example.com", "AnyPassword#1");

        Toast toast = new Toast(context.page).waitOpen();
        assertEquals("Ошибка входа", toast.titleText());
        assertTrue(new LoginPage(context).isAtLoginPage(base));
    }

    @Test
    @DisplayName("SS-T32: Неверный пароль → toast 'Ошибка входа'")
    void wrongPassword_showsErrorToast() {
        String base = ConfigurationReader.get("URL");
        String email = ConfigurationReader.get("email");

        new LoginPage(context).open().login(email, "DefinitelyWrong#123");

        Toast toast = new Toast(context.page).waitOpen();
        assertEquals("Ошибка входа", toast.titleText());
        assertTrue(toast.bodyText().contains("401"));
        assertTrue(new LoginPage(context).isAtLoginPage(base));
    }

    @Test
    @DisplayName("SS-T62: Пустые поля при логине → обязательные поля")
    void emptyFields_showRequiredErrors() {
        LoginPage loginPage = new LoginPage(context).open();
        loginPage.login("", "");
        assertTrue(loginPage.emailError.isVisible());
        assertTrue(loginPage.passwordError.isVisible());
    }

    @Test
    @DisplayName("SS-T63: Невалидный email → ошибка формата")
    void invalidEmail_showsFormatError() {
        LoginPage loginPage = new LoginPage(context).open();
        loginPage.login("test@", "SomePassword#1");
        assertTrue(loginPage.emailError.isVisible());
    }
}