package tests.candidates;

import com.github.javafaker.Faker;
import com.microsoft.playwright.options.LoadState;
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
        candidatesPage.open().clickAddCandidateButton().fillName(name).fillEmail(email).fillPosition(position).clickModalButtonAddCandidate().searchCandidateBy(email);
        assertEquals(name, candidatesPage.candidatesTableNames.textContent());
    }


    @ParameterizedTest
    @DisplayName("Проверка валидационных сообщений об ошибке на модалке ʼДобавить кандидатаʼ")
    @EnumSource(AddCandidateValidation.class)
    public void addNewCandidateValidationErrors(AddCandidateValidation addCandidateValidation) {

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().clickAddCandidateButton().fillName(addCandidateValidation.getName()).fillEmail(addCandidateValidation.getEmail()).clickModalButtonAddCandidate();

        String actualError = candidatesPage.getModalErrorMessageText();
        assertEquals(addCandidateValidation.getExpectedError(), actualError);
    }

    @Test
    @DisplayName("Проверка невозможности добавления кандидата с уже существующим емейлом")
    public void addExistingCandidateError() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().clickAddCandidateButton().fillName(name).fillEmail(email).clickModalButtonAddCandidate().clickAddCandidateButton().fillName(name).fillEmail(email).clickModalButtonAddCandidate();
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        assertTrue(candidatesPage.toast.isVisible());
    }

    @Test
    @DisplayName("Проверка закрытия модалки 'Добавить кандидата'")
    public void closeAddCandidateModal() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage.open().clickAddCandidateButton().fillName(name).clickCancelModalButton();
        assertTrue(candidatesPage.addCandidateModalTitle.isHidden());
    }

}
