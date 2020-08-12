package org.kie.kogito.integrationtests.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class LinkTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testLink() {
        given().contentType(ContentType.JSON).when().post("/SimpleLinkTest").then().statusCode(200);
    }

}
