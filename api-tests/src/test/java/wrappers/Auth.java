package wrappers;

import dto.auth.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Auth {

    @Step("Send login request with {email} and {password}")
    public static Response loginUser(String email, String password) {
        LoginRequest loginPayload = new LoginRequest();
        loginPayload.setEmail(email);
        loginPayload.setPassword(password);

        return given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("login")
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .extract().response();
    }

    @Step("Get current authenticated user")
    public static Response getCurrentUser(String cookie) {
        return given()
                .header("Cookie", cookie)
                .when()
                .get("user")
                .then()
                .extract().response();
    }

    @Step("Logout current session")
    public static Response logout(String cookie) {
        return given()
                .header("Cookie", cookie)
                .accept("application/json")
                .body("")
                .when()
                .post("logout")
                .then()
                .extract().response();
    }

    @Step("Verify JSON schema and status code is 200")
    public static void verifySuccessfulLoginResponse(Response response) {
        response.then().assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/auth/LoginSuccessResponse.json"));
    }

    @Step("Verify JSON schema and status code is 401")
    public static void verifyUnsuccessfulLoginResponse(Response response) {
        response.then().assertThat()
                .statusCode(401)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/auth/ErrorResponse.json"));
    }
}