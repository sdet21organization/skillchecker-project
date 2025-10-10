package tests.managetests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Edit Test UI Tests")
public class EditTestUiTests extends BaseTest {

    @Test
    @DisplayName("Open Edit Test Window")
    void openEditWindowTest() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.verifyEditButtonPresent();
        detailsPage.openEditMode();
        detailsPage.verifyEditFormVisible();
    }

    @Test
    @DisplayName("Edit test name: name is updated and shown in header")
    void editTestName_success() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage details = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String originalName = testsPage.createTestFullyWithUniqueData();
        details.verifyTestTitle(originalName);

        String newName = originalName + " (edited)";
        details.updateNameAndVerify(newName);
    }

    @Test
    @DisplayName("Change test description → new description is saved")
    void changeTestDescription_success() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage details = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        String testName = testsPage.createTestFullyWithUniqueData();
        details.verifyTestTitle(testName);

        String newDescription = "Updated description by UI autotest";
        details.updateDescriptionAndVerify(newDescription);
    }


    @Test
    @DisplayName("Edit test time limit: value is updated and success message is shown")
    void editTestTimeLimit_success() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.updateTimeLimitAndVerify(32);
    }

    @Test
    @DisplayName("Edit test passing score: value is updated and success message is shown")
    void editTestPassingScore_success() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.updatePassingScoreAndVerify(80);
    }


    @Test
    @DisplayName("Edit test active state: value is toggled and success message is shown")
    void editTestActiveState_success() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.toggleActiveStateAndVerify();
    }

    @Test
    @DisplayName("Edit active state: cancel button discards changes")
    void editActiveState_cancelChanges() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.cancelActiveStateChangeAndVerify();
    }


    @Test
    @DisplayName("Edit test name: validation fails when name is empty")
    void editTestName_validationError_empty() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.updateNameToEmptyAndVerifyError();
    }

    @Test
    @DisplayName("Edit test time limit: validation error when negative value entered")
    void editTestTimeLimit_negativeValue_validationError() {
        TestsPage testsPage = new TestsPage(context);
        TestDetailsPage detailsPage = new TestDetailsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();
        detailsPage.verifyTestTitle(testName);

        detailsPage.editTimeLimitToNegativeAndVerifyValidationError(-4);
    }

}
