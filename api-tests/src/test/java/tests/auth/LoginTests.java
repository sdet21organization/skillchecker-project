package tests.auth;

import helpers.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import wrappers.Auth;

@DisplayName("API. Login tests")
public class LoginTests extends BaseTest {

    @Test
    @DisplayName("Check successful login")
    void successfulLogin() {
        String email = ConfigurationReader.get("email");
        String password = ConfigurationReader.get("password");

        Response response = Auth.loginUser(email, password);
        Auth.verifySuccessfulLoginResponse(response);
    }

    @Test
    @DisplayName("Check unsuccessful login")
    void unsuccessfulLogin() {
        String invalidEmail = "wrong@email.com";
        String invalidPassword = "wrongpass";

        Response response = Auth.loginUser(invalidEmail, invalidPassword);
        Auth.verifyUnsuccessfulLoginResponse(response);
    }

}

