package tests.registration;

import helpers.ConfigurationReader;
import helpers.EmailGenerator;
import helpers.ImapCodeFetcher;
import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tests.BaseTest;
import wrappers.Auth;

import java.util.HashMap;
import java.util.Map;

@Epic("API Tests")
@Feature("Auth/Registration")
@Owner("Ko.Herasymets")
@DisplayName("Auth/Registration API Tests")
@Execution(ExecutionMode.SAME_THREAD)
public class RegistrationApiTests extends BaseTest {

    private static Map<String, Object> payload(String email, String fullName, String password, String orgName, Object code) {
        Map<String, Object> m = new HashMap<>();
        m.put("email", email);
        m.put("fullName", fullName);
        m.put("password", password);
        m.put("organizationName", orgName);
        m.put("code", code);
        return m;
    }

    @Test
    @Story("SS-T82")
    @Tag("negative")
    @DisplayName("[SS-T82][NEG] Auth/Registration — Пустые поля → 400")
    void ssT82_empty_fields_400() {
        Map<String, Object> req = payload("", "", "", "", "");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T26")
    @Tag("negative")
    @DisplayName("[SS-T26][NEG] Auth/Registration — Некорректный email → 400")
    void ssT26_invalid_email_400() {
        Map<String, Object> req = payload("not-an-email", "Test User", "Qwerty123!", "Org-" + (int) (Math.random() * 9000 + 1000), "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T29")
    @Tag("negative")
    @DisplayName("[SS-T29][NEG] Auth/Registration — Невалидный пароль → 400")
    void ssT29_invalid_password_400() {
        Map<String, Object> req = payload(EmailGenerator.generateUnique(), "Test User", "123", "Org-" + (int) (Math.random() * 9000 + 1000), "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T28")
    @Tag("negative")
    @DisplayName("[SS-T28][NEG] Auth/Registration — Пустое название организации → 400")
    void ssT28_invalid_org_name_400() {
        Map<String, Object> req = payload(EmailGenerator.generateUnique(), "Test User", "Qwerty123!", "", "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T27")
    @Tag("negative")
    @DisplayName("[SS-T27][NEG] Auth/Registration — Короткое имя фамилия → 409")
    void ssT27_invalid_fullname_409() {
        Map<String, Object> req = payload(EmailGenerator.generateUnique(), "A", "Qwerty123!", "Org-" + (int) (Math.random() * 9000 + 1000), "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 409, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T14")
    @Tag("negative")
    @DisplayName("[SS-T14][NEG] Auth/Registration — Email уже зарегистрирован → 409")
    void ssT14_email_already_registered_409() {
        String existing = EmailGenerator.generateUnique();
        Auth.adminRegisterUser(cookie, existing, "Existing User", "Qwerty123!", "recruiter", true);
        Map<String, Object> req = payload(existing, "Someone Else", "Qwerty123!", "Org-" + (int) (Math.random() * 9000 + 1000), "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 409, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T83")
    @Tag("negative")
    @DisplayName("[SS-T83][NEG] Auth/Registration — Неверный/просроченный код → 409")
    void ssT83_invalid_verification_code_409() {
        String email = EmailGenerator.generateUnique();
        Auth.sendEmailCode(email);
        Map<String, Object> req = payload(email, "API Autotest", "Qwerty123!", "Org-" + (int) (Math.random() * 9000 + 1000), "000000");
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 409, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T11")
    @Tag("positive")
    @Tag("imap")
    @DisplayName("[SS-T11][POS] Auth/Registration — Успешная регистрация → 201 (IMAP)")
    void ssT11_success_registration_imap_201() throws Exception {
        String strategy = ConfigurationReader.get("reg.code.strategy");
        boolean imapOn = strategy != null && strategy.equalsIgnoreCase("imap");
        Assumptions.assumeTrue(imapOn, "IMAP strategy is not enabled");

        String email = EmailGenerator.generateUnique();
        long searchStartTime = System.currentTimeMillis();

        Auth.sendEmailCode(email);
        Thread.sleep(2000);

        String code = ImapCodeFetcher.fetchVerificationCode(email, searchStartTime);
        org.junit.jupiter.api.Assertions.assertNotNull(code, "Verification code was not received via IMAP within timeout");

        Map<String, Object> req = payload(email, "API Autotest", "Qwerty123!", "Org-" + (int)(Math.random()*9000+1000), code);
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 201, "schemas/auth/RegisterResponse.json");
    }

    @Test
    @Story("SS-T84")
    @Tag("positive")
    @Tag("imap")
    @DisplayName("[SS-T84][POS] Auth/Registration — Спецсимволы в названии организации → 201 (IMAP)")
    void ssT84_org_name_allows_symbols_imap_201() throws Exception {
        String strategy = ConfigurationReader.get("reg.code.strategy");
        boolean imapOn = strategy != null && strategy.equalsIgnoreCase("imap");
        Assumptions.assumeTrue(imapOn, "IMAP strategy is not enabled");

        String email = EmailGenerator.generateUnique();
        long searchStartTime = System.currentTimeMillis();

        Auth.sendEmailCode(email);
        Thread.sleep(2000);

        String code = ImapCodeFetcher.fetchVerificationCode(email, searchStartTime);
        org.junit.jupiter.api.Assertions.assertNotNull(code, "Verification code was not received via IMAP within timeout");

        String org = "Org_#2025-API!@%+";
        Map<String, Object> req = payload(email, "API Autotest", "Qwerty123!", org, code);
        Response r = Auth.selfRegister(req);
        ResponseVerifier.verifyResponse(r, 201, "schemas/auth/RegisterResponse.json");
    }
}