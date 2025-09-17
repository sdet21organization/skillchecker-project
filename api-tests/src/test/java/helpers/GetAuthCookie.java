package helpers;

import dto.auth.LoginRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GetAuthCookie {
    public static String getAuthCookie() {
        LoginRequest loginPayload = new LoginRequest();
        loginPayload.setEmail(ConfigurationReader.get("email"));
        loginPayload.setPassword(ConfigurationReader.get("password"));

        Response response =
                given()
                        .contentType("application/json")
                        .body(loginPayload)
                        .when()
                        .post("login")
                        .then()
                        .assertThat()
                        .contentType(ContentType.JSON)
                        .extract().response();
       return response.getCookie("connect.sid");
    }
}
