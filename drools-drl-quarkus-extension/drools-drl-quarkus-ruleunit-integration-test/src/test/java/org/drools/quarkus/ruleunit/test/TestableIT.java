package org.drools.quarkus.ruleunit.test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
public class TestableIT {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @Test
    public void testRuleUnit() {
        given().when().get("/test/testRuleUnit").then().statusCode(200);
    }
}
