package tests.managetests;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.ManageTests;

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


}


