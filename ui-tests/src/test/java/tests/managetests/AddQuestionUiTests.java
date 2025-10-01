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
@DisplayName("Add Question via UI")
public class AddQuestionUiTests extends BaseTest {

    private String uniqueName(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

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


}
