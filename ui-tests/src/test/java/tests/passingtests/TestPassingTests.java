package tests.passingtests;

import com.smartbear.zephyrscale.junit.annotation.TestCase;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@Epic("UI Tests")
@Feature("Tests Passing")
@DisplayName("Tests Passing - UI Tests")
public class TestPassingTests extends BaseTest {


    @DisplayName("Verify successful assignment of the test 'Тест Прохождение тестов с одной опцией' to a candidate")
    @Test
    @TestCase(key="SS-T53")
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @TestCase(key="SS-T13")
    @DisplayName("Get link to the test 'Тест Прохождение тестов с одной опцией'")
    @Test
    public void SS_T13 () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .verifyThatLinkIsCopiedToClipboard();
    }

    @DisplayName("Verify that passing test page opens correctly")
    @Test
    @TestCase(key="SS-T18")
    public void openPassingTestPage () {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .copyLinkToTest()
                .navigateToPassingTestPage()
                .verifyThatPassingTestPageOpensCorrectly();
    }
}
