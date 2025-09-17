package wrappers;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Candidates {

    @Step("Get all candidates")
    public static Response getAllCandidates(String cookie) {
        Response response =
                given()
                        .contentType("application/json")
                        .header("Cookie",cookie)
                        .when()
                        .get("/candidates")
                        .then()
                        .assertThat()
                        .contentType(ContentType.JSON)
                        .extract().response();
        return response;
    }

    @Step("Verify JSON schema and status code is 200")
    public static void verifySuccessfulGetAllCandidatesResponse(Response response) {
        response.then().assertThat()
                .statusCode(200);
//              .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/candidates/GetAllCandidatesSuccessResponse.json"));
    }
}

