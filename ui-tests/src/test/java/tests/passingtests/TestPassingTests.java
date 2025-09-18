package tests.passingtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

public class TestPassingTests extends BaseTest {

    @DisplayName("Verify successful assignment of the test 'Тест Прохождение тестов с одной опцией' to a candidate")
    @Test
    public void assignTestViaTests() {

        new TestsPage(context)
                .openTestsPage()
                .findTestPassingTestWithOneOption()
                .assignTestToCandidate()
                .verifyThatTestIsSuccessfullyAssigned();
    }
}
