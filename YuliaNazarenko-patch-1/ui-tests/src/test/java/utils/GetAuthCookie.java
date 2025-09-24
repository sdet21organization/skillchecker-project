package utils;

import com.microsoft.playwright.options.Cookie;
import context.TestContext;
import pages.LoginPage;

import java.util.List;

public class GetAuthCookie {
    public static List<Cookie> getAuthCookie(TestContext context) {

        context.page.navigate(ConfigurationReader.get("URL"));
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");
        new LoginPage(context).login(email, password);
        return context.browserContext.cookies();
    }
}
