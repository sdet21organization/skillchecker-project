package tests.managetests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@DisplayName("Назначение теста кандидату через UI")
public class AssignTestUiTests extends BaseTest {

    @Test
    @DisplayName("Назначение теста кандидату")
    void assignTestToCandidate() {
        TestsPage testsPage = new TestsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        testsPage.findTestPassingTestWithOneOption();
        testsPage.assignTestToCandidate();
        testsPage.verifyThatTestIsSuccessfullyAssigned();
    }
}
