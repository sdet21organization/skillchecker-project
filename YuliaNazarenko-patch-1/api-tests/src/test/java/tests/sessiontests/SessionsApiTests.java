package tests.sessiontests;

import com.fasterxml.jackson.core.JsonProcessingException;
import dto.candidates.CandidatesRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Candidates;
import wrappers.ManageTests;
import wrappers.Sessions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class SessionsApiTests extends BaseTest {

    @Test
    @DisplayName("Назначить тест кандидату: 201 + схема ответа")
    void createSession_success() throws JsonProcessingException {

        int testId = ManageTests.createTest(cookie, "API Session Test", "assign")
                .jsonPath().getInt("id");


        CandidatesRequest payload = new CandidatesRequest();
        payload.setName("API Candidate");
        payload.setEmail("api.candidate" + System.currentTimeMillis() + "@skillchecker.tech");
        payload.setPhone("+380501234567");
        payload.setPosition("QA Automation");

        Response candidateResp = Candidates.createNewCandidate(cookie, payload);
        int candidateId = candidateResp.jsonPath().getInt("id");


        String expiresAt = Instant.now()
                .plus(3, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.SECONDS)
                .toString();

        Map<String, Object> body = new HashMap<>();
        body.put("testId", testId);
        body.put("candidateId", candidateId);
        body.put("expiresAt", expiresAt);


        Response resp = Sessions.createSession(cookie, body);


        resp.then()
                .statusCode(anyOf(is(201), is(200)))
                .body(matchesJsonSchemaInClasspath("schemas/sessions/CreateSessionSuccessResponse.json"));
    }
}