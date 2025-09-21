package wrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

;

public class ManageTests {

    @Step("Create test via API")
    public static Response createTest(String cookie, String name, String description) {
        String body = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(body)
                .when()
                .post("/tests")
                .then()
                .extract().response();
    }

    @Step("Verify JSON schema and status code is 201")
    public static void verifyCreateTest(Response response) {
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON)

                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath("schemas.manage/CreateTestSuccessResponse.json"));
    }


    @Step("Patch test with id={testId}")
    public static Response updateTest(String cookie, String testId, Map<String, Object> fields) throws JsonProcessingException {
        String body = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(fields);

        return given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .pathParam("id", testId)
                .body(body)
                .when()
                .patch("/tests/{id}")
                .then()
                .extract().response();
    }


    @Step("Verify update test response (200 JSON -> match schema; 204 -> no content)")
    public static void verifyUpdateTest(Response response) {
        int sc = response.statusCode();
        if (sc == 200) {
            response.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(io.restassured.module.jsv.JsonSchemaValidator
                            .matchesJsonSchemaInClasspath("schemas.manage/UpdateTestSuccessResponse.json"));
        } else {

            response.then().statusCode(org.hamcrest.Matchers.anyOf(
                    org.hamcrest.Matchers.is(200),
                    org.hamcrest.Matchers.is(204)
            ));
        }
    }


}




