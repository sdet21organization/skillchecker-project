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
@DisplayName("Check GET /candidates/export requests")
public class ExportCandidatesTests extends BaseTest {


    @Test
    @DisplayName("Check successful candidates export")
    public void exportCandidates() {

        Response response = Candidates.exportCandidates(cookie);
        ResponseVerifier.verifyResponse(response, 200);
    }

}