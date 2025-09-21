package wrappers;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class ManageTests {

    @Step("Создание теста через API")
    public static Response createTest(String cookie, String name, String description) {
        String body = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);

        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(body)
                .when()
                .post("/tests")
                .then()
                .extract().response();
    }

    @Step("Проверка JSon и статуса при создании теста")
    public static void verifyCreateTest(Response response) {
        response.then()
                .statusCode(201)
                .contentType(ContentType.JSON)

                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath("schemas.manage/CreateTestSuccessResponse.json"));
    }


}




