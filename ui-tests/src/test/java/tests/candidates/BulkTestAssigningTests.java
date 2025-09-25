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
        assertEquals("3", candidatesPage.getBulkAssignButtonCounter(), "Счетчик на кнопке ʼНазначить тестʼ не равен ʼ3ʼ");

        List<String> candidatesListOnCandidatesPage = new ArrayList<>();
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(1));
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(2));
        candidatesListOnCandidatesPage.add(candidatesPage.getCndidateName(3));

        candidatesPage.clickBulkAssignButton();
        assertEquals("3", candidatesPage.getBulkAssignModalCounterValue(), "Счетчик в окне ʼНазначить тестʼ не равен ʼ3ʼ");
        assertEquals(candidatesListOnCandidatesPage, candidatesPage.getCandidatesListOnBulkAssignModal(), "Список кандидатов в окне ʼНазначить тестʼ не совпадает с выбранным списком");
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isDisabled(), "Кнопка ʼНазначить тестʼ в окне активна");
        assertTrue(candidatesPage.testInfo.isHidden(), "Блок информации о тесте показан до выбора теста");

        candidatesPage.chooseTestOnBulkAssignModal(testInfo.name);
        assertEquals(testInfo.description, candidatesPage.getTestDescriptionOnBulkModal(), "Описание теста в окне не совпадает с ожидаемым описанием теста");
        assertEquals(testInfo.executiontime, candidatesPage.getTestExecutionTimeOnBulkModal(), "Продолжительность теста в окне не совпадает с ожидаемой продолжительностью теста");
        assertEquals(testInfo.passingscore, candidatesPage.getTestPassingScoreOnBulkModal(), "Проходной бал теста в окне не совпадает с ожидаемым проходным балом теста");
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isEnabled(), "Кнопка ʼНазначить тестʼ в окне не активна");

        candidatesPage.choosePeriodOnBulkAssignModal("14 дней").clickSubmitBulkAssignModalButton();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(candidatesPage.toast.isVisible(), "Тоаст с сообщением об успешном назначении тестов не показан");
        assertTrue(candidatesPage.bulkAssignModalSubmitButton.isHidden(), "Кнока ʼНазначить тестʼ все еще отображется в окне");
        assertTrue(candidatesPage.testInfo.isHidden(), "Информация о тесте все еще отображется в окне");

        candidatesPage.clickCloseBulkAssignModalButton();
        assertTrue(candidatesPage.bulkAssigningModal.isHidden(), "Модальное окно ʼНазначить тестʼ не закрыто");

        CandidatePage candidatePage = new CandidatePage(context);

        candidatePage.open(1);
        List<String> actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name), "На странице {id}го кандидата не найдено назначенный тест");

        candidatePage.open(2);
        actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name), "На странице {id}го кандидата не найдено назначенный тест");

        candidatePage.open(3);
        actualListOfTests = candidatePage.getListOfTests();
        assertTrue(actualListOfTests.contains(BulkTestAssigningValidation.TESTINFO1.name), "На странице {id}го кандидата не найдено назначенный тест");
    }

    @Test
    @DisplayName("Назначение тестов более 500 кандидатам с неуспешным результатом")
    public void unsuccessfulBulkAssigningTests() {

        BulkTestAssigningValidation testInfo = BulkTestAssigningValidation.TESTINFO1;

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().sellectAllCandidates().clickBulkAssignButton().chooseTestOnBulkAssignModal(testInfo.name).clickSubmitBulkAssignModalButton();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(candidatesPage.toast.textContent().contains("Ошибка"), "Тоаст с описанием ошибки не показн");
    }
}
