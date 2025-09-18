package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.Cookie;
import context.TestContext;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import pages.LoginPage;
import utils.BrowserFactory;
import utils.ConfigurationReader;
import utils.GetAuthCookie;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected TestContext context;
    Playwright playwright;
    Browser browser;

    @BeforeAll
    public void beforeAll() throws Exception {
        playwright = Playwright.create();
        browser =  BrowserFactory.get(playwright);
    }

    @BeforeEach
    public void beforeEach() {
        context = new TestContext();
        context.browserContext = browser.newContext(new Browser.NewContextOptions()
                .setPermissions(Arrays.asList("clipboard-read", "clipboard-write")));
        context.browserContext.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        context.page = context.browserContext.newPage();
        context.browserContext.addCookies(GetAuthCookie.getAuthCookie(context));
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
            context.browserContext.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace.zip")));
            context.browserContext.close();
        }
    }

}