package tests.authandregistration;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.google.gson.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.DashboardPage;
import pages.RegistrationPage;
import pages.components.Toast;
import tests.BaseUiTest;
import utils.VerificationCodeClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Epic("UI Tests")
@Feature("Auth / Registration")
@Owner("Ko.Herasymets")
@Tag("ui")
@DisplayName("Auth/Registration UI — Positive & Negative / Позитив и негатив")
@Disabled("Disabled until app stabilization")
public class RegistrationTests extends BaseUiTest {

    private static final boolean RUN_KNOWN_BUGS = Boolean.getBoolean("run.known.bugs");

    @Override
    protected boolean needAuthCookie() {
        return false;
    }

    static Stream<Arguments> invalidFullNames() {
        return Stream.of(
                Arguments.of("", "пусто"),
                Arguments.of("a", "1 латиница"),
                Arguments.of("т", "1 кириллица"),
                Arguments.of("        ", "только пробелы"),
                Arguments.of(" a", "trim→1 слева"),
                Arguments.of("a ", "trim→1 справа"),
                Arguments.of("\u00A0", "NBSP (U+00A0)"),
                Arguments.of("1", "1 цифра")
        );
    }

    static Stream<Arguments> invalidOrganizations() {
        return Stream.of(
                Arguments.of("", "пусто"),
                Arguments.of("a", "1 символ"),
                Arguments.of("т", "1 кириллица"),
                Arguments.of("        ", "только пробелы"),
                Arguments.of(" a", "trim→1 слева"),
                Arguments.of("a ", "trim→1 справа")
        );
    }

    static Stream<Arguments> invalidEmailsFromJson() {
        return loadInvalidEmails();
    }

    static Stream<Arguments> invalidPasswordsFromJson() {
        return loadInvalidPasswords();
    }

    @Test
    @Story("Организация: допустимы цифры и спецсимволы")
    @DisplayName("SS-T84 | [POS] Registration — Org name allows digits & specials / Название организации допускает цифры и спецсимволы")
    void t84_orgAllowsDigitsAndSpecials() {
        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmailPassword(uniqueEmail());

        String[] valid = {"Acme-2025", "DevOps", "Test_Inc", "a b", "Projects (01)"};

        for (String v : valid) {
            reg.setOrganizationAndBlur(v);
            boolean looksInvalid = reg.looksInvalid(reg.orgInput(), reg.orgError);
            assertFalse(reg.orgError.isVisible() || looksInvalid, "Поле 'Название организации' помечено ошибкой для значения: " + v);
        }

        reg.setOrganization(valid[0]).submit();

        boolean codeModal = reg.isCodeModalOpen();
        Toast toast = new Toast(context.page).waitAppear(10_000);
        boolean toastOk = toast.containsAny("Код отправлен", "Письмо с кодом", "код отправлен", "Email с кодом");

        assertTrue(codeModal || toastOk, "Ожидали тост «Код отправлен» или модалку подтверждения. Последний тост: \"" + (toast.titleText() + " " + toast.bodyText()).trim() + "\"");
    }

    @Test
    @Story("Успешная регистрация нового пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("SS-T11 | [POS] Registration — Successful new user registration / Успешная регистрация нового пользователя")
    void shouldRegisterNewUser_SS_T11() {
        String email = uniqueEmail();

        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmailPassword(email)
                .setOrganization(DEF_ORG)
                .submit();

        Toast sent = new Toast(context.page).waitAppear(10_000);
        assertTrue(reg.isCodeModalOpen() || sent.containsAny("Код отправлен", "Письмо с кодом", "код отправлен", "Email с кодом"));

        String code = getVerificationCode(email);
        Assumptions.assumeTrue(code != null && code.matches("\\d{6}"));

        reg.enterVerificationCode(code).confirmVerification();

        Toast toast = new Toast(context.page).waitAppear(15_000);
        boolean successToast = toast.containsAny(
                "Регистрация успешно завершена",
                "Регистрация успешна",
                "email успешно подтвержден",
                "Добро пожаловать",
                "Выполняется автоматический вход",
                "Успешно"
        );

        try {
            context.page.waitForURL(Pattern.compile(".*/dashboard.*"), new com.microsoft.playwright.Page.WaitForURLOptions().setTimeout(20000));
        } catch (Throwable ignored) {
        }

        boolean onDashboard = context.page.url().contains("/dashboard");
        boolean cabinetNav = isLoggedInUI();

        assertTrue(successToast && (onDashboard || cabinetNav), "Ожидали успешное подтверждение и переход на Dashboard/кабинет. Тост: \"" + (toast.titleText() + " " + toast.bodyText()).trim() + "\" URL: " + context.page.url());
    }

    @Test
    @Story("E-mail уже зарегистрирован")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("SS-T14 | [NEG] Registration — E-mail already registered (error after code) / E-mail уже зарегистрирован — ошибка после кода")
    void shouldRejectAlreadyRegisteredEmailAfterCode_SS_T14() {
        String email = uniqueEmail();

        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmailPassword(email)
                .setOrganization(DEF_ORG)
                .submit();

        boolean started1 = reg.isCodeModalOpen() || new Toast(context.page).waitAppear(10_000).containsAny("Код отправлен", "Письмо с кодом", "Email с кодом");
        assertTrue(started1);

        String code1 = getVerificationCode(email);
        Assumptions.assumeTrue(code1 != null && code1.matches("\\d{6}"));

        reg.enterVerificationCode(code1).confirmVerification();
        new Toast(context.page).waitAppear(15_000);
        new DashboardPage(context).waitUntilReady().logout();

        RegistrationPage reg2 = new RegistrationPage(context).open()
                .setFullName("Ivan Testov")
                .setEmail(email)
                .setOrganization(DEF_ORG)
                .setPassword(DEF_PWD)
                .submit();

        boolean started2 = reg2.isCodeModalOpen() || new Toast(context.page).waitAppear(10_000).containsAny("Код отправлен", "Письмо с кодом", "Email с кодом");
        assertTrue(started2);

        String code2 = getVerificationCode(email);
        if (code2 == null || !code2.matches("\\d{6}")) code2 = code1;

        reg2.enterVerificationCode(code2).confirmVerification();

        Toast err = new Toast(context.page).waitAppear(10_000);
        String title = err.titleText();
        String body = err.bodyText();
        boolean titleOk = containsAny(title, "Ошибка регистрации");
        boolean bodyOk = containsAny(body, "Email уже используется", "уже зарегистрирован");
        assertTrue(titleOk && bodyOk);

        assertFalse(context.page.url().contains("/dashboard"));
        assertTrue(reg2.isCodeModalOpen() || reg2.isModalOpen());
    }

    @Test
    @Story("Неверный/просроченный код подтверждения")
    @DisplayName("SS-T83 | [NEG] Registration — Invalid/expired code keeps modal open / Неверный или просроченный код — модалка остаётся открытой")
    void shouldShowErrorOnInvalidOrExpiredCode_SS_T83() {
        String email = uniqueEmail();

        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmailPassword(email)
                .setOrganization(DEF_ORG)
                .submit();

        boolean codeModal = reg.isCodeModalOpen() || new Toast(context.page).waitAppear(10_000).containsAny("Код отправлен", "Письмо с кодом", "Email с кодом");
        assertTrue(codeModal);

        reg.enterVerificationCode("111111").confirmVerification();

        Toast toast = new Toast(context.page).waitAppear(10_000);
        boolean errorToast = toast.containsAny(
                "Неверный или просроченный код подтверждения",
                "Неверный код",
                "просроченный код",
                "invalid or expired"
        );
        assertTrue(errorToast);

        assertTrue(reg.isCodeModalOpen());
        assertFalse(context.page.url().contains("/dashboard"));

        Locator dialog = context.page.locator("div[role='dialog']").first();

        Pattern resendRe = Pattern.compile("(Отправить\\s+код\\s+повторно|Повторн(ая|о).{0,15}отправк|выслать.{0,20}код|получить.{0,20}код.{0,10}(повтор|снова|ещ[её])|Resend(\\s+code)?|Send\\s+(again|new\\s+code)|Request\\s+new\\s+code)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern timerRe = Pattern.compile("(через\\s*\\d+\\s*(с|сек(унд)?|мин(ут)?|m(in)?)|\\b\\d{1,2}:\\d{2}\\b|resend\\s*in\\s*\\d+|\\(\\d{1,2}:\\d{2}\\))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern helpRe = Pattern.compile("(не\\s*при(ш|щ)е?л.*код|я\\s*не\\s*получил(а)?.*код|didn'?t\\s*get.*code|no\\s*code)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern confirmRe = Pattern.compile("(подтвер|verify|confirm)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        boolean available = false;
        for (int i = 0; i < 40 && !available; i++) {
            Locator resendTextInDialog = dialog.getByText(resendRe).first();
            Locator resendBtnInDialog = dialog.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(resendRe)).first();
            Locator timerUiInDialog = dialog.getByText(timerRe).first();
            Locator helpUiInDialog = dialog.getByText(helpRe).first();

            if ((resendTextInDialog.count() > 0 && resendTextInDialog.isVisible())
                    || (resendBtnInDialog.count() > 0 && resendBtnInDialog.isVisible())
                    || (timerUiInDialog.count() > 0 && timerUiInDialog.isVisible())
                    || (helpUiInDialog.count() > 0 && helpUiInDialog.isVisible())) {
                available = true;
            }

            if (!available) {
                Locator globalResend = context.page.getByText(resendRe).first();
                Locator globalTimer = context.page.getByText(timerRe).first();
                Locator globalHelp = context.page.getByText(helpRe).first();
                if ((globalResend.count() > 0 && globalResend.isVisible())
                        || (globalTimer.count() > 0 && globalTimer.isVisible())
                        || (globalHelp.count() > 0 && globalHelp.isVisible())) {
                    available = true;
                }
            }

            if (!available) {
                Locator confirmBtn = dialog.getByRole(AriaRole.BUTTON).filter(new Locator.FilterOptions().setHasText(confirmRe)).first();
                Locator oneField = dialog.locator("input[type='tel'], input[inputmode='numeric'], input[name*='code' i], input[name*='otp' i]").first();
                Locator otpFields = dialog.locator("input[maxlength='1']");
                boolean otpUi = otpFields.count() >= 4 && otpFields.first().isVisible();
                boolean singleUi = oneField.count() > 0 && oneField.isVisible();
                boolean canRetryNow = (confirmBtn.count() > 0 && confirmBtn.isVisible()) && (otpUi || singleUi);
                if (canRetryNow) available = true;
            }

            if (!available) context.page.waitForTimeout(500);
        }

        assertTrue(available, "Должен быть доступен повтор кода: ссылка/кнопка, таймер, подсказка «не получил код» или возможность повторной попытки ввода.");
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @Story("Полное имя: минимальные требования")
    @DisplayName("SS-T27 | [NEG] Registration — Invalid full name; no confirmation / Невалидное «Имя Фамилия» — подтверждение не стартует")
    @MethodSource("invalidFullNames")
    void shouldRejectInvalidFullName_SS_T27(String badFullName, String title) {
        RegistrationPage reg = new RegistrationPage(context).open()
                .setFullName(badFullName)
                .setEmail(uniqueEmail())
                .setOrganization(DEF_ORG)
                .setPassword(DEF_PWD)
                .submit();

        reg.nameError.waitFor(new Locator.WaitForOptions().setTimeout(2000));
        assertTrue(reg.nameError.isVisible());

        String err = reg.nameError.textContent();
        String e = err == null ? "" : err.toLowerCase();
        assertTrue(e.contains("минимум") && e.contains("2"));

        assertFalse(reg.isCodeModalOpen());
        assertTrue(reg.modalRoot().isVisible());
        assertFalse(new Toast(context.page).waitAppear(4_000).containsAny("Код отправлен", "код отправлен"));
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @Story("Организация: минимальные требования")
    @DisplayName("SS-T28 | [NEG] Registration — Invalid organization name shows error / Невалидное «Название организации» — ошибка показывается")
    @MethodSource("invalidOrganizations")
    void shouldRejectInvalidOrganizationName_SS_T28(String badOrg, String title) {
        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmailPassword(uniqueEmail())
                .setOrganizationAndBlur(badOrg);

        assertFalse(reg.orgError.isVisible());

        reg.submit();

        reg.orgError.waitFor(new Locator.WaitForOptions().setTimeout(2000));
        assertTrue(reg.orgError.isVisible());

        String err = reg.orgError.textContent();
        String e = err == null ? "" : err.toLowerCase();
        assertTrue(e.contains("минимум") && e.contains("2"));

        assertFalse(reg.isCodeModalOpen());
        assertTrue(reg.modalRoot().isVisible());
        assertFalse(new Toast(context.page).waitAppear(4_000).containsAny("Код отправлен", "код отправлен"));
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @Story("Некорректный e-mail")
    @DisplayName("SS-T26 | [NEG] Registration — Incorrect e-mail; confirmation not started / Некорректный e-mail — подтверждение не стартует")
    @MethodSource("invalidEmailsFromJson")
    void shouldRejectInvalidEmail_SS_T26(String email, String title, boolean bugCase, String bugId) {
        if (bugCase && !RUN_KNOWN_BUGS) {
            if (bugId != null && !bugId.isBlank()) Allure.link("ISSUE", bugId);
            else Allure.link("ISSUE", "SS-115");
            Allure.label("known_bug", "true");
            Assumptions.assumeTrue(false, "Known bug " + (bugId == null ? "SS-115" : bugId) + ": " + title);
        }

        RegistrationPage reg = new RegistrationPage(context).open()
                .setFullName("Ivan Testov")
                .setOrganization(DEF_ORG)
                .setPassword(DEF_PWD)
                .setEmail(email)
                .submit();

        boolean toastProceeded = false;
        try {
            toastProceeded = new Toast(context.page).waitAppear(7000).containsAny("Код отправлен", "Письмо с кодом", "code sent");
        } catch (RuntimeException ignored) {
        }

        boolean proceeded = reg.isCodeModalOpen() || toastProceeded;

        boolean toastErr = false;
        try {
            toastErr = new Toast(context.page).waitAppear(7000).containsAny("Введите корректный email", "email", "ошиб", "invalid", "корректный");
        } catch (RuntimeException ignored) {
        }

        boolean inlineErr = new RegistrationPage(context).emailError.isVisible();

        assertFalse(proceeded, "Не ожидаем запуск подтверждения по некорректному email: " + email);
        assertTrue(inlineErr || toastErr, "Ожидалась inline-ошибка или тост про некорректный email: " + email);
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @Story("Пароль: невалидные варианты")
    @DisplayName("SS-T29 | [NEG] Registration — Invalid password; registration not started / Невалидный пароль — регистрация не стартует")
    @MethodSource("invalidPasswordsFromJson")
    void shouldRejectInvalidPasswords_SS_T29(String badPwd, String title, boolean bugCase) {
        if (bugCase && !RUN_KNOWN_BUGS) {
            Allure.link("ISSUE", "SS-119");
            Allure.label("known_bug", "true");
            Assumptions.assumeTrue(false, "Known bug SS-119: " + title);
        }

        RegistrationPage reg = new RegistrationPage(context).open()
                .fillNameEmail(uniqueEmail())
                .setOrganization(DEF_ORG)
                .setPassword(badPwd)
                .submit();

        boolean toastProceeded = false;
        try {
            toastProceeded = new Toast(context.page).waitAppear(6000).containsAny("Код отправлен", "Письмо с кодом");
        } catch (RuntimeException ignored) {
        }
        boolean proceeded = reg.isCodeModalOpen() || toastProceeded;

        boolean toastErr = false;
        try {
            toastErr = new Toast(context.page).waitAppear(6000).containsAny("Пароль", "Исправьте ошибки", "минимум 8 символов", "букву", "цифру", "спецсимвол");
        } catch (RuntimeException ignored) {
        }
        boolean inlineError = new RegistrationPage(context).passwordError.isVisible();

        assertFalse(proceeded, "Регистрация не должна стартовать по невалидному паролю: " + title);
        assertTrue(inlineError || toastErr, "Ожидалась ошибка валидации пароля: " + title);
    }

    @Test
    @Story("Пустые поля при регистрации")
    @DisplayName("SS-T82 | [NEG] Registration — Empty form shows errors under all fields / Сабмит пустой формы показывает ошибки")
    void shouldShowErrorsOnEmptyFields_SS_T82() {
        RegistrationPage reg = new RegistrationPage(context).open();

        reg.submit();

        reg.modalRoot().locator(".text-red-500").first().waitFor(new Locator.WaitForOptions().setTimeout(2000));

        assertTrue(reg.nameError.isVisible());
        assertTrue(reg.emailError.isVisible() || reg.looksInvalid(reg.emailInput(), reg.emailError));
        assertTrue(reg.orgError.isVisible());
        assertTrue(reg.passwordError.isVisible());

        assertFalse(reg.isCodeModalOpen());
        assertTrue(reg.modalRoot().isVisible());

        assertTrue(new Toast(context.page).waitAppear(6_000).containsAny("Исправьте ошибки", "ошиб", "исправьте"));
    }

    private boolean isLoggedInUI() {
        try {
            if (context.page.locator("text=Панель управления").first().isVisible()) return true;
        } catch (Throwable ignored) {
        }
        try {
            if (context.page.locator("text=Тесты").first().isVisible()) return true;
        } catch (Throwable ignored) {
        }
        try {
            if (context.page.locator("text=Кандидаты").first().isVisible()) return true;
        } catch (Throwable ignored) {
        }
        try {
            if (context.page.locator("header,nav").locator("text=Выход").first().isVisible()) return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private String getVerificationCode(String email) {
        String sys = System.getProperty("reg.code");
        if (sys != null && sys.matches("\\d{6}")) return sys;
        String env = System.getenv("REG_CODE");
        if (env != null && env.matches("\\d{6}")) return env;
        String fromApi = VerificationCodeClient.fetchCodeForEmail(email);
        if (fromApi != null && fromApi.matches("\\d{6}")) return fromApi;
        return null;
    }

    private static boolean containsAny(String text, String... needles) {
        String t = text == null ? "" : text.toLowerCase();
        for (String n : needles) if (t.contains(n.toLowerCase())) return true;
        return false;
    }

    private static Stream<Arguments> loadInvalidEmails() {
        JsonObject root = readCasesJson();
        if (!root.has("invalid_emails")) throw new IllegalStateException("Test data JSON not found for key 'invalid_emails'");
        List<Arguments> out = new ArrayList<>();
        for (JsonElement el : root.getAsJsonArray("invalid_emails")) {
            JsonObject o = el.getAsJsonObject();
            String email = o.get("email").getAsString();
            String title = o.get("title").getAsString();
            boolean bug = o.has("bug") && !o.get("bug").isJsonNull() && o.get("bug").getAsBoolean();
            String bugId = o.has("bugId") && !o.get("bugId").isJsonNull() ? o.get("bugId").getAsString() : null;
            out.add(Arguments.of(email, title, bug, bugId));
        }
        return out.stream();
    }

    private static Stream<Arguments> loadInvalidPasswords() {
        JsonObject root = readCasesJson();
        if (!root.has("invalid_passwords")) throw new IllegalStateException("Test data JSON not found for key 'invalid_passwords'");
        List<Arguments> out = new ArrayList<>();
        for (JsonElement el : root.getAsJsonArray("invalid_passwords")) {
            JsonObject o = el.getAsJsonObject();
            String pwd = o.get("password").getAsString();
            String title = o.get("title").getAsString();
            boolean bug = o.has("bug") && !o.get("bug").isJsonNull() && o.get("bug").getAsBoolean();
            out.add(Arguments.of(pwd, title, bug));
        }
        return out.stream();
    }

    private static JsonObject readCasesJson() {
        String[] paths = {
                "/data/registration/invalid_data.json",
                "/data/registration/invalid_passwords.json"
        };
        for (String p : paths) {
            InputStream is = RegistrationTests.class.getResourceAsStream(p);
            if (is != null) {
                try (Reader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    return JsonParser.parseReader(r).getAsJsonObject();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse JSON: " + p, e);
                }
            }
        }
        throw new IllegalStateException("Test data JSON not found at: " + String.join(", ", paths));
    }
}