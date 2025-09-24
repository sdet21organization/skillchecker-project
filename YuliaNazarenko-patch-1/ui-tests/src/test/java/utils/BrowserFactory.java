package utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import java.util.Arrays;

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
            // Для CI (GitHub Actions / Docker) добавляем безопасные флаги
            launchOptions.setArgs(Arrays.asList(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--disable-gpu"
            ));
        }

        return switch (browserConfig) {
            case "chrome"  -> playwright.chromium().launch(launchOptions);
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "safari"  -> playwright.webkit().launch(launchOptions);
            default -> throw new Exception(
                    "Не выбрана корректная конфигурация браузера в configuration.properties (chrome|firefox|safari)"
            );
        };
    }
}
