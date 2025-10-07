package tests.dashboard;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigurationReader;

import com.microsoft.playwright.Locator;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Dashboard")
@Feature("UI")
@DisplayName("Dashboard UI tests")
@Disabled("Disabled until app stabilization")
public class DashboardUiTests extends BaseTest {

    private DashboardPage openDashboardEnsuringAuth() {
        DashboardPage p = new DashboardPage(context).open();

        String baseUrl = ConfigurationReader.get("URL");
        LoginPage lp = new LoginPage(context);

        if (lp.isAtLoginPage(baseUrl)) {
            String email = ConfigurationReader.get("email");
            String password = ConfigurationReader.get("password");
            lp.login(email, password);
            context.page.waitForURL("**/dashboard");
        }
        return p.waitUntilReady();
    }

    @Test
    @DisplayName("should display correct numbers in all analytics widgets")
    void analyticsWidgets_showNonNegativeNumbers_andStableAfterIdle() {
        DashboardPage p = openDashboardEnsuringAuth();

        assertAll("Analytics widgets visible",
                () -> assertTrue(p.activeTestsCounter.isVisible(), "Active tests widget is not visible"),
                () -> assertTrue(p.candidatesCounter.isVisible(), "Candidates widget is not visible"),
                () -> assertTrue(p.pendingSessionsCounter.isVisible(), "Pending sessions widget is not visible"),
                () -> assertTrue(p.completedSessionsCounter.isVisible(), "Completed sessions widget is not visible")
        );

        int active     = p.asInt(p.activeTestsCounter);
        int candidates = p.asInt(p.candidatesCounter);
        int pending    = p.asInt(p.pendingSessionsCounter);
        int completed  = p.asInt(p.completedSessionsCounter);

        assertAll("Numbers are non-negative",
                () -> assertTrue(active >= 0, "Active < 0"),
                () -> assertTrue(candidates >= 0, "Candidates < 0"),
                () -> assertTrue(pending >= 0, "Pending < 0"),
                () -> assertTrue(completed >= 0, "Completed < 0")
        );

        p.waitUntilReady();
        assertEquals(active,     p.asInt(p.activeTestsCounter),     "Active changed unexpectedly");
        assertEquals(candidates, p.asInt(p.candidatesCounter),      "Candidates changed unexpectedly");
        assertEquals(pending,    p.asInt(p.pendingSessionsCounter), "Pending changed unexpectedly");
        assertEquals(completed,  p.asInt(p.completedSessionsCounter),"Completed changed unexpectedly");
    }

    @Test
    @DisplayName("should open the \"Create Test\" modal upon clicking the quick action button")
    void createTest_quickAction_opensModal() {
        DashboardPage p = openDashboardEnsuringAuth();
        Locator nameInput = p.openCreateTestModalAndWaitInput();
        assertTrue(nameInput.isVisible(),
                "Create Test modal's name input should be visible");
    }

    @Test
    @DisplayName("should open the \"Add Candidate\" modal upon clicking the quick action button")
    void addCandidate_quickAction_opensModal() {
        DashboardPage p = openDashboardEnsuringAuth();
        Locator nameInput = p.openAddCandidateModalAndWaitInput();
        assertTrue(nameInput.isVisible(),
                "Add Candidate modal's name input should be visible");
    }

    @Test
    @DisplayName("should display the list of recent activities")
    void recentActivity_list_isVisible_andHasItems() {
        DashboardPage p = openDashboardEnsuringAuth();
        assertTrue(p.recentActivityListContainer.isVisible(),
                "Recent activity container should be visible");
        assertTrue(p.recentActivityListItems.count() > 0,
                "Recent activity list should contain at least one item");
    }
}
