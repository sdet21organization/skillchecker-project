package tests.candidates;

import dto.candidates.CandidatesRequest;
import helpers.ResponseVerifier;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Candidates;

@DisplayName("Проверка запросов POST /candidates")
public class CreateNewCandidateTests extends BaseTest {

    @Test
    @DisplayName("Check creating a new candidate")
    void createNewCandidate() {
        long timestamp = System.currentTimeMillis();
        CandidatesRequest payload = new CandidatesRequest();
        payload.setName("Rocky Balboa" + timestamp);
        payload.setEmail("rocky.balboa" + timestamp + "@mail.com");
        payload.setPosition("Developer");
        Response response = Candidates.createNewCandidate(cookie, payload);

        ResponseVerifier.verifyResponse(response, 201, "schemas/candidates/CreateNewCandidateSuccessResponse.json");
    }

    @Test
    @DisplayName("Check error response when creating a new candidate without data")
    void createNewCandidateWithoutData() {
        CandidatesRequest payload = new CandidatesRequest();
        payload.setEmail("rocky.balboa@mail.com");
        payload.setPosition("Developer");
        Response response = Candidates.createNewCandidate(cookie, payload);

        ResponseVerifier.verifyResponse(response, 400, "schemas/candidates/CreateNewCandidateErrorResponse.json");
        Assertions.assertEquals("Invalid candidate data", response.jsonPath().getString("message"));
    }
}