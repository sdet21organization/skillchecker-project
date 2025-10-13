package tests.dashboard;

import helpers.ResponseVerifier;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Dashboard;

@Epic("API Tests")
@DisplayName("API-tests for Dashboard")
public class DashboardApiTests extends BaseTest {


    @Test
    @DisplayName("Verify Dashboard Statistics Retrieval")
    void getDashboardStatsTest() {
        Response response = Dashboard.getStats(cookie);

        ResponseVerifier.verifyResponse(response, 200, "schemas/dashboard/GetStatsSuccessResponse.json");
    }

    @Test
    @DisplayName("Verify Retrieval of Recent Activity List")
    void getRecentActivityListTest() {
        Response response = Dashboard.getRecentActivity(cookie);

        ResponseVerifier.verifyResponse(response, 200, "schemas/dashboard/GetRecentActivitySuccessResponse.json");
    }
}