package wrappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import static io.restassured.RestAssured.given;

@DisplayName("Назначение теста кандидату через API")
public class Sessions {
    private static final ObjectMapper om = new ObjectMapper();

    public static Response createSession(String cookie, Map<String, Object> body) throws JsonProcessingException {
        String json = om.writeValueAsString(body);
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(json)
                .when()
                .post("sessions")
                .then()
                .extract().response();
    }
}