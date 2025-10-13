package tests.auth;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Auth;

@Epic("API Tests")
@Feature("Auth/Login")
@Owner("Ko.Herasymets")
@DisplayName("Auth/Login API Tests")
public class LoginTests extends BaseTest {

    @Test
    @Story("SS-T32")
    @Tag("negative")
    @DisplayName("[SS-T32][NEG] Auth/Login — Неверный пароль → 401")
    void ssT32_login_wrong_password_401() {
        Response r = Auth.loginUser("admin@skillchecker.tech", "wrongpassword");
        ResponseVerifier.verifyResponse(r, 401, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T31")
    @Tag("negative")
    @DisplayName("[SS-T31][NEG] Auth/Login — Неверный логин → 401")
    void ssT31_login_wrong_email_401() {
        Response r = Auth.loginUser("wrong@email.com", "admin123");
        ResponseVerifier.verifyResponse(r, 401, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T62")
    @Tag("negative")
    @DisplayName("[SS-T62][NEG] Auth/Login — Пустые поля → 400")
    void ssT62_login_empty_fields_400() {
        Response r = Auth.loginUser("", "");
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @Story("SS-T30")
    @Tag("positive")
    @DisplayName("[SS-T30][POS] Auth/Login — Успешная авторизация → 200")
    void ssT30_login_success_200() {
        Response r = Auth.loginUser("admin@skillchecker.tech", "admin123");
        ResponseVerifier.verifyResponse(r, 200, "schemas/auth/LoginSuccessResponse.json");
    }
}