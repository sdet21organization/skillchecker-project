package tests.candidates;

import com.github.javafaker.Faker;
import dto.candidates.ImportCandidatesRequest;
import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Candidates;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("API Tests")
@DisplayName("Check POST /candidates/import requests")
public class ImportCandidatesTests extends BaseTest {

    Faker faker = new Faker();

    @Test
    @DisplayName("Check fully successful candidates import")
    public void importCandidates() {
        ImportCandidatesRequest payload = new ImportCandidatesRequest();

        List<ImportCandidatesRequest.Candidate> candidates = new ArrayList<>();
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));

        payload.setCandidates(candidates);
        Response response = Candidates.importCandidates(cookie, payload);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/ImportCandidatesSuccessResponse.json");
        assertEquals(true, response.path("success"));
        assertEquals((Integer) 2, response.path("imported"));
        assertEquals((Integer) 0, response.path("duplicates"));
    }

    @Test
    @DisplayName("Check candidates import when email is absent")
    public void importCandidatesWithAbsentEmail() {
        ImportCandidatesRequest payload = new ImportCandidatesRequest();

        List<ImportCandidatesRequest.Candidate> candidates = new ArrayList<>();
        candidates.add(createCandidate(faker.name().lastName(), "", faker.job().position()));
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));

        payload.setCandidates(candidates);
        Response response = Candidates.importCandidates(cookie, payload);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/ImportCandidatesSuccessResponse.json");
        assertEquals(true, response.path("success"));
        assertEquals((Integer) 1, response.path("imported"));
        assertEquals((Integer) 0, response.path("duplicates"));
        assertEquals((Integer) 2, response.path("errors[0].row"));
        assertEquals("Email", response.path("errors[0].field"));
        assertEquals("Обязательное поле отсутствует или имеет неверный формат", response.path("errors[0].message"));
    }

    @Test
    @DisplayName("Check candidates import when name is absent")
    public void importCandidatesWithAbsentName() {
        ImportCandidatesRequest payload = new ImportCandidatesRequest();

        List<ImportCandidatesRequest.Candidate> candidates = new ArrayList<>();
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));
        candidates.add(createCandidate("", faker.internet().safeEmailAddress(), faker.job().position()));

        payload.setCandidates(candidates);
        Response response = Candidates.importCandidates(cookie, payload);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/ImportCandidatesSuccessResponse.json");
        assertEquals(true, response.path("success"));
        assertEquals((Integer) 1, response.path("imported"));
        assertEquals((Integer) 0, response.path("duplicates"));
        assertEquals((Integer) 3, response.path("errors[0].row"));
        assertEquals("Name", response.path("errors[0].field"));
        assertEquals("Обязательное поле отсутствует или имеет неверный формат", response.path("errors[0].message"));
    }

    @Test
    @DisplayName("Check candidates import when row has no values")
    public void importCandidatesWithAbsentRow() {
        ImportCandidatesRequest payload = new ImportCandidatesRequest();

        List<ImportCandidatesRequest.Candidate> candidates = new ArrayList<>();
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));
        candidates.add(createCandidate("", "", ""));
        candidates.add(createCandidate(faker.name().lastName(), faker.internet().safeEmailAddress(), faker.job().position()));

        payload.setCandidates(candidates);
        Response response = Candidates.importCandidates(cookie, payload);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/ImportCandidatesSuccessResponse.json");
        assertEquals(true, response.path("success"));
        assertEquals((Integer) 2, response.path("imported"));
        assertEquals((Integer) 0, response.path("duplicates"));
        assertEquals((Integer) 3, response.path("errors[0].row"));
        assertEquals("Email", response.path("errors[0].field"));
        assertEquals("Обязательное поле отсутствует или имеет неверный формат", response.path("errors[0].message"));
    }

    @Test
    @DisplayName("Check candidates import with duplicated emails")
    public void importCandidatesWithDuplicatedEmails() {
        ImportCandidatesRequest payload = new ImportCandidatesRequest();

        List<ImportCandidatesRequest.Candidate> candidates = new ArrayList<>();
        String duplicatedEmail = faker.internet().safeEmailAddress();
        candidates.add(createCandidate(faker.name().lastName(), duplicatedEmail, faker.job().position()));
        candidates.add(createCandidate(faker.name().lastName(), duplicatedEmail, faker.job().position()));

        payload.setCandidates(candidates);
        Response response = Candidates.importCandidates(cookie, payload);
        ResponseVerifier.verifyResponse(response, 200, "schemas/candidates/ImportCandidatesSuccessResponse.json");
        assertEquals(true, response.path("success"));
        assertEquals((Integer) 1, response.path("imported"));
        assertEquals((Integer) 1, response.path("duplicates"));
    }

    public ImportCandidatesRequest.Candidate createCandidate(String name, String email, String position) {
        ImportCandidatesRequest.Candidate candidate = new ImportCandidatesRequest.Candidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPosition(position);
        return candidate;
    }
}