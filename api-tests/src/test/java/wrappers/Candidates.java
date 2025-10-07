package wrappers;

import dto.candidates.CandidatesRequest;
import dto.candidates.ImportCandidatesRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Candidates {

    @Step("Get all candidates")
    public static Response getAllCandidates(String cookie) {
        return  given().contentType("application/json").header("Cookie", cookie).when().get("candidates").then().assertThat().contentType(ContentType.JSON).extract().response();
    }

    @Step("Create a new Candidate")
    public static Response createNewCandidate(String cookie, CandidatesRequest payload) {
        return given().contentType("application/json").header("Cookie", cookie).body(payload).when().post("candidates").then().assertThat().contentType(ContentType.JSON).extract().response();
    }

    @Step("Import Candidates")
    public static Response importCandidates(String cookie, ImportCandidatesRequest payload) {
        return given().contentType("application/json").header("Cookie", cookie).body(payload).when().post("candidates/import").then().assertThat().contentType(ContentType.JSON).extract().response();
    }

    @Step("Export candidates data to Excel")
    public static Response exportCandidates(String cookie) {
        return  given().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").header("Cookie", cookie).when().get("candidates/export").then().assertThat().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").extract().response();
    }
}