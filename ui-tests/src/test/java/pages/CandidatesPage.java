package pages;

import com.microsoft.playwright.Locator;
import context.TestContext;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.options.WaitForSelectorState.HIDDEN;
import static com.microsoft.playwright.options.WaitForSelectorState.VISIBLE;

public class CandidatesPage {

    TestContext context;


    private final Locator addCandidateButton;
    private final Locator bulkAssignButton;
    private final Locator bulkAssignButtonCounter;
    private final Locator searchInput;
    private final Locator loadingState;
    private final Locator candidatesTableChcekboxes;
    private final Locator sellectAllCheckbox;
    public final Locator candidatesTableNames;

    private final Locator importButton;
    public final Locator importInfoStatus;
    private final Locator importSubmitImportModalButton;
    public final Locator fileUploadImportModalButton;
    private final Locator importCancelImportModalButton;


    public final Locator addCandidateModalTitle;
    private final Locator nameModalField;
    private final Locator emailModalField;
    private final Locator positionModalField;
    private final Locator addCandidateModalButton;
    private final Locator cancelModalButton;
    private final Locator modalErrorMessage;
    public final Locator bulkAssigningModal;
    private final Locator bulkAssignModalCounter;
    private final Locator bulkAssignModalCandidatesList;
    private final Locator bulkAssignModalChooseTestDropdown;
    private final Locator bulkAssignModalChooseTestDropdownList;
    private final Locator bulkAssignModalChoosePeriodDropdown;
    public final Locator testInfo;
    public final Locator bulkAssignModalSubmitButton;
    private final Locator bulkAssignModalCancelButton;
    private final Locator bulkAssignModalCloseButton;
    public final Locator toast;

    public CandidatesPage(TestContext context) {
        this.context = context;
        this.addCandidateButton = context.page.locator("[data-testid='add-candidate-button']");
        this.bulkAssignButton = context.page.locator("[data-testid='bulk-assign-button']");
        this.bulkAssignButtonCounter = context.page.locator("[data-testid='bulk-assign-button'] span:nth-of-type(2)");
        this.searchInput = context.page.locator("[data-testid='candidates-search-input']");
        this.loadingState = context.page.getByText("Загрузка...");
        this.candidatesTableChcekboxes = context.page.locator("[data-testid^='candidate-checkbox-']");
        this.sellectAllCheckbox = context.page.locator("[data-testid='select-all-checkbox']");
        this.candidatesTableNames = context.page.locator("[data-testid^='candidate-link-']");

        this.importButton = context.page.locator("button[class*='inline-flex'] span:has(svg.lucide-download)");
        this.importInfoStatus = context.page.locator("div[role*=\"dialog\"] div.space-y-1");
        this.importSubmitImportModalButton = context.page.locator("[data-testid='import-submit-button']");
        this.fileUploadImportModalButton = context.page.locator("[data-testid='file-upload-input']");
        this.importCancelImportModalButton = context.page.locator("[data-testid='import-cancel-button']");

        this.addCandidateModalTitle = context.page.locator("h2.text-lg");
        this.nameModalField = context.page.locator("input[name='name']");
        this.emailModalField = context.page.locator("input[name='email']");
        this.positionModalField = context.page.locator("input[name='position']");
        this.addCandidateModalButton = context.page.locator("button[type='submit']");
        this.cancelModalButton = context.page.locator("[data-testid='candidate-modal-cancel-button']");
        this.modalErrorMessage = context.page.locator("p.text-destructive");
        this.bulkAssigningModal = context.page.locator("[role='dialog']");
        this.bulkAssignModalCounter = context.page.locator("[role='dialog'] div.gap-2 span");
        this.bulkAssignModalCandidatesList = context.page.locator("[role='dialog'] div.text-sm");
        this.bulkAssignModalChooseTestDropdown = context.page.locator("div[role='dialog'] [aria-controls='radix-:rm:']");
        this.bulkAssignModalChooseTestDropdownList = context.page.locator("[role='listbox']");
        this.bulkAssignModalChoosePeriodDropdown = context.page.locator("div[role='dialog'] [aria-controls='radix-:rn:']");
        this.testInfo = context.page.locator("[role='dialog'] div.p-3 div");
        this.bulkAssignModalSubmitButton = context.page.locator("[data-testid='bulk-assign-submit-button']");
        this.bulkAssignModalCancelButton = context.page.locator("[data-testid='bulk-assign-cancel-button']");
        this.bulkAssignModalCloseButton = context.page.locator("[data-testid='bulk-assign-close-button']");
        this.toast = context.page.locator("div.gap-1");
    }

    @Step("Открыть страницу 'Кандидаты'")
    public CandidatesPage open() {
        context.page.navigate(ConfigurationReader.get("URL") + "dashboard/candidates");
        loadingState.waitFor(new Locator.WaitForOptions().setState(HIDDEN));
        context.page.waitForTimeout(1500);
        return this;
    }

    @Step("Нажать кнопку 'Создать кандидата'")
    public CandidatesPage clickAddCandidateButton() {
        addCandidateButton.waitFor(new Locator.WaitForOptions().setState(VISIBLE));
        addCandidateButton.click();
        return this;
    }

    @Step("Нажать кнопку 'Назначить тест'")
    public CandidatesPage clickBulkAssignButton() {
        bulkAssignButton.click();
        return this;
    }

    @Step("Получить значения счетчика на кнопке 'Назначить тест'")
    public String getBulkAssignButtonCounter() {
        return bulkAssignButtonCounter.textContent();
    }

    @Step("Отметить {n}-го кандидата в таблице")
    public CandidatesPage checkCndidate(int n) {
        candidatesTableChcekboxes.nth(n - 1).click();
        return this;
    }

    @Step("Отметить всех кандидатов в таблице")
    public CandidatesPage sellectAllCandidates() {
        sellectAllCheckbox.click();
        return this;
    }

    @Step("Получить имя {n}-го кандидата в таблице")
    public String getCndidateName(int n) {
        return candidatesTableNames.nth(n - 1).textContent();
    }


    @Step("Ввести значение в поле 'Имя'")
    public CandidatesPage fillName(String name) {
        nameModalField.fill(name);
        return this;
    }

    @Step("Ввести значение в поле 'Email'")
    public CandidatesPage fillEmail(String email) {
        emailModalField.fill(email);
        return this;
    }

    @Step("Ввести значение в поле 'Должность'")
    public CandidatesPage fillPosition(String position) {
        positionModalField.fill(position);
        return this;
    }

    @Step("Нажать кнопку 'Добавить кандидата'")
    public CandidatesPage clickModalButtonAddCandidate() {
        addCandidateModalButton.click();
        return this;
    }

    @Step("Искать кандидата по: {keyword}")
    public CandidatesPage searchCandidateBy(String keyword) {
        searchInput.fill(keyword);
        return this;
    }

    @Step("Получить текст об ошибке из модалки")
    public String getModalErrorMessageText() {
        return modalErrorMessage.textContent();
    }

    @Step("Нажать кнопку ʼОтменаʼ")
    public CandidatesPage clickCancelModalButton() {
        cancelModalButton.click();
        return this;
    }

    @Step("Получаем значение счетчика на модалке ʼНазначить тестʼ")
    public String getBulkAssignModalCounterValue() {
        return bulkAssignModalCounter.textContent().replaceAll("\\D+", "");
    }

    @Step("Получить список кандидатов, который отображен на модалке ʼНазначить тестʼ")
    public List<String> getCandidatesListOnBulkAssignModal() {
        String candidates = bulkAssignModalCandidatesList.textContent();
        return  Arrays.stream(candidates.split(", ")).toList();
    }

    @Step("Выбрать тест {test} из списка ʼВыберите тестʼ")
    public CandidatesPage chooseTestOnBulkAssignModal(String testName) {
        bulkAssignModalChooseTestDropdown.click();
        bulkAssignModalChooseTestDropdownList.locator("text=" + testName).click();
        return this;
    }

    @Step("Выбрать период ʼ{period}ʼ из списка ʼСрок действия ссылки (дней)ʼ")
    public CandidatesPage choosePeriodOnBulkAssignModal(String period) {
        bulkAssignModalChoosePeriodDropdown.click();
        context.page.locator("text=" + period).click();
        return this;
    }

    @Step("Получить описание выбраного теста в окне ʼНазнаить тестʼ")
    public String getTestDescriptionOnBulkModal() {
        return testInfo.nth(0).textContent();
    }

    @Step("Получить время прохождения выбраного теста в окне ʼНазнаить тестʼ")
    public String getTestExecutionTimeOnBulkModal() {
        return testInfo.nth(1).textContent();
    }

    @Step("Получить проходной бал выбраного теста в окне ʼНазнаить тестʼ")
    public String getTestPassingScoreOnBulkModal() {
        return testInfo.nth(2).textContent();
    }

    @Step("Нажать кнопку ʼНазначить тестʼ в окне ʼНазначить тестʼ")
    public CandidatesPage clickSubmitBulkAssignModalButton() {
        bulkAssignModalSubmitButton.click();
        return this;
    }

    @Step("Нажать кнопку ʼЗакрытьʼ в окне ʼНазначить тестʼ")
    public CandidatesPage clickCloseBulkAssignModalButton() {
        bulkAssignModalCloseButton.click();
        return this;
    }

    @Step("Нажать кнопку ʼИмпортʼ")
    public CandidatesPage clickImportButton() {
        importButton.click();
        return this;
    }

    @Step("Нажать кнопку ʼИмпортироватьʼ в окне ʼИмпорт кандидатовʼ")
    public CandidatesPage clickSubmitImportButton() {
        importSubmitImportModalButton.click();
        return this;
    }

    @Step("Нажать кнопку ʼЗакрытьʼ в окне ʼИмпорт кандидатовʼ")
    public CandidatesPage clickImportCancelButton() {
        importCancelImportModalButton.click();
        return this;
    }
}