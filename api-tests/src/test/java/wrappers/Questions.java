package wrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class Questions {

    private static final ObjectMapper om = new ObjectMapper();

    @Step("Создание вопроса через API")
    public static Response createQuestion(String cookie, Map<String, Object> body) throws JsonProcessingException {
        String json = om.writeValueAsString(body);
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(json)
                .when()
                .post("/api/questions")
                .then()
                .extract().response();
    }

    @Step("Генерация вопросов через API")
    public static Response generateQuestions(String cookie, Map<String, Object> body) throws JsonProcessingException {
        // Если у генерации другой endpoint — просто поменяй путь:
        // пример: /api/questions/generate
        String json = om.writeValueAsString(body);
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(json)
                .when()
                .post("/api/questions/generate")
                .then()
                .extract().response();
    }

    @Step("Проверка успешного ответа создания вопроса (201 + схема)")
    public static void verifyCreate(Response response, int expectedTestId) {
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("id", org.hamcrest.Matchers.notNullValue())
                .body("testId", org.hamcrest.Matchers.equalTo(expectedTestId))
                .body("type", org.hamcrest.Matchers.notNullValue())
                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath("schemas.questions/CreateQuestionSuccessResponse.json"));
    }

    @Step("Проверка успешной генерации (200/201 + непустой массив)")
    public static void verifyGenerate(Response response) {
        response.then()
                .statusCode(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is(200),
                        org.hamcrest.Matchers.is(201)))
                .contentType(ContentType.JSON)
                .body("$", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty()))
                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath("schemas.questions/GenerateQuestionsSuccessResponse.json"));
    }
}
