package tests.candidates;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSources;
import pages.CandidatesPage;
import testdata.AddCandidateValidation;
import tests.BaseTest;

@DisplayName("Candidates tests")
public class AddCandidateTests extends BaseTest {

    Faker fakerData = new Faker();


    @Test
    @DisplayName("Check successful Candidate adding")
    public void addNewCandidate() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);
        String position = fakerData.job().position();

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage
                .open()
                .clickAddCandidateButton()
                .fillName(name)
                .fillEmail(email)
                .fillPosition(position)
                .clickModalButtonAddCandidate()
                .searchCandidateBy(email);

        Assertions.assertEquals(name, candidatesPage.candidateEntryName.textContent());
    }


    @ParameterizedTest
    @DisplayName("Check Modal's validation errors when adding Candidate")
    @EnumSource(AddCandidateValidation.class)
    public void addNewCandidateValidationErrors(AddCandidateValidation addCandidateValidation) {

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage
                .open()
                .clickAddCandidateButton()
                .fillName(addCandidateValidation.getName())
                .fillEmail(addCandidateValidation.getEmail())
                .clickModalButtonAddCandidate();

        String actualError = candidatesPage.getModalErrorMessageText();
        Assertions.assertEquals(addCandidateValidation.getExpectedError(),actualError );
    }

    @Test
    @DisplayName("Check error notification while adding candidate that was already added")
    public void addExistingCandidateError() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage
                .open()
                .clickAddCandidateButton()
                .fillName(name)
                .fillEmail(email)
                .clickModalButtonAddCandidate()
                .clickAddCandidateButton()
                .fillName(name)
                .fillEmail(email)
                .clickModalButtonAddCandidate();

        Assertions.assertTrue(candidatesPage.errorToast.isVisible());
    }

    @Test
    @DisplayName("Check closing modal 'Добавить кандидата'")
    public void closeAddCandidateModal() {

        String name = fakerData.name().lastName();
        String email = fakerData.internet().safeEmailAddress(name);

        CandidatesPage candidatesPage = new CandidatesPage(context);
        candidatesPage
                .open()
                .clickAddCandidateButton()
                .fillName(name)
                .clickCancelModalButton();

        Assertions.assertTrue(candidatesPage.addCandidateModalTitle.isHidden());
    }

}
