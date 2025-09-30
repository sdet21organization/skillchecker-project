package tests.managetests;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Disabled("Временно отключаем все тесты класса")
@DisplayName("Генерация вопросов через UI")
public class GenerateQuestionsUiTests extends BaseTest {

    private String uniqueName(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    @Test
    @DisplayName("Генерация вопросов: позитивный сценарий → модалка открывается")
    void generateQuestions_success() {

        final String testName = uniqueName("ui-test");
        final String description = "created by UI autotest (generate questions)";

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

        details.clickGenerateQuestions();
        details.verifyGenerateQuestionsModalIsVisible();
        details.selectGenerateCount(3);
        details.selectGenerateType("Single Choice");
        details.confirmGenerateQuestions();
        details.waitGenerateModalClosed();

    }
}
