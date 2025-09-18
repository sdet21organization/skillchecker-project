package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class TestsPage {
    TestContext context;

    private final Locator searchInput;
    private final Locator testsButton;
    private final Locator assignTestButton;
    private final Locator candidateNameSelect;
    private final Locator assignTestButtonInModal;
    private final Locator copyLinkButton;
    private final Locator testAssignedNotification;

    public TestsPage(TestContext context) {
        this.context = context;
        this.testsButton = context.page.getByText("Тесты");
        this.searchInput = context.page.locator("input[placeholder='Найти...']");
        this.assignTestButton = context.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Назначить тест"));
        this.candidateNameSelect = context.page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Имя"));
        this.assignTestButtonInModal = context.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Assign Test"));
        this.copyLinkButton = context.page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Copy link"));
        this.testAssignedNotification = context.page.locator("div[class='text-sm font-semibold']");
    }

    @Step("Open Tests page")
    public TestsPage openTestsPage() {
        context.page.navigate("https://skillchecker.tech/dashboard/tests");
        return this;
    }

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
