package tests.managetests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddQuestionUiTests extends BaseTest {

    private String uniqueName(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    @Test
    @DisplayName("Добавление вопроса: валидные данные → вопрос создаётся")
    void addQuestion_success() {

        final String testName = uniqueName("ui-test");
        final String description = "created by UI autotest";

        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        testsPage.verifyCreateTestButtonIsVisible();
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
        details.fillQuestionMinimal(
                "Сколько будет 2 + 2?",
                "3",
                "4",
                1
        );
        details.saveQuestionAndVerifyAppeared();
    }


}
