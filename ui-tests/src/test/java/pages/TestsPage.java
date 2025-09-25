package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class TestsPage {
    TestContext context;

    // ==== Юлины локаторы ====
    private final Locator searchInput;
    private final Locator testsButton;
    private final Locator assignTestButton;
    private final Locator candidateNameSelect;
    private final Locator assignTestButtonInModal;
    private final Locator copyLinkButton;
    private final Locator testAssignedNotification;

    // ==== мои локаторы ====
    private final Locator createTestButton;
    private final Locator testNameInput;
    private final Locator testDescriptionInput;
    private final Locator createTestConfirmButton;

    public TestsPage(TestContext context) {
        this.context = context;

        // --- Юлины ---
        this.testsButton = context.page.getByText("Тесты");
        this.searchInput = context.page.locator("input[placeholder='Найти...']");
        this.assignTestButton = context.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Назначить тест"));
        this.candidateNameSelect = context.page.getByRole(AriaRole.COMBOBOX,
                new Page.GetByRoleOptions().setName("Имя"));
        this.assignTestButtonInModal = context.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Assign Test"));
        this.copyLinkButton = context.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Copy link"));
        this.testAssignedNotification = context.page.locator("div[class='text-sm font-semibold']");

        // --- мои ---
        this.createTestButton = context.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Создать тест"));
        this.testNameInput = context.page.locator("//input[@id='name']");
        this.testDescriptionInput = context.page.locator("textarea[name='description']");
        this.createTestConfirmButton = context.page.locator("//button[@type='submit']");
    }

    // ===== Юлины методы =====

    @Step("Open Tests page")
    public TestsPage openTestsPage() {
        context.page.navigate("https://skillchecker.tech/dashboard/tests");
        return this;
    }

    // ===== Мои методы =====

    @Step("Проверить, что страница 'Тесты' открыта")
    public void verifyTestsPageIsOpened() {
        Assertions.assertTrue(context.page.url().contains("/dashboard/tests"),
                "URL страницы Tests некорректный");
    }


    @Step("Проверить, что кнопка 'Создать тест' видна")
    public void verifyCreateTestButtonIsVisible() {
        createTestButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assertions.assertTrue(createTestButton.isVisible(), "Кнопка 'Создать тест' не отображается");
    }

    @Step("Кликнуть по кнопке 'Создать тест'")
    public void clickCreateTestButton() {
        createTestButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        createTestButton.click();
    }

    @Step("Проверить, что модальное окно 'Создать тест' отображается")
    public void verifyCreateTestModalIsVisible() {
        testNameInput.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assertions.assertTrue(testNameInput.isVisible(), "Модальное окно 'Создать тест' не отображается");
    }

    @Step("Ввести название теста: {testName}")
    public void enterTestName(String testName) {
        testNameInput.fill(testName);
    }

    @Step("Ввести описание теста: {description}")
    public void enterTestDescription(String description) {
        testDescriptionInput.fill(description);
    }


    @Step("Отправить форму создания теста")
    public void submitCreateTest() {
        createTestConfirmButton.scrollIntoViewIfNeeded();
        createTestConfirmButton.click();

    }

    @Step("Дождаться, что тест создан и заголовок равен '{expectedName}'")
    public void waitTestCreatedAndHeaderIs(String expectedName) {

        context.page.waitForURL("**/dashboard/tests/**", new Page.WaitForURLOptions().setTimeout(15000));


        Locator headerByRole = context.page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(expectedName)
        );
        Locator headerFallback = context.page.locator("h1:has-text('" + expectedName + "'), h2:has-text('" + expectedName + "')");

        boolean visible = headerByRole.isVisible() || headerFallback.isVisible();
        if (!visible) {
            headerByRole.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        }

        Assertions.assertTrue(
                headerByRole.isVisible() || headerFallback.isVisible(),
                "Created test header does not match expected name: " + expectedName
        );
    }


    @Step("Проверить сообщение об ошибке для пустого названия теста")
    public void verifyEmptyNameErrorVisible() {
        Locator error = context.page.getByText("Test name must be at least 3 characters");
        Assertions.assertTrue(error.isVisible(), "Сообщение об ошибке для пустого имени теста не отображается");
    }


    @Step("Открыть тест по названию: {testName}")
    public void openTestByName(String testName) {

        Locator rowByText = context.page.locator("tr:has-text(\"" + testName + "\")");
        rowByText.waitFor(new Locator.WaitForOptions()
                .setTimeout(7000)
                .setState(WaitForSelectorState.VISIBLE));

        rowByText.locator("a, button, td, div").first().scrollIntoViewIfNeeded();
        rowByText.locator("a, button, td, div").first().click();
    }


    // ===== Юлины методы =====
    @Step("Find test 'Тест Прохождение тестов с одной опцией'")
    public TestsPage findTestPassingTestWithOneOption() {
        searchInput.fill("Тест Прохождение тестов с одной опцией");
        return this;
    }

    @Step("Assign test 'Тест Прохождение тестов с одной опцией' to candidate")
    public TestsPage assignTestToCandidate() {
        assignTestButton.click();
        candidateNameSelect.click();
        context.page.getByText("John Doe (john.doe@example.").click();
        assignTestButtonInModal.click();
        return this;
    }

    @Step("Copy link to the test 'Тест Прохождение тестов с одной опцией'")
    public TestsPage copyLinkToTest() {
        assignTestButton.click();
        candidateNameSelect.click();
        context.page.getByText("John Doe (john.doe@example.").click();
        assignTestButtonInModal.click();
        copyLinkButton.click();
        return this;
    }

    @Step("Open new tab and navigate to URL {url}")
    public Page openNewTabAndNavigate(String url) {
        Page newTab = context.page.context().newPage();
        newTab.navigate(url);
        newTab.waitForLoadState();
        return newTab;
    }

    @Step("Copy link to clipboard {copyLinkButton}")
    public String copyLinkToClipboard(Locator copyButton) {
        copyButton.click();
        return context.page.evaluate("() => navigator.clipboard.readText()").toString();
    }

    @Step("Navigate to passing test page")
    public PassingTestPage navigateToPassingTestPage() {
        String testUrl = copyLinkToClipboard(copyLinkButton);
        Page passingTestPage = openNewTabAndNavigate(testUrl);
        return new PassingTestPage(passingTestPage, context);
    }

    @Step("Verify that link is copied to clipboard")
    public void verifyThatLinkIsCopiedToClipboard() {
        String actualResult = context.page.evaluate("() => navigator.clipboard.readText()").toString();
        Assertions.assertTrue(actualResult.contains("https://skillchecker.tech/take-test/"));
    }

    @Step("Verify that test is successfully assigned")
    public void verifyThatTestIsSuccessfullyAssigned() {
        String actualText = testAssignedNotification.textContent();
        Assertions.assertEquals("Test assigned successfully", actualText);
    }
}
