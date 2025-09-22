package tests.questiontests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Questions;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * API-тесты для работы с вопросами:
 * - создание вопросов
 * - негативные сценарии создания
 * - генерация вопросов
 */
public class QuestionsApiTests extends BaseTest {

    private Map<String, Object> validQuestionBody(int testId) {
        Map<String, Object> body = new HashMap<>();
        body.put("testId", testId);
        body.put("content", "What is 2 + 2?");
        body.put("type", "single_choice");

        Map<String, Object> options = new HashMap<>();
        options.put("A", "3");
        options.put("B", "4");
        options.put("C", "5");
        body.put("options", options);

        Map<String, Object> correct = new HashMap<>();
        correct.put("key", "B");
        body.put("correctAnswer", correct);

        body.put("points", 5);
        body.put("order", 1);
        body.put("imageUrl", null);
        body.put("codeSnippet", null);
        body.put("codeLanguage", "javascript");
        return body;
    }

    @Test
    @DisplayName("Создание вопроса: 201 + схема")
    void createQuestion_success() throws JsonProcessingException {
        Response createTest = ManageTests.createTest(cookie, "API Test for Questions", "create question flow");
        int testId = createTest.jsonPath().getInt("id");

        Response resp = Questions.createQuestion(cookie, validQuestionBody(testId));

        resp.then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("testId", equalTo(testId))
                .body("content", equalTo("What is 2 + 2?"))
                .body("type", equalTo("single_choice"))
                .body(matchesJsonSchemaInClasspath("schemas.questions/CreateQuestionSuccessResponse.json"));
    }

    @Test
    @DisplayName("Создание вопроса: 400/422 при пустом content")
    void createQuestion_invalidContent() throws JsonProcessingException {
        int testId = ManageTests.createTest(cookie, "API Test invalid question", "bad").jsonPath().getInt("id");

        Map<String, Object> body = validQuestionBody(testId);
        body.put("content", ""); // невалидно

        Response resp = Questions.createQuestion(cookie, body);

        resp.then()
                .statusCode(anyOf(is(400), is(422)));
        // при наличии сообщения можно добавить:
        // .body("message", containsString("content"));
    }

    @Test
    @DisplayName("Генерация вопросов: 200/201 + непустой список")
    void generateQuestions_success() throws JsonProcessingException {
        int testId = ManageTests.createTest(cookie, "API Test generation", "generate").jsonPath().getInt("id");

        Map<String, Object> gen = new HashMap<>();
        gen.put("testId", testId);
        gen.put("topic", "basic math");
        gen.put("count", 3);

        Response resp = Questions.generateQuestions(cookie, gen);

        resp.then()
                .statusCode(anyOf(is(200), is(201)))
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body(matchesJsonSchemaInClasspath("schemas.questions/GenerateQuestionsSuccessResponse.json"));
    }
}
