package tests.auth;

import helpers.ConfigurationReader;
import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Auth;

@Epic("API Tests")
@Feature("Auth/Login")
@DisplayName("Auth/Login tests (SS-T30, SS-T31, SS-T32, SS-T62)")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("[SS-T30] POS — успешная авторизация: 200 + schema + cookie + /user 200")
    void login_ok() {
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        Response login = Auth.loginUser(email, password);
        Auth.verifySuccessfulLoginResponse(login);
        ResponseVerifier.hasCookie(login, "connect.sid");

        String cookieHeader = "connect.sid=" + login.getCookie("connect.sid");
        Response me = Auth.getCurrentUser(cookieHeader);
        ResponseVerifier.verifyResponse(me, 200);
    }

    @Test
    @DisplayName("[SS-T31] NEG — неверный логин: 401 + ErrorResponse")
    void invalid_email() {
        String validPassword = ConfigurationReader.get("password");
        Response resp = Auth.loginUser("wrong_user@test.com", validPassword);
        Auth.verifyUnsuccessfulLoginResponse(resp);
    }

    @Test
    @DisplayName("[SS-T32] NEG — неверный пароль: 401 + ErrorResponse")
    void invalid_password() {
        String validEmail = ConfigurationReader.get("email");
        Response resp = Auth.loginUser(validEmail, "wrongpass");
        Auth.verifyUnsuccessfulLoginResponse(resp);
    }

    @Test
    @DisplayName("[SS-T62] NEG — пустые поля (email и пароль): 400 + ErrorResponse")
    void both_empty() {
        Response r = Auth.loginUser("", "");
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @DisplayName("[SS-T62] NEG — пустой email: 400 + ErrorResponse")
    void email_empty() {
        Response r = Auth.loginUser("", "AnyPass123!");
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }

    @Test
    @DisplayName("[SS-T62] NEG — пустой пароль: 400 + ErrorResponse")
    void password_empty() {
        Response r = Auth.loginUser("any@mail.com", "");
        ResponseVerifier.verifyResponse(r, 400, "schemas/auth/ErrorResponse.json");
    }
}