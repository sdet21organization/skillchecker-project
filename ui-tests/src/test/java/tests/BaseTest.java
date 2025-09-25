package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import context.TestContext;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import utils.BrowserFactory;
import utils.GetAuthCookie;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected TestContext context;
    Playwright playwright;
    Browser browser;

    protected boolean needAuthCookie() {
        return true;
    }

    @BeforeAll
    public void beforeAll() throws Exception {
        playwright = Playwright.create();
        browser = BrowserFactory.get(playwright);
    }

    @BeforeEach
    public void beforeEach() {
        context = new TestContext();
        context.browserContext = browser.newContext(
                new Browser.NewContextOptions()
                        .setPermissions(Arrays.asList("clipboard-read", "clipboard-write"))
        );
        context.browserContext.tracing().start(
                new Tracing.StartOptions().setScreenshots(true).setSnapshots(true)
        );

        context.page = context.browserContext.newPage();

        if (needAuthCookie()) {
            context.browserContext.addCookies(GetAuthCookie.getAuthCookie(context));
        }
    }

    @AfterAll
    public void afterAll() {
        playwright.close();
    }

    @AfterEach
    public void afterEach() {
        byte[] screenshot = context.page.screenshot();
        Allure.addAttachment("Скриншот", new ByteArrayInputStream(screenshot));

        if (context.browserContext != null) {
            context.browserContext.tracing().stop(
                    new Tracing.StopOptions().setPath(Paths.get("trace.zip"))
            );
            context.browserContext.close();
        }
    }
}