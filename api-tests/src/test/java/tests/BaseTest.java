package tests;

import helpers.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigurationReader.get("URL") + "api/";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}