package tests.dashboard;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Dashboard;

@Epic("Dashboard")
@Owner("Ваше Имя")
@DisplayName("API-тесты для страницы Dashboard")
public class DashboardApiTests extends BaseTest {


    @Test
    @DisplayName("Проверка получения статистики дашборда")
    void getDashboardStatsTest() {
        Response response = Dashboard.getStats(cookie);

        ResponseVerifier.verifyResponse(response, 200, "schemas/dashboard/GetStatsSuccessResponse.json");
    }

    @Test
    @DisplayName("Проверка получения списка недавней активности")
    void getRecentActivityListTest() {
        Response response = Dashboard.getRecentActivity(cookie);

        ResponseVerifier.verifyResponse(response, 200, "schemas/dashboard/GetRecentActivitySuccessResponse.json");
    }
}