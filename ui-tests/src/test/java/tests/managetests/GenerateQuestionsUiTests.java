package tests.managetests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestDetailsPage;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Generate Questions UI Tests")
@Disabled("Disabled until app stabilization")
public class GenerateQuestionsUiTests extends BaseTest {

    @Test
    @DisplayName("Generate Questions: positive scenario → questions are generated and appear in the list")
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
