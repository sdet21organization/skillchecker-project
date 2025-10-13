package tests.dashboard;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Dashboard;

@Epic("API Tests")
@Feature("Dashboard Access")
@Owner("Ko.Herasymets")
@DisplayName("Dashboard Access API Tests")
public class DashboardAccessTests extends BaseTest {

    @Test
    @Story("SS-T66")
    @Tag("negative")
    @DisplayName("[SS-T66][NEG] Dashboard/Stats — Доступ без авторизации → 401")
    void ssT66_stats_unauthorized_401() {
        Response r = Dashboard.getStats();
        ResponseVerifier.verifyUnauthorized(r);
    }

    @Test
    @Story("SS-T66")
    @Tag("negative")
    @DisplayName("[SS-T66][NEG] Dashboard/RecentActivity — Доступ без авторизации → 401")
    void ssT66_recentActivity_unauthorized_401() {
        Response r = Dashboard.getRecentActivity();
        ResponseVerifier.verifyUnauthorized(r);
    }
}