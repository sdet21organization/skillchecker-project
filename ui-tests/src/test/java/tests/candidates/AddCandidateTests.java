package tests.candidates;

import com.github.javafaker.Faker;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pages.CandidatesPage;
import testdata.AddCandidateValidation;
import tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Добавление нового кандидата")
public class AddCandidateTests extends BaseTest {

    Faker fakerData = new Faker();

    @Test
    @DisplayName("Проверка успешного добавления Кандидата")
    public void addNewCandidate() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);
        String position = fakerData.job().position();

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();
        candidatesPage.clickAddCandidateButton();
        candidatesPage.fillName(name).fillEmail(email);
        candidatesPage.fillPosition(position);
        candidatesPage.clickModalButtonAddCandidate();
        candidatesPage.searchCandidateBy(email);

        assertEquals(name, candidatesPage.candidatesTableNames.textContent(), "Кандидат не найден на страниу");
    }


    @ParameterizedTest
    @DisplayName("Проверка валидационных сообщений об ошибке на модалке ʼДобавить кандидатаʼ")
    @EnumSource(AddCandidateValidation.class)
    public void addNewCandidateValidationErrors(AddCandidateValidation addCandidateValidation) {

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();
        candidatesPage.clickAddCandidateButton();
        candidatesPage.fillName(addCandidateValidation.getName());
        candidatesPage.fillEmail(addCandidateValidation.getEmail());
        candidatesPage.clickModalButtonAddCandidate();

        String actualError = candidatesPage.getModalErrorMessageText();
        assertEquals(addCandidateValidation.getExpectedError(), actualError, "Валидационное сообщение об ошибке в модальном окне не совпадает с ожидаемым результатом");
    }

    @Test
    @DisplayName("Проверка невозможности добавления кандидата с уже существующим емейлом")
    public void addExistingCandidateError() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();
        candidatesPage.clickAddCandidateButton();
        candidatesPage.fillName(name);
        candidatesPage.fillEmail(email);
        candidatesPage.clickModalButtonAddCandidate();
        context.page.waitForTimeout(1500);

        candidatesPage.clickAddCandidateButton();
        candidatesPage.fillName(name).fillEmail(email);
        candidatesPage.clickModalButtonAddCandidate();

        assertTrue(candidatesPage.toast.isVisible(), "Тоаст с сообщением об ошибке не показан");
    }

    @Test
    @DisplayName("Проверка закрытия модалки 'Добавить кандидата'")
    public void closeAddCandidateModal() {

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open();
        candidatesPage.clickAddCandidateButton();
        candidatesPage.clickCancelModalButton();
        candidatesPage.addCandidateModalTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));



        assertTrue(candidatesPage.addCandidateModalTitle.isHidden(), "Модалка ʼДобавить кандидатаʼ не закрыта");
    }
}