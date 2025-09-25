package wrappers;

import dto.users.RegisterUserRequest;
import dto.users.ResetPasswordRequest;
import dto.users.UpdateUserRequest;
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

    @Step("POST /register (DTO)")
    public static Response registerUser(String cookie, RegisterUserRequest req) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie)
                .body(req)
                .when().post("register")
                .then().extract().response();
    }

    public static Response registerUser(String cookie, String email, String fullName, String password, String role, boolean active) {
        RegisterUserRequest r = new RegisterUserRequest();
        r.setEmail(email);
        r.setFullName(fullName);
        r.setPassword(password);
        r.setRole(role);
        r.setActive(active);
        return registerUser(cookie, r);
    }

    @Step("PATCH /users/{id} (DTO)")
    public static Response updateUser(String cookie, String id, UpdateUserRequest req) {
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .body(req)
                .when().patch("users/{id}")
                .then().extract().response();
    }

    @Step("PATCH /users/{id} set role")
    public static Response updateUserRole(String cookie, String id, String role) {
        UpdateUserRequest u = new UpdateUserRequest();
        u.setRole(role);
        return updateUser(cookie, id, u);
    }

    @Step("PATCH /users/{id} set active")
    public static Response updateUserActive(String cookie, String id, boolean active) {
        UpdateUserRequest u = new UpdateUserRequest();
        u.setActive(active);
        return updateUser(cookie, id, u);
    }

    @Step("POST /users/{id}/reset-password")
    public static Response resetUserPassword(String cookie, String id, String newPassword) {
        ResetPasswordRequest r = new ResetPasswordRequest();
        r.setNewPassword(newPassword);
        return given()
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .header("Cookie", cookie).pathParam("id", id)
                .body(r)
                .when().post("users/{id}/reset-password")
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


    @Step("Find userId by email via GET /users")
    public static String findUserIdByEmail(String cookie, String email) {
        Response resp = getAllUsers(cookie);
        List<Map<String, Object>> users = resp.jsonPath().getList("");
        if (users == null) return null;
        for (Map<String, Object> u : users) {
            if (email.equals(u.get("email"))) {
                return String.valueOf(u.get("id"));
            }
        }
        return null;
    }

    @Step("Delete user by email if exists")
    public static void deleteByEmailIfExists(String cookie, String email) {
        String id = findUserIdByEmail(cookie, email);
        if (id != null) deleteUserById(cookie, id);
    }

    @Step("Ensure user exists -> return id")
    public static String ensureUserExists(String cookie, String email, String fullName, String password, String role) {
        String id = findUserIdByEmail(cookie, email);
        if (id != null) return id;
        Response r = registerUser(cookie, email, fullName, password, role, true);
        if (r.statusCode() != 201) throw new AssertionError("Can't create user, body=" + r.asString());
        return String.valueOf(r.jsonPath().getInt("id"));
    }
}

