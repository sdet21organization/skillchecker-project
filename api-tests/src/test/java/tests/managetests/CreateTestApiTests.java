package tests.managetests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.ManageTests;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("API Tests")
@DisplayName("Create tests via API")
public class CreateTestApiTests extends BaseTest {

    @Test
    @DisplayName("Positive scenario: successful test creation")
    void createTestSuccessfully() {
        String testName = "API Test " + System.currentTimeMillis();
        String testDescription = "This is a test created via API";

        Response response = ManageTests.createTest(cookie, testName, testDescription);

        ManageTests.verifyCreateTest(response);
    }

    @Test
    @DisplayName("Positive scenario: создание теста без описания")
    void createTestWithoutDescription() {
        String testName = "API Test " + System.currentTimeMillis();

        Response response = ManageTests.createTest(cookie, testName, "");
        response.then().statusCode(201);
    }

    @Test
    @DisplayName("Negative scenario: создание теста без названия")
    void createTestWithoutName() {
        String testDescription = "Test without name";

        Response response = ManageTests.createTest(cookie, "", testDescription);

        response.then().statusCode(400);
    }


    @Test
    @DisplayName("Edit test: change name")
    void updateTestName() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("name", "Updated Test Name"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Edit test: change description")
    void updateTestDescription() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("description", "Updated Description"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Edit test: change name and description")
    void updateTestNameAndDescription() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("name", "Updated Test Name", "description", "Updated Description"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Edit test: change time limit")
    void updateTestTimeLimit() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("timeLimit", 30));

        ManageTests.verifyUpdateTest(updateResponse);
        assertEquals(30, updateResponse.jsonPath().getInt("timeLimit"), "timeLimit должен обновиться до 30");
    }

    @Test
    @DisplayName("Edit test: change time limit")
    void updateTestPassingScore() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("passingScore", 85));

        ManageTests.verifyUpdateTest(updateResponse);

        assertEquals(85, updateResponse.jsonPath().getInt("passingScore"), "passingScore должен обновиться до 85");
    }

    @Test
    @DisplayName("Edit test: change isActive to true")
    void updateTestIsActive() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Inactive Test", "With flag");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("isActive", true));

        ManageTests.verifyUpdateTest(updateResponse);

        assertTrue(updateResponse.jsonPath().getBoolean("isActive"), "Поле isActive должно быть true");
    }
}
