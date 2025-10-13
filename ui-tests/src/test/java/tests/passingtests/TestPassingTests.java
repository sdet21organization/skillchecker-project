package tests.passingtests;

import io.qameta.allure.Epic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CandidatesPage;
import pages.TestsPage;
import tests.BaseTest;
import utils.ConfigurationReader;

@Epic("UI Tests")
@DisplayName("Tests passing")
public class TestPassingTests extends BaseTest {

    @DisplayName("Verify successful test assignment to a candidate via Tests page")
    @Test
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.for.successful.passing"))
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @DisplayName("Verify successful test assignment to a candidate via Candidates page")
    @Test
    public void assignTestViaCandidates() {

        new CandidatesPage(context)
                .open()
                .searchCandidateBy(ConfigurationReader.get("candidate.name"))
                .assignTestToCandidateViaCandidatesPage()
                .verifyThatTestIsSuccessfullyAssigned();
    }

    @DisplayName("Get link to the test and verify that link is copied to clipboard")
    @Test
    public void getLinkToTest() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.for.successful.passing"))
                .copyLinkTest()
                .verifyThatLinkIsCopiedToClipboard();
    }

    @DisplayName("Verify that passing test page opens correctly")
    @Test
    public void openPassingTestPage() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.for.successful.passing"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .verifyThatPassingTestPageOpensCorrectly();
    }

    @DisplayName("Successful filling and submitting the test 'With one option'")
    @Test
    public void passTest() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.for.successful.passing"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .startTest()
                .testPassing()
                .verifyThatTestIsPassed();
    }

    @DisplayName("Verify that 'Next' button is disabled until an option is selected")
    @Test
    public void verifyNextButtonDisabled() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.for.successful.passing"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .startTest()
                .verifyThatNextButtonIsDisabled();
    }

    @Disabled
    @DisplayName("Verify that test is closed when time is expired")
    @Test
    public void verifyTestClosesWhenTimeExpired() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("passing.time.test"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .getTimeToPassTheTest()
                .startTest()
                .verifyThatTestClosesWhenTimeExpired();
    }

    @DisplayName("Passing test with two options")
    @Test
    public void passTestWithTwoOptions() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.with.two.options"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .startTest()
                .passTestWithTwoOptions()
                .verifyThatTestWithTwoOptionsIsPassed();
    }

    @DisplayName("Passing test with text input option")
    @Test
    public void passTestWithTextInput() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption(ConfigurationReader.get("test.with.text.input"))
                .copyLinkTest()
                .navigateToPassingTestPage()
                .startTest()
                .passTestWithTextInput()
                .verifyThatTestWithTextInputOptionIsPassed();
    }
}
