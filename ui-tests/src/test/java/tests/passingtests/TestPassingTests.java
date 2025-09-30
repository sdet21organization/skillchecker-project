package tests.passingtests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@DisplayName("Прохождение тестов")
public class TestPassingTests extends BaseTest {

    @DisplayName("Verify successful assignment of the test 'Тест Прохождение тестов с одной опцией' to a candidate")
    @Epic("UI Tests")
    @Test
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @DisplayName("Get link to the test 'Тест Прохождение тестов с одной опцией'")
    @Epic("UI Tests")
    @Test
    public void getLinkToTest () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .verifyThatLinkIsCopiedToClipboard();
    }

    @DisplayName("Verify that passing test page opens correctly")
    @Epic("UI Tests")
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
