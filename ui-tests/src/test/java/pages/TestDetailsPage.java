package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

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

}




