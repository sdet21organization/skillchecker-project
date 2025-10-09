package helpers;

import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseVerifier {

    @Step("Verify JSON schema and status code is {expectedStatusCode}")
    public static void verifyResponse(Response response, int expectedStatusCode, String pathToSchema) {
        response.then().assertThat()
                .statusCode(expectedStatusCode)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(pathToSchema));
    }

    @Step("Verify status code is {expectedStatusCode}")
    public static void verifyResponse(Response response, int expectedStatusCode) {
        response.then().assertThat()
                .statusCode(expectedStatusCode);
    }

    @Step("Verify cookie {cookieName} is set")
    public static void hasCookie(Response response, String cookieName) {
        String c = response.getCookie(cookieName);
        assertTrue(c != null && !c.isBlank());
    }

    @Step("Verify unauthorized (401 or 403)")
    public static void verifyUnauthorized(Response response) {
        int code = response.getStatusCode();
        assertTrue(code == 401 || code == 403);
    }
}