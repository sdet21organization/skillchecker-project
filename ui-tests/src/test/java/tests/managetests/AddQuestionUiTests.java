package tests.managetests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Add Question via UI")
@Disabled("Disabled until app stabilization")
public class AddQuestionUiTests extends BaseTest {

    @Test
    @DisplayName("Add Question: successful addition of a question to a test")
    void addQuestion_success() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();

        TestDetailsPage details = new TestDetailsPage(context);
        details.verifyTestTitle(testName);
        details.openQuestionsSection();

        details.openAddQuestionModal();
        details.fillQuestionMinimal(
                "Сколько будет 2 + 2?",
                "3",
                "4",
                1
        );
        details.saveQuestionAndVerifyAppeared();
    }

    @Test
    @DisplayName("Add Question: validation fails when text is less than 5 characters")
    void addQuestion_validationError() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();

        TestDetailsPage details = new TestDetailsPage(context);
        details.verifyTestTitle(testName);
        details.openQuestionsSection();

        details.addInvalidQuestion("1234", "a", "b", 1);
        details.verifyValidationErrorShown(5);
    }

    @Test
    @DisplayName("Add Question: validation fails when name is empty")
    void addQuestion_validationError_emptyName() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        final String testName = testsPage.uniqueName("ui-test-neg");
        final String description = "created for validation";

        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();
        testsPage.enterTestName(testName);
        testsPage.enterTestDescription(description);
        testsPage.submitCreateTest();
        testsPage.waitTestCreatedAndHeaderIs(testName);

        TestDetailsPage details = new TestDetailsPage(context);
        details.verifyTestTitle(testName);
        details.openQuestionsSection();
        details.openAddQuestionModal();
        details.fillQuestionMinimal("", "a", "b", 1);
        details.verifyEmptyNameError();
    }

}
