package pages;

import com.microsoft.playwright.Locator;
import context.TestContext;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

import java.util.List;

public class CandidatePage {
    TestContext context;

    private final Locator testNames;

    public CandidatePage(TestContext context) {
        this.context = context;
        this.testNames = context.page.locator("div.mb-2 a");
    }

    @Step("Открыть страницу кандидата с id {id}")
    public CandidatePage open(int id) {
        context.page.navigate(ConfigurationReader.get("URL") + "dashboard/candidates/" + id);
        context.page.waitForTimeout(1500);
        return this;
    }

    @Step("Получить список всех назначенных тестов")
    public List<String> getListOfTests() {
        return testNames.allTextContents();
    }
}
