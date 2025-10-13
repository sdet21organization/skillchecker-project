package tests.auth;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Auth;
import wrappers.Dashboard;

@Epic("API Tests")
@Feature("Auth/Logout")
@Owner("Ko.Herasymets")
@DisplayName("Auth/Logout API Tests")
public class LogoutTests extends BaseTest {

    @Test
    @Story("SS-T69")
    @Tag("positive")
    @DisplayName("[SS-T69][ACC] Auth/Logout — Logout делает Dashboard недоступной → 401")
    void ssT69_logout_makes_dashboard_unavailable() {
        Response logoutResp = Auth.logout(cookie);
        ResponseVerifier.verifyResponse(logoutResp, 200);

        Response dashboardResp = Dashboard.getStats();
        ResponseVerifier.verifyUnauthorized(dashboardResp);
    }
}