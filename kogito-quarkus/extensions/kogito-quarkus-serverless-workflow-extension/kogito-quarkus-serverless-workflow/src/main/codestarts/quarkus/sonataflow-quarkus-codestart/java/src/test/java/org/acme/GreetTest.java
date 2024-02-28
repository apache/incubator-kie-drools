package org.acme;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class GreetTest {

    @Test
    public void testEnglish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"Yoda\", \"language\":\"English\"}}")
                .when()
                .post("/greet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Hello"));
    }

    @Test
    public void testSpanish() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"Yoda\", \"language\":\"Spanish\"}}")
                .when()
                .post("/greet")
                .then()
                .statusCode(201)
                .body("workflowdata.greeting", containsString("Saludos"));
    }
}