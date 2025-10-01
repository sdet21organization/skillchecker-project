package wrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.manage.CreateTestRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@DisplayName("Manage Tests API Wrapper")
public class ManageTests {

    @Step("Creation of a new test via API")
    public static Response createTest(String cookie, String name, String description) {
        CreateTestRequest body = new CreateTestRequest(name, description);

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

    @Step("Verification of JSON schema and status code upon test creation")
    public static void verifyCreateTest(Response response) {
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/manage/CreateTestSuccessResponse.json"));

    }


    @Step("Updating an existing test via API")
    public static Response updateTest(String cookie, String testId, Map<String, Object> fields) throws JsonProcessingException {
        String body = new ObjectMapper().writeValueAsString(fields);

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


    @Step("Verification of JSON schema and status code upon test update")
    public static void verifyUpdateTest(Response response) {
        int sc = response.statusCode();
        if (sc == 200) {
            response.then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)

                    .body(matchesJsonSchemaInClasspath("schemas/manage/UpdateTestSuccessResponse.json"));

        } else {

            response.then().statusCode(Matchers.anyOf(
                    Matchers.is(200),
                    Matchers.is(204)
            ));
        }
    }


}
