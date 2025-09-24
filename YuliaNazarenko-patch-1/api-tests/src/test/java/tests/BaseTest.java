package tests;

import helpers.ConfigurationReader;
import helpers.GetAuthCookie;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected static String cookie;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigurationReader.get("URL") + "api/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        cookie = "connect.sid=" + GetAuthCookie.getAuthCookie();
    }
}