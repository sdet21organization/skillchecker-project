package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;

public class SidebarPage {
    private final TestContext context;

    public final Locator nav;

    public final Locator dashboardItem;
    public final Locator testsItem;
    public final Locator candidatesItem;
    public final Locator interviewsItem;
    public final Locator settingsItem;

    public SidebarPage(TestContext context) {
        this.context = context;

        this.nav             = context.page.getByTestId("navigation-menu");
        this.dashboardItem   = context.page.getByTestId("nav-item-dashboard");
        this.testsItem       = context.page.getByTestId("nav-item-tests");
        this.candidatesItem  = context.page.getByTestId("nav-item-candidates");
        this.interviewsItem  = context.page.getByTestId("nav-item-interviews");
        this.settingsItem    = context.page.getByTestId("nav-item-settings");
    }

    @Step("Дождаться готовности сайдбара")
    public SidebarPage waitUntilReady() {
        nav.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    @Step("Клик по пункту меню и ожидание URL: {expectedPath}")
    public void clickAndAssert(Locator itemDiv, String expectedPath) {
        itemDiv.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        itemDiv.click();
        context.page.waitForURL("**" + expectedPath);
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public boolean isActive(Locator itemDiv) {
        var anchor = itemDiv.locator("xpath=ancestor::a[1]");
        if (anchor.count() > 0 && "page".equals(anchor.getAttribute("aria-current"))) return true;

        String cls = itemDiv.getAttribute("class");
        if (cls == null) return false;
        String c = " " + cls + " ";
        return c.contains(" text-primary-600 ")
                || c.contains(" bg-primary-50 ")
                || c.contains(" font-medium ");
    }
}
