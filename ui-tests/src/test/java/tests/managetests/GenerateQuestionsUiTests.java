package tests.managetests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Epic("UI Tests")
@DisplayName("Генерация вопросов через UI")
public class GenerateQuestionsUiTests extends BaseTest {

    private String uniqueName(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    @Test
    @DisplayName("Генерация вопросов: позитивный сценарий → модалка открывается")
    void generateQuestions_success() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();

        String testName = testsPage.createTestFullyWithUniqueData();

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
