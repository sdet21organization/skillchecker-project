package wrappers;

import dto.candidates.CandidatesRequest;
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
                        .header("Cookie", cookie)
                        .when()
                        .get("candidates")
                        .then()
                        .assertThat()
                        .contentType(ContentType.JSON)
                        .extract().response();
        return response;
    }

    @Step("Create a new Candidate")
    public static Response createNewCandidate(String cookie, CandidatesRequest payload) {

        Response response =
                given()
                        .contentType("application/json")
                        .header("Cookie", cookie)
                        .body(payload)
                        .when()
                        .post("candidates")
                        .then()
                        .assertThat()
                        .contentType(ContentType.JSON)
                        .extract().response();
        return response;
    }
}

