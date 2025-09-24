package pages;

import com.microsoft.playwright.Locator;
import context.TestContext;
import io.qameta.allure.Step;

public class CandidatesPage {

    TestContext context;

    public Locator addCandidateButton;
    public Locator searchInput;
    public Locator candidateEntryName;
    public Locator addCandidateModalTitle;
    public Locator nameModalField;
    public Locator emailModalField;
    public Locator positionModalField;
    public Locator addCandidateModalButton;
    public Locator cancelModalButton;
    public Locator modalErrorMessage;
    public Locator errorToast;

    public CandidatesPage(TestContext context) {
        this.context = context;
        this.addCandidateButton = context.page.locator("[data-testid='add-candidate-button']");
        this.searchInput = context.page.locator("[data-testid='candidates-search-input']");
        this.candidateEntryName = context.page.locator("a[data-testid^='candidate-link-']");
        this.addCandidateModalTitle = context.page.locator("h2.text-lg");
        this.nameModalField = context.page.locator("input[name='name']");
        this.emailModalField = context.page.locator("input[name='email']");
        this.positionModalField = context.page.locator("input[name='position']");
        this.addCandidateModalButton = context.page.locator("button[type='submit']");
        this.cancelModalButton = context.page.locator("[data-testid='candidate-modal-cancel-button']");
        this.modalErrorMessage = context.page.locator("p.text-destructive");
        this.errorToast = context.page.locator("div.gap-1");
    }

    @Step("Open page 'Кандидаты'")
    public CandidatesPage open() {
        context.page.navigate("https://skillchecker.tech/dashboard/candidates");
        return this;
    }

    @Step("Click button 'Создать кандидата'")
    public CandidatesPage clickAddCandidateButton() {
        addCandidateButton.click();
        return this;
    }

    @Step("Enter value in field 'Имя'")
    public CandidatesPage fillName(String name) {
        nameModalField.fill(name);
        return this;
    }

    @Step("Enter value in field 'Email'")
    public CandidatesPage fillEmail(String email) {
        emailModalField.fill(email);
        return this;
    }

    @Step("Enter value in field 'Должность'")
    public CandidatesPage fillPosition(String position) {
        positionModalField.fill(position);
        return this;
    }

    @Step("Click modal button 'Добавить кандидата'")
    public CandidatesPage clickModalButtonAddCandidate() {
        addCandidateModalButton.click();
        return this;
    }

    @Step("Search candidate by {keyword}")
    public CandidatesPage searchCandidateBy(String keyword) {
        searchInput.fill(keyword);
        return this;
    }

    @Step ("Get actual error validation message")
    public String getModalErrorMessageText (){
        return modalErrorMessage.textContent();
    }

    @Step ("Click button ʼОтменаʼ")
    public CandidatesPage clickCancelModalButton(){
        cancelModalButton.click();
        return this;
    }
}

