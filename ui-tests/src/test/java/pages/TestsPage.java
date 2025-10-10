package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import utils.ConfigurationReader;

@DisplayName("Page: Tests")
public class TestsPage {
    TestContext context;

    private final Locator searchInput;
    private final Locator assignTestButton;
    private final Locator candidateNameSelect;
    private final Locator assignTestButtonInModal;
    private final Locator copyLinkButton;
    private final Locator testAssignedNotification;
    private final Locator createTestButton;
    private final Locator testNameInput;
    private final Locator testDescriptionInput;
    private final Locator createTestConfirmButton;
    private final Locator testHeaderByRole;
    private final Locator testHeaderFallback;

    public String uniqueName(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    public TestsPage(TestContext context) {
        this.context = context;
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
        this.createTestButton = context.page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Создать тест"));
        this.testNameInput = context.page.locator("//input[@id='name']");
        this.testDescriptionInput = context.page.locator("textarea[name='description']");
        this.createTestConfirmButton = context.page.locator("//button[@type='submit']");
        this.testHeaderByRole = context.page.getByRole(AriaRole.HEADING);
        this.testHeaderFallback = context.page.locator("h1, h2");
    }

    @Step("Open Tests page")
    public TestsPage openTestsPage() {
        context.page.navigate(ConfigurationReader.get("URL") + "dashboard/tests");
        return this;
    }

    @Step("Verify Tests page is opened")
    public void verifyTestsPageIsOpened() {
        Assertions.assertTrue(context.page.url().contains("/dashboard/tests"),
                "URL страницы Tests некорректный");
    }


    @Step("Verify 'Create Test' button is visible")
    public void verifyCreateTestButtonIsVisible() {
        createTestButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assertions.assertTrue(createTestButton.isVisible(), "Кнопка 'Создать тест' не отображается");
    }

    @Step("Click 'Create Test' button")
    public void clickCreateTestButton() {
        createTestButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        createTestButton.click();
    }

    @Step("Verify 'Create Test' modal is visible")
    public void verifyCreateTestModalIsVisible() {
        testNameInput.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        Assertions.assertTrue(testNameInput.isVisible(), "Модальное окно 'Создать тест' не отображается");
    }

    @Step("Enter test name: {testName}")
    public void enterTestName(String testName) {
        testNameInput.fill(testName);
    }

    @Step("Enter test description: {description}")
    public void enterTestDescription(String description) {
        testDescriptionInput.fill(description);
    }

    @Step("Submit create test form")
    public void submitCreateTest() {
        createTestConfirmButton.scrollIntoViewIfNeeded();
        createTestConfirmButton.click();

    }

    @Step("Wait for test to be created and header to be: {expectedName}")
    public void waitTestCreatedAndHeaderIs(String expectedName) {

        context.page.waitForURL("**/dashboard/tests/**",
                new Page.WaitForURLOptions().setTimeout(15000));
        Locator headerByRole = testHeaderByRole.filter(
                new Locator.FilterOptions().setHasText(expectedName)
        );
        Locator headerFallback = testHeaderFallback.filter(
                new Locator.FilterOptions().setHasText(expectedName)
        );

        boolean visible = headerByRole.isVisible() || headerFallback.isVisible();
        if (!visible) {
            headerByRole.waitFor(new Locator.WaitForOptions()
                    .setTimeout(5000)
                    .setState(WaitForSelectorState.VISIBLE));
        }

        Assertions.assertTrue(
                headerByRole.isVisible() || headerFallback.isVisible(),
                "Created test header does not match expected name: " + expectedName
        );
    }


    @Step("Create test with unique name and description")
    public String createTestFullyWithUniqueData() {
        final String name = uniqueName("ui-test");
        final String description = uniqueName("desc");

        if (context.page.locator("input#name, input[name='name']").count() == 0) {
            clickCreateTestButton();
            verifyCreateTestModalIsVisible();
        }

        enterTestName(name);
        enterTestDescription(description);
        submitCreateTest();
        waitTestCreatedAndHeaderIs(name);

        return name;
    }


    @Step("Verify empty name error is visible")
    public void verifyEmptyNameErrorVisible() {
        Locator error = context.page.getByText("Test name must be at least 3 characters");
        Assertions.assertTrue(error.isVisible(), "Сообщение об ошибке для пустого имени теста не отображается");
    }

    @Step("Find test {test}")
    public TestsPage findTestPassingTestWithOneOption(String test) {
        searchInput.fill(test);
        return this;
    }

    @Step("Assign test to candidate via Tests page")
    public TestsPage assignTestToCandidate() {
        assignTestButton.click();
        candidateNameSelect.click();
        context.page.getByText(ConfigurationReader.get("candidate.email")).click();
        assignTestButtonInModal.click();
        return this;
    }

    @Step("Copy test link")
    public TestsPage copyLinkTest() {
        assignTestButton.click();
        candidateNameSelect.click();
        context.page.getByText(ConfigurationReader.get("candidate.email")).click();
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

    @Step("Copy link to clipboard")
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
        Assertions.assertTrue(actualResult.contains(ConfigurationReader.get("URL") + "take-test/"));
    }

    @Step("Verify that test is successfully assigned")
    public void verifyThatTestIsSuccessfullyAssigned() {
        String actualText = testAssignedNotification.textContent();
        Assertions.assertEquals("Test assigned successfully", actualText);
    }
}






