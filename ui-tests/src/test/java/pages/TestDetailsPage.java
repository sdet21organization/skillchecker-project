package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Page: Test Details")
public class TestDetailsPage {
    private final TestContext context;
    private final Locator modal;
    private final Locator questionText;
    private final Locator points;
    private final Locator option1;
    private final Locator option2;
    public final Locator saveBtn;
    private final Locator questionsHeader;
    private final Locator addQuestionBtn;
    private final Locator actionsBarAnyAddBtn;
    private final Locator questionsTable;
    private final Locator emptyPlaceholder;
    private final Locator tableRows;
    private final Locator generateQuestionsButton;
    private final Locator generateQuestionsModal;
    private final Locator dialog;
    private final Locator countCombobox;
    private final Locator typeCombobox;
    private final Locator generateButton;
    private final Locator editTestButton;
    private final Locator editContainer;
    private final Locator editNameInput;
    private final Locator editSaveButton;
    private final Locator timeLimitInput;
    private final Locator successToast;
    private final Locator passingScoreInput;
    private final Locator activeToggle;
    private final Locator cancelButton;
    private final Locator nameErrorMessage;
    private final Locator timeLimitErrorMessage;

    private Locator countOption(int count) {
        return context.page.locator("[role='option']:has-text(\"" + count + " questions\")");
    }

    private Locator typeOption(String typeText) {
        return context.page.locator("[role='option']:has-text(\"" + typeText + "\")");
    }

    public TestDetailsPage(TestContext context) {
        this.context = context;

        this.modal = context.page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName("Add New Question"));
        this.questionText = modal.getByPlaceholder("Enter the question text");
        this.points = modal.getByRole(AriaRole.SPINBUTTON, new Locator.GetByRoleOptions().setName("Points"));
        this.option1 = modal.getByPlaceholder("Option 1");
        this.option2 = modal.getByPlaceholder("Option 2");
        this.saveBtn = modal.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Save"));
        this.questionsHeader = context.page.locator("text=Вопросы").first();
        this.addQuestionBtn = context.page
                .locator("button:has-text('Add Question'), button:has-text('Добавить вопрос')")
                .first();
        this.actionsBarAnyAddBtn = this.addQuestionBtn;
        this.questionsTable = context.page
                .locator("[data-testid='questions-table'], [role='table'], table");
        this.emptyPlaceholder = context.page.getByText("No questions in this test yet.");
        this.tableRows = context.page
                .locator("[data-testid='questions-table'] tr, table tr");
        this.generateQuestionsButton = context.page.locator("button:has-text(\"Генерировать вопросы\")");
        this.generateQuestionsModal = context.page.locator("text=Генерация вопросов");
        this.dialog = context.page.locator("div[role='dialog']").filter(
                new Locator.FilterOptions().setHasText("Generate Questions"));
        this.countCombobox = context.page.locator("label:has-text(\"Count\")").locator("..").locator("[role='combobox']");
        this.typeCombobox = context.page.locator("label:has-text(\"Type\")").locator("..").locator("[role='combobox']");
        this.generateButton = context.page.locator("button:has-text(\"Generate\")");
        this.editTestButton = context.page.locator(
                "button:has-text('Редакт'), button:has-text('Edit')"
        ).first();
        this.editContainer = context.page.locator("form:has(input[name='name'])").first();
        this.editNameInput = context.page.locator("input#name, input[name='name']").first();
        this.editSaveButton = editContainer.locator(
                "button:has-text('Сохранить'), button:has-text('Save')"
        ).first();
        this.timeLimitInput = context.page.locator(
                "[data-testid='time-limit-input'], input[name='timeLimit']"
        ).first();
        this.successToast = context.page.locator(
                "[role='status']:has-text('Успех'), [role='alert']:has-text('Успех')"
        ).first();
        this.passingScoreInput = context.page.locator("[data-testid='passing-score-input'], input[name='passingScore']").first();
        this.activeToggle = context.page.locator("button[role='switch']").first();
        this.cancelButton = context.page.locator("button[data-testid='cancel-button']");
        this.nameErrorMessage = context.page
                .locator(".text-destructive:has-text('Test name must be at least 3 characters'), " +
                        "[id$='form-item-message']:has-text('Test name must be at least 3 characters')")
                .first();
        this.timeLimitErrorMessage = context.page
                .locator("[id$='form-item-message']:has-text('greater than or equal to 0')")
                .first();

    }


    @Step("Verify test title is: '{expected}'")
    public void verifyTestTitle(String expected) {
        Locator title = context.page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(expected));
        if (!title.isVisible()) {
            String esc = expected.replace("\"", "\\\"");
            title = context.page.locator("h1:has-text(\"" + esc + "\"), h2:has-text(\"" + esc + "\")");
        }
        title.waitFor(new Locator.WaitForOptions().setTimeout(7000).setState(WaitForSelectorState.VISIBLE));
        assertTrue(title.isVisible(), "Ожидали заголовок теста: " + expected);
    }

    @Step("Open Questions section")
    public void openQuestionsSection() {
        if (questionsHeader.isVisible()) {
            questionsHeader.scrollIntoViewIfNeeded();
        }
        actionsBarAnyAddBtn.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            if (questionsTable.isVisible() || emptyPlaceholder.isVisible()) break;
            context.page.waitForTimeout(100);
        }
        boolean ok = questionsTable.isVisible() || emptyPlaceholder.isVisible();
        assertTrue(ok, "Не нашли ни таблицу вопросов, ни плейсхолдер пустого списка.");
    }

    @Step("Open 'Add Question' modal")
    public void openAddQuestionModal() {
        addQuestionBtn.scrollIntoViewIfNeeded();
        addQuestionBtn.click();
        assertThat(modal).isVisible();
    }

    @Step("Save question and verify it appeared in the list")
    public void saveQuestionAndVerifyAppeared() {
        saveBtn.click();
        modal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED));
        context.page.waitForLoadState(LoadState.NETWORKIDLE);

        if (emptyPlaceholder.isVisible()) {
            emptyPlaceholder.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(5000));
        }
        tableRows.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));

        assertTrue(tableRows.count() > 0, "После сохранения вопрос не появился в списке.");
    }

    @Step("Fill question with minimal data: text='{text}', opt1='{opt1}', opt2='{opt2}', pts={pts}")
    public void fillQuestionMinimal(String text, String opt1, String opt2, int pts) {
        questionText.fill(text);
        points.fill(String.valueOf(pts));
        option1.fill(opt1);
        option2.fill(opt2);
    }

    @Step("Click 'Generate Questions' button")
    public void clickGenerateQuestions() {
        generateQuestionsButton.click();
    }

    @Step("Verify 'Generate Questions' modal is visible")
    public void verifyGenerateQuestionsModalIsVisible() {
        assertThat(generateQuestionsModal).isVisible();
    }

    @Step("Fill in the modal the number of questions to generate: {count}")
    public void selectGenerateCount(int count) {
        countCombobox.click();
        countOption(count).click();
    }

    @Step("Fill in the modal the type of questions to generate: {typeText}")
    public void selectGenerateType(String typeText) {
        typeCombobox.click();
        typeOption(typeText).click();
    }

    @Step("Confirm generation of questions")
    public void confirmGenerateQuestions() {
        generateButton.click();
    }


    @Step("Wait until 'Generate Questions' modal is closed")
    public void waitGenerateModalClosed() {
        assertThat(dialog)
                .isHidden(new LocatorAssertions.IsHiddenOptions().setTimeout(10000)); // 10 сек
    }

    @Step("Verify 'Edit' button is present on test details page")
    public void verifyEditButtonPresent() {
        assertThat(editTestButton).isVisible();
    }

    @Step("Click 'Edit' button to open edit mode")
    public void openEditMode() {
        editTestButton.click();
        assertThat(editNameInput).isVisible();
    }

    @Step("Verify edit form (modal) is visible")
    public void verifyEditFormVisible() {
        assertThat(editContainer).isVisible();
        assertThat(editNameInput).isVisible();
    }

    @Step("Set new test name: {newName}")
    public void setEditName(String newName) {
        editNameInput.fill("");
        editNameInput.type(newName);
    }

    @Step("Save changes in edit modal")
    public void saveEdit() {
        editSaveButton.scrollIntoViewIfNeeded();
        editSaveButton.click();
    }

    @Step("Wait for edit modal to close")
    public void waitEditClosed() {
        assertThat(editContainer)
                .isHidden(new LocatorAssertions.IsHiddenOptions().setTimeout(10000));
    }


    @Step("Update name to '{newName}' and verify header")
    public void updateNameAndVerify(String newName) {
        openEditMode();
        setEditName(newName);
        saveEdit();
        waitEditClosed();
        verifyTestTitle(newName);
    }

    @Step("Verify that validation message mentions minLength={min}")
    public void verifyValidationErrorShown(int min) {
        questionText.focus();
        context.page.keyboard().press("Tab");

        String actual = (String) questionText.evaluate("el => el.validationMessage");
        assertTrue(
                actual.contains(String.valueOf(min)),
                "Expected validation message to mention minLength=" + min + ", but was: " + actual
        );
    }

    @Step("Check that an error message is shown for empty question name")
    public void verifyEmptyNameError() {
        questionText.focus();
        context.page.keyboard().press("Tab");

        String actual = (String) questionText.evaluate("el => el.validationMessage");

        assertTrue(
                actual != null && !actual.isBlank(),
                "Ожидали, что появится сообщение об обязательности поля, но оно отсутствует"
        );
    }

    @Step("Add an invalid question '{question}' with answers '{answer1}', '{answer2}' and correct index {correctIndex}")
    public void addInvalidQuestion(String question, String answer1, String answer2, int correctIndex) {
        openAddQuestionModal();
        fillQuestionMinimal(question, answer1, answer2, correctIndex);
    }

    @Step("Set new test description: {newDescription}")
    public void setEditDescription(String newDescription) {
        editContainer.locator("textarea#description, textarea[name='description']").fill("");
        editContainer.locator("textarea#description, textarea[name='description']").type(newDescription);
    }

    @Step("Update description to '{newDescription}' and verify on page")
    public void updateDescriptionAndVerify(String newDescription) {
        openEditMode();
        setEditDescription(newDescription);
        saveEdit();
        waitEditClosed();

        Locator descBlock = context.page.locator("div, p").filter(
                new Locator.FilterOptions().setHasText(newDescription)
        ).first();
        descBlock.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        assertTrue(descBlock.isVisible(), "Ожидали увидеть новое описание: " + newDescription);
    }

    @Step("Update time limit to {minutes} minutes and verify")
    public void updateTimeLimitAndVerify(int minutes) {
        openEditMode();

        assertThat(timeLimitInput).isVisible();
        assertThat(timeLimitInput).isEnabled();

        int guard = 0;
        int current = Integer.parseInt(timeLimitInput.inputValue());
        timeLimitInput.focus();
        while (current != minutes && guard++ < 300) {
            timeLimitInput.press(current < minutes ? "ArrowUp" : "ArrowDown");
            current = Integer.parseInt(timeLimitInput.inputValue());
        }
        timeLimitInput.press("Tab");

        saveEdit();
        waitEditClosed();

        successToast.waitFor(new Locator.WaitForOptions()
                .setTimeout(15000)
                .setState(WaitForSelectorState.ATTACHED));

        assertThat(successToast)
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
        assertThat(successToast).containsText("Успех");
    }

    @Step("Update passing score to {score} and verify")
    public void updatePassingScoreAndVerify(int score) {
        openEditMode();

        assertThat(passingScoreInput).isVisible();
        assertThat(passingScoreInput).isEnabled();

        int guard = 0;
        int current = Integer.parseInt(passingScoreInput.inputValue());
        passingScoreInput.focus();
        while (current != score && guard++ < 300) {
            passingScoreInput.press(current < score ? "ArrowUp" : "ArrowDown");
            current = Integer.parseInt(passingScoreInput.inputValue());
        }
        passingScoreInput.press("Tab");

        saveEdit();
        waitEditClosed();

        successToast.waitFor(new Locator.WaitForOptions()
                .setTimeout(15000)
                .setState(WaitForSelectorState.ATTACHED));

        assertThat(successToast)
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
        assertThat(successToast).containsText("Успех");
    }

    @Step("Toggle active state and verify success message")
    public void toggleActiveStateAndVerify() {
        openEditMode();

        assertThat(activeToggle).isVisible();
        assertThat(activeToggle).isEnabled();
        activeToggle.click();

        saveEdit();
        waitEditClosed();

        successToast.waitFor(new Locator.WaitForOptions()
                .setTimeout(15000)
                .setState(WaitForSelectorState.ATTACHED));
        assertThat(successToast)
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
        assertThat(successToast).containsText("Успех");
    }

    @Step("Edit active state, click Cancel and verify no changes applied")
    public void cancelActiveStateChangeAndVerify() {
        openEditMode();
        String before = activeToggle.getAttribute("data-state");

        activeToggle.click();
        cancelButton.click();

        waitEditClosed();

        openEditMode();
        String after = activeToggle.getAttribute("data-state");
        Assertions.assertEquals(before, after, "Состояние тумблера изменилось после отмены");
    }


    @Step("Edit name to empty and verify validation error")
    public void updateNameToEmptyAndVerifyError() {
        openEditMode();

        assertThat(editNameInput).isVisible();
        assertThat(editNameInput).isEnabled();

        editNameInput.fill("");
        saveEdit();

        assertThat(nameErrorMessage)
                .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(5000));
        assertThat(editContainer).isVisible();
    }

    @Step("Edit time limit to negative value and verify validation error appears")
    public void editTimeLimitToNegativeAndVerifyValidationError(int minutes) {
        openEditMode();

        assertThat(timeLimitInput).isVisible();
        assertThat(timeLimitInput).isEnabled();

        timeLimitInput.fill(String.valueOf(minutes));
        timeLimitInput.press("Tab");
        saveEdit();

        timeLimitErrorMessage.waitFor(new Locator.WaitForOptions()
                .setTimeout(5000)
                .setState(WaitForSelectorState.VISIBLE));
        assertThat(timeLimitErrorMessage).isVisible();
    }

}




