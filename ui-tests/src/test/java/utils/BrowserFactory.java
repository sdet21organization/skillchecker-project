package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;

import static com.microsoft.playwright.BrowserType.LaunchOptions;

public class BrowserFactory {

    public static Browser get(Playwright playwright) throws Exception {
        String browserConfig = ConfigurationReader.get("browser");

        playwright.selectors().setTestIdAttribute("data-test");

        Browser browser;

        LaunchOptions launchOptions = new LaunchOptions();
        if(!Boolean.parseBoolean(ConfigurationReader.get("headless"))){
            launchOptions.setHeadless(false).setSlowMo(500);
        }

        switch (browserConfig){
            case ("chrome"):
                browser = playwright.chromium().launch(launchOptions);
                return browser;
            case ("firefox"):
                browser = playwright.firefox().launch(launchOptions);
                return browser;
            case ("safari"):
                browser = playwright.webkit().launch(launchOptions);
                return browser;
            default:
                throw new Exception("Не выбрана конфигурация браузера в файле configuration.properties");
        }
    }
}