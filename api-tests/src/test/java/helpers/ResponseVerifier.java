package helpers;

import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class ResponseVerifier {

    @Step("Verify JSON schema and status code is {statusCode}")
    public static void verifyResponse(Response response, int expectedStatusCode, String pathToSchema) {
        response.then().assertThat()
                .statusCode(expectedStatusCode)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(pathToSchema));
    }
}
