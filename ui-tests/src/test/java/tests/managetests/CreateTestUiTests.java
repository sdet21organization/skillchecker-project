package tests.managetests;

import com.smartbear.zephyrscale.junit.annotation.TestCase;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Create Test UI Tests")
@Disabled("Disabled until app stabilization")
public class CreateTestUiTests extends BaseTest {

    @Test
    @DisplayName("Open Tests Page")
    void openTestsPage() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
    }

    @Test
    @DisplayName("Open Tests Page and check 'Create Test' button is visible")
    void createTestButtonIsVisible() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyCreateTestButtonIsVisible();
    }

    @Test
    @DisplayName("'Create Test' modal appears after clicking the button")
    @TestCase(key="SS-T4")
    void createTestModalAppearsAfterClick() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyCreateTestButtonIsVisible();
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();
    }

    @Test
    @DisplayName("Create Test: positive scenario → test is created successfully")
    @TestCase(key="SS-T2")
    void createTest() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        testsPage.waitTestCreatedAndHeaderIs(testName);
    }

    @Test
    @DisplayName("Create Test with empty name: negative scenario → error message appears")
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

    @Test
    @DisplayName("Create Test with only name: positive scenario → test is created successfully")
    void createNewTest() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        testsPage.waitTestCreatedAndHeaderIs(testName);
    }
}
