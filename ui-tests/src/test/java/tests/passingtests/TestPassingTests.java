package tests.passingtests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@DisplayName("Прохождение тестов")
public class TestPassingTests extends BaseTest {

    @Epic("UI Tests")
    @DisplayName("Verify successful assignment of the test 'Тест Прохождение тестов с одной опцией' to a candidate")
    @Test
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @Epic("UI Tests")
    @DisplayName("Get link to the test 'Тест Прохождение тестов с одной опцией'")
    @Test
    public void getLinkToTest () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .verifyThatLinkIsCopiedToClipboard();
    }

    @Epic("UI Tests")
    @DisplayName("Verify that passing test page opens correctly")
    @Test
    public void openPassingTestPage () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .navigateToPassingTestPage()
                .verifyThatPassingTestPageOpensCorrectly();
    }
}
