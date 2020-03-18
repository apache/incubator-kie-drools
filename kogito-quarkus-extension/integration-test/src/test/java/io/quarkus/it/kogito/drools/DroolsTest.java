package io.quarkus.it.kogito.drools;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
public class DroolsTest {

    @Test
    public void testAdult() {
        String payload = "{ \"eventData\": [{ \"type\": \"temperature\", \"value\" : 40 }] }";
        RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/warnings/first")
                .then()
                .statusCode(200)
                .body("severity", is("warning"))
                .body("message", startsWith("Event"));
    }
}
