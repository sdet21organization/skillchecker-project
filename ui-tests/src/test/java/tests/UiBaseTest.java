package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import context.TestContext;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import utils.BrowserFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UiBaseTest {
    protected TestContext context;
    private Playwright playwright;
    private Browser browser;

    @BeforeAll
    void beforeAll() throws Exception {
        playwright = Playwright.create();
        browser = BrowserFactory.get(playwright);
    }

    @BeforeEach
    void beforeEach() {
        context = new TestContext();
        context.browserContext = browser.newContext(
                new Browser.NewContextOptions().setPermissions(Arrays.asList("clipboard-read", "clipboard-write"))
        );

        context.browserContext.tracing().start(
                new Tracing.StartOptions().setScreenshots(true).setSnapshots(true)
        );

        context.page = context.browserContext.newPage();
        // Общие таймауты: чуть меньше флаки
        context.page.setDefaultTimeout(10_000);
        context.page.setDefaultNavigationTimeout(20_000);
    }

    @AfterEach
    void afterEach(TestInfo info) {
        try {
            byte[] screenshot = context.page.screenshot();
            Allure.addAttachment("Скриншот", new ByteArrayInputStream(screenshot));
        } catch (Throwable ignore) {}

        if (context.browserContext != null) {
            try {
                Path trace = Paths.get("target", "traces",
                        info.getDisplayName().replaceAll("[^a-zA-Z0-9_.-]", "_") + ".zip");
                trace.toFile().getParentFile().mkdirs();
                context.browserContext.tracing().stop(new Tracing.StopOptions().setPath(trace));
            } catch (Throwable ignore) {}
            try {
                context.browserContext.close();
            } catch (Throwable ignore) {}
        }
    }

    @AfterAll
    void afterAll() {
        try { browser.close(); } catch (Throwable ignore) {}
        try { playwright.close(); } catch (Throwable ignore) {}
    }
}