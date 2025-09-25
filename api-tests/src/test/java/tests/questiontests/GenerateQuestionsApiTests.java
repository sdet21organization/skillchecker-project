package tests.questiontests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.ManageTests;
import wrappers.Questions;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("Генерация вопросов через API")
public class GenerateQuestionsApiTests extends BaseTest {

    @Test
    @DisplayName("Генерация 3 single_choice вопросов: проверка статуса 200 + схемы")
    void generateQuestions_success() throws JsonProcessingException {

        Response createTestResp = ManageTests.createTest(cookie, "API Test for Generation", "generate questions");
        int testId = createTestResp.jsonPath().getInt("id");

        Map<String, Object> body = new HashMap<>();
        body.put("count", 3);
        body.put("type", "single_choice");
        body.put("testId", testId);
        body.put("topic", "math");

        Response resp = Questions.generateQuestions(cookie, body);
        resp.then()
                .statusCode(200)
                .contentType(containsString("application/json"));

        resp.then().body(matchesJsonSchemaInClasspath(
                "schemas/questions/GenerateQuestionsSuccessResponse.json"));

        assertThat(resp.jsonPath().getList("$"), hasSize(3));
        assertThat(resp.jsonPath().getList("type"), everyItem(equalTo("single_choice")));
    }
}
