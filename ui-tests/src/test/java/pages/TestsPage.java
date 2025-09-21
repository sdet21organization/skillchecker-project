package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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

    // ==== мои  локаторы ====
    private final Locator createTestButton;
    private final Locator createTestConfirmButton;
    private final Locator testDescriptionInput;
    private final Locator testNameInput;

    //    private final Locator testNameError;
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
        this.createTestButton = context.page.locator("//button[contains(normalize-space(.), 'Создать тест')]");

        // Название теста (обязательно)
        this.testNameInput = context.page.locator("//input[@id='name']");
        // Описание теста (обязательно)
        this.createTestConfirmButton = context.page.locator("//button[@type='submit']");
        this.testDescriptionInput = context.page.locator("textarea[name='description']");
//        this.testNameError = context.page.getByText("Test name must be at least 3 characters");

    }


    // ===== Юлины методы =====

    @Step("Open Tests page")
    public TestsPage openTestsPage() {
        context.page.navigate("https://skillchecker.tech/dashboard/tests");
        return this;
    }

    // ===== Мои методы =====

    @Step("Verify that Tests page is opened")
    public void verifyTestsPageIsOpened() {
        boolean looksRight = context.page.url().contains("/dashboard/tests");
        Assertions.assertTrue(looksRight, "Tests page URL is incorrect");
    }


    @Step("Verify that 'Create Test' button is visible")
    public void verifyCreateTestButtonIsVisible() {
        Assertions.assertTrue(createTestButton.isVisible(),
                "Кнопка 'Создать тест' не видна на странице");
    }

    @Step("Click 'Create Test' button")
    public void clickCreateTestButton() {
        createTestButton.click();
    }

    @Step("Verify that 'Create Test' modal is visible")
    public void verifyCreateTestModalIsVisible() {

        Assertions.assertTrue(testNameInput.isVisible(), "Create Test modal is not visible");
    }

    @Step("Enter test name: {testName}")
    public void enterTestName(String testName) {
        testNameInput.fill(testName);
    }

    @Step("Enter test description: {description}")
    public void enterTestDescription(String description) {
        testDescriptionInput.fill(description);
    }

    @Step("Submit creating test")
    public void submitCreateTest() {
        createTestConfirmButton.scrollIntoViewIfNeeded();
        createTestConfirmButton.click();
    }

    @Step("Verify created test header equals '{expectedName}'")
    public void verifyCreatedTestHeader(String expectedName) {
        context.page.waitForURL("**/dashboard/tests/**");

        Locator header = context.page.getByRole(
                AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(expectedName)
        );

        Assertions.assertTrue(header.isVisible(),
                "Created test header does not match expected name: " + expectedName);
    }


    @Step("Verify error message for empty test name")
    public void verifyEmptyNameErrorVisible() {
        Locator error = context.page.getByText("Test name must be at least 3 characters");
        Assertions.assertTrue(error.isVisible(), "Expected error for empty test name is not visible");
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