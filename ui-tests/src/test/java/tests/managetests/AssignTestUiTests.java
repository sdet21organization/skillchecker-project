package tests.managetests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Assign Test UI Tests")
@Disabled("Disabled until app stabilization")
public class AssignTestUiTests extends BaseTest {

    @Test
    @DisplayName("Assign Test to Candidate: positive scenario → test is assigned to candidate and appears in the list")
    void assignTestToCandidate() {
        TestsPage testsPage = new TestsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        testsPage.findTestPassingTestWithOneOption();
        testsPage.assignTestToCandidate();
        testsPage.verifyThatTestIsSuccessfullyAssigned();
    }
}
