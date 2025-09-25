package tests.managetests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.ManageTests;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Создание теста через API")
public class CreateTestApiTests extends BaseTest {

    @Test
    @DisplayName("Позитивный сценарий: успешное создание теста")
    void createTestSuccessfully() {
        String testName = "API Test " + System.currentTimeMillis();
        String testDescription = "This is a test created via API";

        Response response = ManageTests.createTest(cookie, testName, testDescription);

        ManageTests.verifyCreateTest(response);
    }

    @Test
    @DisplayName("Позитивный сценарий: создание теста без описания")
    void createTestWithoutDescription() {
        String testName = "API Test " + System.currentTimeMillis();

        Response response = ManageTests.createTest(cookie, testName, "");
        response.then().statusCode(201);
    }

    @Test
    @DisplayName("Негативный сценарий: создание теста без имени")
    void createTestWithoutName() {
        String testDescription = "Test without name";

        Response response = ManageTests.createTest(cookie, "", testDescription);

        response.then().statusCode(400);
    }


    @Test
    @DisplayName("Редактирование теста: изменение названия")
    void updateTestName() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("name", "Updated Test Name"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Редактирование теста: изменение описания")
    void updateTestDescription() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("description", "Updated Description"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Редактирование теста: изменение имени и описания")
    void updateTestNameAndDescription() throws JsonProcessingException {
        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, Map.of("name", "Updated Test Name", "description", "Updated Description"));

        ManageTests.verifyUpdateTest(updateResponse);
    }

    @Test
    @DisplayName("Редактирование теста: изменение лимита времени")
    void updateTestTimeLimit() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("timeLimit", 30));

        ManageTests.verifyUpdateTest(updateResponse);
        assertEquals(30, updateResponse.jsonPath().getInt("timeLimit"), "timeLimit должен обновиться до 30");
    }

    @Test
    @DisplayName("Редактирование теста: изменение проходного балла")
    void updateTestPassingScore() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Original Test", "Original Description");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("passingScore", 85));

        ManageTests.verifyUpdateTest(updateResponse);

        assertEquals(85, updateResponse.jsonPath().getInt("passingScore"), "passingScore должен обновиться до 85");
    }

    @Test
    @DisplayName("Редактирование теста: изменение активности")
    void updateTestIsActive() throws JsonProcessingException {

        Response createResponse = ManageTests.createTest(cookie, "Inactive Test", "With flag");
        String testId = createResponse.jsonPath().getString("id");

        Response updateResponse = ManageTests.updateTest(cookie, testId, java.util.Map.of("isActive", true));

        ManageTests.verifyUpdateTest(updateResponse);

        assertTrue(updateResponse.jsonPath().getBoolean("isActive"), "Поле isActive должно быть true");
    }
}
