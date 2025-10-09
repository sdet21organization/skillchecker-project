package tests.auth;

import helpers.ConfigurationReader;
import helpers.ResponseVerifier;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Auth;
import wrappers.Dashboard;

@DisplayName("[SS-T69] ACC Auth/Logout — Logout делает Dashboard недоступной")
public class LogoutTests extends BaseTest {

    @Test
    @DisplayName("локальная сессия: logout 200 → stats 401/403")
    void logout_then_forbidden() {
        Response login = Auth.loginUser(ConfigurationReader.get("email"), ConfigurationReader.get("password"));
        String localCookie = "connect.sid=" + login.getCookie("connect.sid");

        Response out = Auth.logout(localCookie);
        ResponseVerifier.verifyResponse(out, 200);

        Response stats = Dashboard.getStats(localCookie);
        ResponseVerifier.verifyUnauthorized(stats);
    }
}