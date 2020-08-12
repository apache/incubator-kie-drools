package org.kie.kogito.integrationtests.springboot;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = KogitoSpringbootApplication.class)
public class LinkTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testLink() {
        given().contentType(ContentType.JSON).when().post("/SimpleLinkTest").then().statusCode(200);
    }

}
