package pages;

import com.microsoft.playwright.Locator;
import context.TestContext;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

public class CandidatesPage {

    TestContext context;

    public Locator addCandidateButton;

    public CandidatesPage(TestContext context){
        this.context = context;
        this.addCandidateButton = context.page.getByText("Добавить кандидата");

    }

    @Step("open page")
    public CandidatesPage open(){
        context.page.navigate("https://skillchecker.tech/dashboard/candidates");
        return this;

    }
}
