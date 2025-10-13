package wrappers;

import dto.auth.LoginRequest;
import dto.auth.SelfRegisterRequest;
import dto.auth.SendEmailCodeRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class Auth {

    @Step("POST /login")
    public static Response loginUser(String email, String password) {
        LoginRequest body = new LoginRequest();
        body.setEmail(email);
        body.setPassword(password);
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/login");
    }

    @Step("POST /logout")
    public static Response logout(String cookie) {
        return given()
                .relaxedHTTPSValidation()
                .header("Cookie", cookie)
                .contentType(ContentType.JSON)
                .post("/logout");
    }

    @Step("POST /send-email-code")
    public static Response sendEmailCode(String email) {
        SendEmailCodeRequest body = new SendEmailCodeRequest();
        body.setEmail(email);
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/send-email-code");
    }

    @Step("POST /self-register (Map)")
    public static Response selfRegister(Map<String, Object> payload) {
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(payload)
                .post("/self-register");
    }

    @Step("POST /self-register (DTO)")
    public static Response selfRegister(SelfRegisterRequest body) {
        return given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/self-register");
    }

    @Step("POST /register (admin)")
    public static Response adminRegisterUser(String cookie,
                                             String email,
                                             String fullName,
                                             String password,
                                             String role,
                                             boolean active) {
        return given()
                .relaxedHTTPSValidation()
                .header("Cookie", cookie)
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", email,
                        "fullName", fullName,
                        "password", password,
                        "role", role,
                        "active", active
                ))
                .post("/register");
    }
}