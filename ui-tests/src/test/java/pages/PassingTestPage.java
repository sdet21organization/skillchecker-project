package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class PassingTestPage {
    TestContext context;
    Page page;

    private final Locator startTestButton;
    private final Locator title;

    public PassingTestPage(Page page, TestContext context) {
        this.page = page;
        this.context = context;
        this.startTestButton = page.locator("button[class]");
        this.title = page.locator("//div[text()]");
    }

    @Step("Verify that Passing Test page opens correctly")
    public void verifyThatPassingTestPageOpensCorrectly() {
        String actualTitle = title.textContent();
        String actualStartButton = startTestButton.textContent();
        Assertions.assertEquals("Тест Прохождение тестов с одной опцией", actualTitle, "Title does not match");
        Assertions.assertEquals("Начать тест", actualStartButton, "Start Test button text does not match");
    }
}
