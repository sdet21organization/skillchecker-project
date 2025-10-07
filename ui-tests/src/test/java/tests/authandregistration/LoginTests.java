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
@Feature("Auth / Login")
@Owner("Ko.Herasymets")
@Tag("ui")
@DisplayName("Auth/Login UI — Login & Logout / Авторизация и выход")
@Disabled("Disabled until app stabilization")
public class LoginTests extends BaseTest {
    @Override
    protected boolean needAuthCookie() { return false; }

    private String validEmail;
    private String validPassword;

    @BeforeEach
    void initCreds() {
        validEmail = ConfigurationReader.get("email");
        validPassword = ConfigurationReader.get("password");
    }

    private String baseUrl() {
        String base = ConfigurationReader.get("URL");
        return base.endsWith("/") ? base : base + "/";
    }

    private LoginPage openLogin() {
        return new LoginPage(context).open();
    }

    @Test
    @Story("Успешный вход")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("SS-T30 | [POS] Auth/Login — Successful login / Успешная авторизация")
    void shouldLoginSuccessfully_SS_T30() {
        openLogin().login(validEmail, validPassword);
        new DashboardPage(context).waitUntilReady();

        boolean onDashboard = context.page.url().endsWith("/dashboard");
        boolean toastOk = new Toast(context.page).waitAppear(8000)
                .containsAny("Успешный вход", "Добро пожаловать", "System Administrator");

        assertTrue(onDashboard || toastOk, "После логина не увидели Dashboard или приветственный тост");
    }

    @Test
    @Story("Неверный email")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("SS-T31 | [NEG] Auth/Login — Invalid email → auth error / Неверный email → ошибка авторизации")
    void shouldShowErrorOnWrongEmail_SS_T31() {
        String wrongEmail = "notexists" + System.currentTimeMillis() + "@gmail.com";
        assertInvalidCredentials(wrongEmail, validPassword);
    }

    @Test
    @Story("Неверный пароль")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("SS-T32 | [NEG] Auth/Login — Invalid password → auth error / Неверный пароль → ошибка авторизации")
    void shouldShowErrorOnWrongPassword_SS_T32() {
        String wrongPassword = "wrongPass!" + System.currentTimeMillis();
        assertInvalidCredentials(validEmail, wrongPassword);
    }

    @Test
    @Story("Ошибки валидации при пустых/частично заполненных полях")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("SS-T62 | [NEG] Auth/Login — Validation errors on empty/partial fields / Ошибки при пустых/частичных полях")
    void shouldShowValidationErrorsOnEmptyFields_SS_T62() {
        LoginPage login = openLogin();

        login.login("", "");
        assertTrue(login.emailError.isVisible());
        assertTrue(login.passwordError.isVisible());

        login.login("", validPassword);
        assertTrue(login.emailError.isVisible());
        assertTrue(!login.passwordError.isVisible());

        login.login(validEmail, "");
        assertTrue(login.passwordError.isVisible());
        assertTrue(!login.emailError.isVisible());
    }

    @Test
    @Story("Доступ к /dashboard без авторизации")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("SS-T66 | [ACC] Auth — Dashboard without login redirects to /login / Доступ к Dashboard без логина → редирект на /login")
    void shouldRedirectToLoginWhenOpenDashboardWithoutAuth_SS_T66() {
        context.page.navigate(baseUrl() + "dashboard");
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(new LoginPage(context).isAtLoginPage(baseUrl()));
    }

    @Test
    @Story("Logout: защита после выхода")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("SS-T69 | [ACC] Auth/Logout — Dashboard denied after logout / После выхода Dashboard недоступен")
    void shouldDenyDashboardAfterLogout_SS_T69() {
        openLogin().login(validEmail, validPassword);
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
        openLogin().login(email, password);
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