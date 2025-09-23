package wrappers;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Users {

    @Step("GET /users")
    public static Response getAllUsers(String cookie) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie)
                .when().get("users")
                .then().extract().response();
    }

    @Step("POST /register")
    public static Response registerUser(String cookie, String email, String fullName, String password, String role, boolean active) {
        String body = String.format(
                "{\"email\":\"%s\",\"fullName\":\"%s\",\"password\":\"%s\",\"role\":\"%s\",\"active\":%s}",
                email, fullName, password, role, active
        );
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).body(body)
                .when().post("register")
                .then().extract().response();
    }

    @Step("DELETE /users/{id}")
    public static Response deleteUserById(String cookie, String id) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .when().delete("users/{id}")
                .then().extract().response();
    }

    @Step("PATCH /users/{id} set role")
    public static Response updateUserRole(String cookie, String id, String role) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .body(Map.of("role", role))
                .when().patch("users/{id}")
                .then().extract().response();
    }

    @Step("POST /users/{id}/reset-password")
    public static Response resetUserPassword(String cookie, String id, String newPassword) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .body(Map.of("newPassword", newPassword))
                .when().post("users/{id}/reset-password")
                .then().extract().response();
    }

    @Step("Find userId by email via GET /users")
    public static String findUserIdByEmail(String cookie, String email) {
        Response resp = getAllUsers(cookie);
        List<Map<String, Object>> users = resp.jsonPath().getList(""); // корневой массив
        if (users == null) return null;
        for (Map<String, Object> u : users) {
            if (email.equals(u.get("email"))) return String.valueOf(u.get("id"));
        }
        return null;
    }

    @Step("Delete user by email if exists")
    public static void deleteByEmailIfExists(String cookie, String email) {
        String id = findUserIdByEmail(cookie, email);
        if (id != null) {
            Response del = deleteUserById(cookie, id);
            if (del.statusCode() < 200 || del.statusCode() >= 300) {
                throw new AssertionError("Cleanup failed: expected 2xx, got " + del.statusCode()
                        + " body=" + del.asString());
            }
        }
    }

    @Step("PATCH /users/{id} set active")
    public static Response updateUserActive(String cookie, String id, boolean active) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .body(Map.of("active", active))
                .when().patch("users/{id}")
                .then().extract().response();
    }

}

