package tests.dashboard;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Dashboard;

@Epic("API Tests")
@DisplayName("[SS-T66] ACC Auth — Доступ к Dashboard без логина")
public class DashboardAccessTests extends BaseTest {

    @Test
    @DisplayName("GET /api/stats без авторизации → 401/403")
    void stats_unauthorized() {
        Response r = Dashboard.getStats();
        ResponseVerifier.verifyUnauthorized(r);
    }

    @Test
    @DisplayName("GET /api/recent-activity без авторизации → 401/403")
    void recent_unauthorized() {
        Response r = Dashboard.getRecentActivity();
        ResponseVerifier.verifyUnauthorized(r);
    }
}