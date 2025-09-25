package tests.questiontests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class QuestionsApiTests extends BaseTest {


    @Test
    @DisplayName("Создание вопроса:проверка статуса 201 + схемы")
    void createQuestion_success() throws JsonProcessingException {

        io.restassured.response.Response createTestResp =
                wrappers.ManageTests.createTest(cookie, "API Test", "for questions");
        int testId = createTestResp.jsonPath().getInt("id");

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("testId", testId);
        body.put("content", "What is 2+2?");
        body.put("type", "single_choice");
        body.put("options", java.util.Map.of("A", "3", "B", "4", "C", "5"));
        body.put("correctAnswer", java.util.Map.of("key", "B"));
        body.put("points", 5);
        body.put("order", 1);
        body.put("imageUrl", null);
        body.put("codeSnippet", null);
        body.put("codeLanguage", "javascript");

        Response resp = wrappers.Questions.createQuestion(cookie, body);

        resp.then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/questions/CreateQuestionSuccessResponse.json"));
    }

    @Test
    @DisplayName("Создание вопроса: проверка статуса 400 если отсутствует testId")
    void createQuestion_missingTestId_400() throws JsonProcessingException {
        wrappers.ManageTests.createTest(cookie, "API Negative", "missing testId");

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("content", "What is 2 + 2?");
        body.put("type", "single_choice");
        body.put("options", java.util.Map.of("A", "3", "B", "4", "C", "5"));
        body.put("correctAnswer", java.util.Map.of("key", "B"));
        body.put("points", 5);
        body.put("order", 1);
        body.put("imageUrl", null);
        body.put("codeSnippet", null);
        body.put("codeLanguage", "javascript");

        Response resp = wrappers.Questions.createQuestion(cookie, body);

        resp.then().statusCode(org.hamcrest.Matchers.anyOf(
                org.hamcrest.Matchers.is(400),
                org.hamcrest.Matchers.is(422)
        ));
    }
}
