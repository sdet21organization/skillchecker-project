package tests.sidebar;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.DashboardPage;
import pages.LoginPage;
import pages.SidebarPage;
import tests.BaseTest;
import utils.ConfigurationReader;

import static org.junit.jupiter.api.Assertions.*;

@Epic("UI Tests")
@DisplayName("Sidebar navigation Web-tests")
public class SidebarNavigationTests extends BaseTest {

    private void openDashboardEnsuringAuth() {
        DashboardPage p = new DashboardPage(context).open();
        String baseUrl = ConfigurationReader.get("URL");
        LoginPage lp = new LoginPage(context);
        if (lp.isAtLoginPage(baseUrl)) {
            lp.login(ConfigurationReader.get("email"), ConfigurationReader.get("password"));
            context.page.waitForURL("**/dashboard");
        }
        p.waitUntilReady();
    }


    @Test
    @DisplayName("should navigate to Candidates and highlight the menu item")
    void shouldNavigateToCandidates() {
        openDashboardEnsuringAuth();
        SidebarPage sb = new SidebarPage(context).waitUntilReady();

        sb.clickAndAssert(sb.candidatesItem, "/dashboard/candidates");
        assertTrue(sb.isActive(sb.candidatesItem), "Меню 'Кандидаты' должен быть активным");
    }

    @Test
    @DisplayName("should navigate to Dashboard and highlight the menu item")
    void shouldNavigateToDashboard() {
        openDashboardEnsuringAuth();
        SidebarPage sb = new SidebarPage(context).waitUntilReady();

        sb.clickAndAssert(sb.dashboardItem, "/dashboard");
        assertTrue(sb.isActive(sb.dashboardItem), "Меню 'Панель управления' должен быть активным");
    }

    @Test
    @DisplayName("should navigate to Tests and highlight the menu item")
    void shouldNavigateToTests() {
        openDashboardEnsuringAuth();
        SidebarPage sb = new SidebarPage(context).waitUntilReady();

        sb.clickAndAssert(sb.testsItem, "/dashboard/tests");
        assertTrue(sb.isActive(sb.testsItem), "Меню 'Тесты' должен быть активным");
    }

    @Test
    @DisplayName("should navigate to Interviews and highlight the menu item")
    void shouldNavigateToInterviews() {
        openDashboardEnsuringAuth();
        SidebarPage sb = new SidebarPage(context).waitUntilReady();

        sb.clickAndAssert(sb.interviewsItem, "/dashboard/interviews");
        assertTrue(sb.isActive(sb.interviewsItem), "Меню 'Интервью' должен быть активным");
    }

    @Test
    @DisplayName("should navigate to Settings and highlight the menu item")
    void shouldNavigateToSettings() {
        openDashboardEnsuringAuth();
        SidebarPage sb = new SidebarPage(context).waitUntilReady();

        sb.clickAndAssert(sb.settingsItem, "/dashboard/settings");
        assertTrue(sb.isActive(sb.settingsItem), "Меню 'Настройки' должен быть активным");
    }
}
