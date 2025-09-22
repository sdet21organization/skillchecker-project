package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

import java.util.Arrays;

import static com.microsoft.playwright.BrowserType.LaunchOptions;

public class BrowserFactory {
    public static Browser get(Playwright playwright) throws Exception {
        // Используем data-testid, как договорились в проекте
        playwright.selectors().setTestIdAttribute("data-testid");

        String browserConfig = ConfigurationReader.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigurationReader.get("headless"));

        LaunchOptions launchOptions = new LaunchOptions();
        if (!headless) {
            launchOptions.setHeadless(false).setSlowMo(400);
        } else {
            launchOptions.setHeadless(true);
            // Для CI иногда полезно:
            // launchOptions.setArgs(Arrays.asList("--disable-gpu","--no-sandbox","--disable-dev-shm-usage"));
        }

        return switch (browserConfig) {
            case "chrome"  -> playwright.chromium().launch(launchOptions);
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "safari"  -> playwright.webkit().launch(launchOptions);
            default -> throw new Exception("Не выбрана корректная конфигурация браузера в configuration.properties (chrome|firefox|safari)");
        };
    }
}