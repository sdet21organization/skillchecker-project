package tests.authandregistration;

import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import pages.DashboardPage;
import pages.LoginPage;
import pages.components.Toast;
import tests.BaseTest;
import utils.ConfigurationReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("UI Tests")
@Feature("Auth")
@Owner("Ko.Herasymets")
@Tag("ui")
public class LoginTests extends BaseTest {
    @Override
    protected boolean needAuthCookie() { return false; }

    private String baseUrl() {
        String base = ConfigurationReader.get("URL");
        return base.endsWith("/") ? base : base + "/";
    }

    @Test
    @TmsLink("SS-T30")
    @Story("SS-T30 Успешный вход")
    @DisplayName("[SS-T30] Login: успешная авторизация валидными данными")
    void shouldLoginSuccessfully_SS_T30() {
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        new LoginPage(context).open().login(email, password);
        new DashboardPage(context).waitUntilReady();

        boolean onDashboard = context.page.url().endsWith("/dashboard");
        boolean toastOk = new Toast(context.page).waitAppear(8000)
                .containsAny("Успешный вход", "Добро пожаловать", "System Administrator");

        assertTrue(onDashboard || toastOk, "После логина не увидели Dashboard или приветственный тост");
    }

    @Test
    @TmsLink("SS-T31")
    @Story("SS-T31 Неверный логин")
    @DisplayName("[SS-T31] Login: неверный email → ошибка авторизации")
    void shouldShowErrorOnWrongEmail_SS_T31() {
        String wrongEmail = "not-exists+" + System.currentTimeMillis() + "@example.com";
        String password = ConfigurationReader.get("password");
        assertInvalidCredentials(wrongEmail, password);
    }

    @Test
    @TmsLink("SS-T32")
    @Story("SS-T32 Неверный пароль")
    @DisplayName("[SS-T32] Login: неверный пароль → ошибка авторизации")
    void shouldShowErrorOnWrongPassword_SS_T32() {
        String email = ConfigurationReader.get("email");
        String wrongPassword = "wrongPass!" + System.currentTimeMillis();
        assertInvalidCredentials(email, wrongPassword);
    }

    @Test
    @TmsLink("SS-T62")
    @Story("SS-T62 Валидации пустых полей")
    @DisplayName("[SS-T62] Login: ошибки валидации при пустых/частично заполненных полях")
    void shouldShowValidationErrorsOnEmptyFields_SS_T62() {
        LoginPage login = new LoginPage(context).open();

        login.login("", "");
        assertTrue(login.emailError.isVisible());
        assertTrue(login.passwordError.isVisible());

        login.login("", ConfigurationReader.get("password"));
        assertTrue(login.emailError.isVisible());
        assertTrue(!login.passwordError.isVisible());

        login.login(ConfigurationReader.get("email"), "");
        assertTrue(login.passwordError.isVisible());
        assertTrue(!login.emailError.isVisible());
    }

    @Test
    @TmsLink("SS-T66")
    @Story("SS-T66 Доступ к защищённой странице без авторизации")
    @DisplayName("[SS-T66] Access: переход на /dashboard без логина ведёт на /login")
    void shouldRedirectToLoginWhenOpenDashboardWithoutAuth_SS_T66() {
        context.page.navigate(baseUrl() + "dashboard");
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(new LoginPage(context).isAtLoginPage(baseUrl()));
    }

    @Test
    @TmsLink("SS-T69")
    @Story("SS-T69 Logout и защита после выхода")
    @DisplayName("[SS-T69] Logout: после выхода /dashboard недоступен (редирект на /login)")
    void shouldDenyDashboardAfterLogout_SS_T69() {
        new LoginPage(context).open().login(ConfigurationReader.get("email"), ConfigurationReader.get("password"));
        new DashboardPage(context).waitUntilReady().logout();

        assertTrue(new LoginPage(context).isAtLoginPage(baseUrl()));

        assertTrue(
                new Toast(context.page).waitAppear(7000)
                        .containsAny("Выход выполнен", "Вы вышли из системы", "Signed out"),
                "Ожидали тост об успешном выходе"
        );

        context.page.navigate(baseUrl() + "dashboard");
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(new LoginPage(context).isAtLoginPage(baseUrl()));
    }

    private void assertInvalidCredentials(String email, String password) {
        new LoginPage(context).open().login(email, password);
        Toast toast = new Toast(context.page).waitAppear(8000);
        String title = toast.titleText();
        String body = toast.bodyText();

        boolean looksLikeInvalid =
                containsAny(title, "Ошибка входа", "Неверные", "Invalid") ||
                        containsAny(body, "Ошибка входа", "Неверные", "Invalid credentials", "401");

        assertTrue(looksLikeInvalid,
                "Ожидали тост об ошибке авторизации. title=\"" + title + "\", body=\"" + body + "\"");
    }

    private boolean containsAny(String text, String... needles) {
        String t = text == null ? "" : text.toLowerCase();
        for (String n : needles) if (t.contains(n.toLowerCase())) return true;
        return false;
    }
}