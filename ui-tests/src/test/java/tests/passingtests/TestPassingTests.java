package tests.passingtests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@DisplayName("Прохождение тестов")
public class TestPassingTests extends BaseTest {

    @Tag("SS-T53")
    @DisplayName("Verify successful assignment of the test 'Тест Прохождение тестов с одной опцией' to a candidate")
    @Test
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @Tag("SS-T13")
    @DisplayName("Get link to the test 'Тест Прохождение тестов с одной опцией'")
    @Test
    public void getLinkToTest () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .verifyThatLinkIsCopiedToClipboard();
    }

    @Tag("SS-T18")
    @DisplayName("Verify that passing test page opens correctly")
    @Test
    public void openPassingTestPage_SS_T18 () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .navigateToPassingTestPage()
                .verifyThatPassingTestPageOpensCorrectly();
    }
}
