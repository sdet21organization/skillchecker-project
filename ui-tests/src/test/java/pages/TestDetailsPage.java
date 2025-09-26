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

@DisplayName("Страница деталей теста- вопросы")
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
        this.questionsHeader = context.page
                .locator("div.text-2xl.font-semibold.leading-none.tracking-tight:has-text('Вопросы')")
                .first();
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
    }



    @Step("Проверить заголовок теста: {expected}")
    public void verifyTestTitle(String expected) {
        Locator title = context.page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(expected));
        if (!title.isVisible()) {
            String esc = expected.replace("\"", "\\\"");
            title = context.page.locator("h1:has-text(\"" + esc + "\"), h2:has-text(\"" + esc + "\")");
        }
        title.waitFor(new Locator.WaitForOptions().setTimeout(7000).setState(WaitForSelectorState.VISIBLE));
        Assertions.assertTrue(title.isVisible(), "Ожидали заголовок теста: " + expected);
    }

    @Step("Открыть секцию 'Вопросы'")
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
        Assertions.assertTrue(ok, "Не нашли ни таблицу вопросов, ни плейсхолдер пустого списка.");
    }

    @Step("Открыть модалку добавления вопроса")
    public void openAddQuestionModal() {
        addQuestionBtn.scrollIntoViewIfNeeded();
        addQuestionBtn.click();
        assertThat(modal).isVisible();
    }

    @Step("Сохранить вопрос и проверить, что он появился в списке")
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

        Assertions.assertTrue(tableRows.count() > 0, "После сохранения вопрос не появился в списке.");
    }

    @Step("Заполнить вопрос: текст='{text}', опции: '{opt1}', '{opt2}', баллы={pts}")
    public void fillQuestionMinimal(String text, String opt1, String opt2, int pts) {
        questionText.fill(text);
        points.fill(String.valueOf(pts));
        option1.fill(opt1);
        option2.fill(opt2);
    }

    @Step("Кликнуть по кнопке 'Генерировать вопросы'")
    public void clickGenerateQuestions() {
        generateQuestionsButton.click();
    }

    @Step("Проверить, что модалка генерации вопросов видима")
    public void verifyGenerateQuestionsModalIsVisible() {
        assertThat(generateQuestionsModal).isVisible();
    }

    @Step("Выбрать в модалке генерации количество вопросов: {count}")
    public void selectGenerateCount(int count) {
        countCombobox.click();
        countOption(count).click();
    }

    @Step("Выбрать в модалке генерации тип вопросов: {typeText}")
    public void selectGenerateType(String typeText) {
        typeCombobox.click();
        typeOption(typeText).click();
    }

    @Step("Подтвердить генерацию вопросов")
    public void confirmGenerateQuestions() {
        generateButton.click();
    }


    @Step("Дождаться закрытия модалки генерации вопросов")
    public void waitGenerateModalClosed() {
       assertThat(dialog)
                .isHidden(new LocatorAssertions.IsHiddenOptions().setTimeout(10000)); // 10 сек
    }


}




