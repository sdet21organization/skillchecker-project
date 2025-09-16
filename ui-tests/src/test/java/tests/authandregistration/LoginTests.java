package tests.authandregistration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigurationReader;

@DisplayName("UI. Login tests")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("Successful login test")
    public void successfulLoginTest(){
        context.page.navigate(ConfigurationReader.get("URL"));
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        new LoginPage(context).login(email,password);
        context.page.waitForURL(ConfigurationReader.get("URL") + "dashboard");
        String currentUrl = context.page.url();

        Assertions.assertEquals(ConfigurationReader.get("URL")+"dashboard",currentUrl);
    }

    @Test
    @DisplayName("Unsuccessful login test")
    public void unsuccessfulLoginTest(){
        context.page.navigate(ConfigurationReader.get("URL"));
        String email = "wrong@email.com";
        String password = "wrogpass";

        new LoginPage(context).login(email,password);
        String currentUrl = context.page.url();

        Assertions.assertEquals(ConfigurationReader.get("URL"),currentUrl);
    }
}
