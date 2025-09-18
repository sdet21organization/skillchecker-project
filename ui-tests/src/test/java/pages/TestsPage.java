package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class TestsPage {
    TestContext context;

    public Locator searchInput;
    public Locator testsButton;
    public Locator assignTestButton;
    public Locator candidateNameSelect;
    public Locator assignTestButtonInModal;
    public Locator copyLinkButton;
    public Locator testAssignedNotification;

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

    @Step("Verify that test is successfully assigned")
    public void verifyThatTestIsSuccessfullyAssigned() {
        String actualText = testAssignedNotification.textContent();
        Assertions.assertEquals("Test assigned successfully", actualText);
    }
}
