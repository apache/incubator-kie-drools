package org.drools.quarkus.test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
public class TestableIT {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @Test
    public void testCepEvaluation() {
        given().when().get("/test/testCepEvaluation").then().statusCode(200);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testFireUntiHalt() {
        given().when().get("/test/testFireUntiHalt").then().statusCode(200);
    }

    @Test
    public void testAllPkgsKBase() {
        given().when().get("/test/testAllPkgsKBase").then().statusCode(200);
    }

    @Test
    public void testTms() {
        given().when().get("/test/testTms").then().statusCode(200);
    }
}
