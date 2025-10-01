package wrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import java.util.Map;
import static io.restassured.RestAssured.given;

@DisplayName("Questions API Wrapper")
public class Questions {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Response createQuestion(String cookie, Map<String, Object> body) throws JsonProcessingException {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(objectMapper.writeValueAsString(body))
                .when()
                .post("questions")
                .then()
                .extract().response();
    }

    public static Response generateQuestions(String cookie, Map<String, Object> body) throws JsonProcessingException {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(objectMapper.writeValueAsString(body))
                .when()
                .post("/generate-questions")
                .then()
                .extract().response();
    }


}
