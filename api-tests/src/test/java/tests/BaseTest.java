package tests;

import helpers.ConfigurationReader;
import helpers.GetAuthCookie;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    protected static String cookie;

    @BeforeAll
    public static void setup() {
        String raw = ConfigurationReader.get("URL").trim();
        String url = raw.replaceAll("/+$", "");
        boolean hasApi = url.matches(".*/api$");

        if (hasApi) {
            RestAssured.baseURI = url;
            RestAssured.basePath = "";
        } else {
            RestAssured.baseURI = url;
            RestAssured.basePath = "/api";
        }

        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        cookie = "connect.sid=" + GetAuthCookie.getAuthCookie();
    }
}