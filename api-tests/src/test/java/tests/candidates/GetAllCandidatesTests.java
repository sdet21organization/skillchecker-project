package tests.candidates;

import helpers.ResponseVerifier;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Candidates;

@DisplayName("Get all candidates tests")
public class GetAllCandidatesTests extends BaseTest {

    @Test
    @DisplayName("Check getting all candidates")
    void getAllCandidates() {
        Response response = Candidates.getAllCandidates(cookie);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/GetAllCandidatesSuccessResponse.json");

    }
}