package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import utils.ConfigurationReader;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PassingTestPage {
    TestContext context;
    Page page;

    private final Locator startTestButton;
    private final Locator title;
    private final Locator optionOne;
    private final Locator optionTwo;
    private final Locator nextButton;
    private final Locator sendTestButton;
    private final Locator successNotification;
    private final Locator timeExpiredMessage;
    private final Locator timeTestPass;
    private final Locator textInputArea;

    private String timeToPassTest;

    public PassingTestPage(Page page, TestContext context) {
        this.page = page;
        this.context = context;
        this.startTestButton = page.locator("button[class]");
        this.title = page.locator("//div[text()]");
        this.optionOne = page.locator("//label[text()='Опция 1']");
        this.optionTwo = page.locator("//label[text()='Опция 2']");
        this.nextButton = page.locator("//button[text()='Далее']");
        this.sendTestButton = page.locator("//button[text()='Отправить тест']");
        this.successNotification = page.locator("//span[text()='Ваши ответы были сохранены']");
        this.timeExpiredMessage = page.locator("//div[text()='Время на прохождение теста истекло']");
        this.timeTestPass = page.locator("//div[span[1][contains(text(),'Ограничение по времени:')]]/span[2]");
        this.textInputArea = page.locator("[placeholder='Введите ваш ответ...']");
    }

    @Step("Verify that Passing Test page opens correctly")
    public void verifyThatPassingTestPageOpensCorrectly() {
        context.page.waitForTimeout(1500);
        String actualTitle = title.textContent();
        String actualStartButton = startTestButton.textContent();
        Assertions.assertEquals(ConfigurationReader.get("test.for.successful.passing"), actualTitle, "Title does not match");
        Assertions.assertEquals("Начать тест", actualStartButton, "Start Test button text does not match");
    }

    @Step("Get time to pass the test")
    public PassingTestPage getTimeToPassTheTest() {
        timeToPassTest = timeTestPass.textContent().trim().split("\\s+")[0];
        return this;
    }

    @Step("Start the test")
    public PassingTestPage startTest() {
        startTestButton.click();
        return this;
    }

    @Step("Pass the test with one option")
    public PassingTestPage testPassing() {
        optionOne.click();
        nextButton.click();
        optionTwo.click();
        sendTestButton.click();
        return this;
    }

    @Step("Pass the test with two options")
    public PassingTestPage passTestWithTwoOptions() {
        optionOne.click();
        optionTwo.click();
        sendTestButton.click();
        return this;
    }

    @Step("Pass the test with text input")
    public PassingTestPage passTestWithTextInput() {
        textInputArea.fill("This is my answer.");
        sendTestButton.click();
        return this;
    }

    @Step("Verify notification about the answers are saved and validate test name")
    public void verifyThatTestIsPassed() {
        successNotification.waitFor();
        Locator passedTestTitle = page.locator(format("//div[text()='%s']", ConfigurationReader.get("test.for.successful.passing")));
        passedTestTitle.waitFor();
    }

    @Step("Verify notification about the answers are saved and validate test name")
    public void verifyThatTestWithTwoOptionsIsPassed() {
        successNotification.waitFor();
        Locator passedTestTitle = page.locator(format("//div[text()='%s']", ConfigurationReader.get("test.with.two.options")));
        passedTestTitle.waitFor();
    }

    @Step("Verify notification about the answers are saved and validate test name")
    public void verifyThatTestWithTextInputOptionIsPassed() {
        successNotification.waitFor();
        Locator passedTestTitle = page.locator(format("//div[text()='%s']", ConfigurationReader.get("test.with.text.input")));
        passedTestTitle.waitFor();
    }

    @Step("Verify that 'Next' button is disabled until an option is selected")
    public void verifyThatNextButtonIsDisabled() {
        assertFalse(nextButton.isEnabled());
    }

    @Step("Verify that test closes when time is expired")
    public void verifyThatTestClosesWhenTimeExpired() {
        context.page.waitForTimeout(Integer.parseInt(timeToPassTest) * 60 * 1000);
        timeExpiredMessage.waitFor();
    }
}
