package wrappers;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Dashboard {

    @Step("Get dashboard statistics")
    public static Response getStats(String cookie) {
        return given()
                .header("Cookie", cookie)
                .when()
                .get("stats")
                .then()
                .extract().response();
    }

    @Step("Get recent activity")
    public static Response getRecentActivity(String cookie) {
        return given()
                .header("Cookie", cookie)
                .when()
                .get("recent-activity")
                .then()
                .extract().response();
    }

    @Step("Get dashboard statistics (unauthorized)")
    public static Response getStats() {
        return given()
                .when()
                .get("stats")
                .then()
                .extract().response();
    }

    @Step("Get recent activity (unauthorized)")
    public static Response getRecentActivity() {
        return given()
                .when()
                .get("recent-activity")
                .then()
                .extract().response();
    }
}