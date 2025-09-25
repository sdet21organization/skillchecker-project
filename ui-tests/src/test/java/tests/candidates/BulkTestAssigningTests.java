package tests.candidates;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.CandidatePage;
import pages.CandidatesPage;
import testdata.BulkTestAssigningValidation;
import tests.BaseTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Масовое назначение тестов кандидатам")
public class BulkTestAssigningTests extends BaseTest {

    @Test
    @DisplayName("Назначение тестов нескольким кандидатам с успешным результатом")
    public void fullSuccessfulBulkAssigningTests() {

        BulkTestAssigningValidation testInfo = BulkTestAssigningValidation.TESTINFO1;

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().checkCndidate(1).checkCndidate(2).checkCndidate(3);
        assertEquals("3", candidatesPage.getBulkAssignButtonCounter());

        List<String> candidatesListOnCandidatesPage = new ArrayList<>();
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(1));
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(2));
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(3));

        candidatesPage.clickBulkAssignButton();
        assertEquals("3", candidatesPage.getBulkAssignModalCounterValue());
        assertEquals(candidatesListOnCandidatesPage, candidatesPage.getCandidatesListOnBulkAssignModal());
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isDisabled());
        assertTrue(candidatesPage.testInfo.isHidden());

        candidatesPage.chooseTestOnBulkAssignModal(testInfo.name);
        assertEquals(testInfo.description, candidatesPage.getTestDescriptionOnBulkModal());
        assertEquals(testInfo.executiontime, candidatesPage.getTestExecutionTimeOnBulkModal());
        assertEquals(testInfo.passingscore, candidatesPage.getTestPassingScoreOnBulkModal());
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isEnabled());

        candidatesPage.choosePeriodOnBulkAssignModal("14 дней").clickSubmitBulkAssignModalButton();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(candidatesPage.toast.isVisible());
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isHidden());
        assertTrue(candidatesPage.testInfo.isHidden());

        candidatesPage.clickCloseBulkAssignModalButton();
        assertTrue(candidatesPage.bulkAssigningModal.isHidden());

        CandidatePage candidatePage = new CandidatePage(context);

        candidatePage.open(1);
        List<String> actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name));

        candidatePage.open(2);
        actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name));

        candidatePage.open(3);
        actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name));
    }

    @Test
    @DisplayName("Назначение тестов более 500 кандидатам с неуспешным результатом")
    public void unsuccessfulBulkAssigningTests() {

        BulkTestAssigningValidation testInfo = BulkTestAssigningValidation.TESTINFO1;

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().sellectAllCandidates().clickBulkAssignButton().chooseTestOnBulkAssignModal(testInfo.name).clickSubmitBulkAssignModalButton();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(candidatesPage.toast.textContent().contains("Ошибка"));
    }
}
