package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PricingTest {

    @Test
    public void testBasePrice() {
        given()
          .body("{ \"Age\": 47, \"Previous incidents?\": false }")
          .contentType(ContentType.JSON)
          .when()
            .post("/pricing")
          .then()
            .statusCode(200)
            .body("'Base price'", is(500));
    }
}
