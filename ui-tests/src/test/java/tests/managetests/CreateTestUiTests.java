package tests.managetests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

import java.util.regex.Pattern;


public class CreateTestUiTests extends BaseTest {

    @Test
    @DisplayName("Tests page opens")
    void openTestsPage() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
    }

//    @Test
//    @DisplayName("Create Test button is visible")
//    void createTestButtonIsVisible() {
//        TestsPage testsPage = new TestsPage(context);
//        testsPage.openTestsPage();
//        testsPage.verifyCreateTestButtonIsVisible();
//    }


    @Test
    @DisplayName("Create Test button is visible")
    void createTestButtonIsVisible() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        testsPage.verifyCreateTestButtonIsVisible();
    }

    @Test
    @DisplayName("Create Test modal appears after click")
    void createTestModalAppearsAfterClick() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyCreateTestButtonIsVisible();
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();
    }

    @Test
    @DisplayName("Create test")
    void createTest() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();

        String testName = "Autotest_" + System.currentTimeMillis();

        testsPage.enterTestName(testName);
        testsPage.enterTestDescription("description");
        testsPage.submitCreateTest();

        testsPage.verifyCreatedTestHeader(testName);
    }

    @Test
    @DisplayName("Create test with empty name shows error")
    void createTestWithEmptyName() {
        TestsPage testsPage = new TestsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();

        testsPage.enterTestName("");
        testsPage.enterTestDescription("description");
        testsPage.submitCreateTest();
        testsPage.verifyEmptyNameErrorVisible();
    }

}