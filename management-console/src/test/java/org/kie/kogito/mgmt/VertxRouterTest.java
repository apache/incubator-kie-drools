package org.kie.kogito.mgmt;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class VertxRouterTest {

    @Test
    public void testHandlePath() {
        given().when().get("/ProcessInstances")
                .then()
                .statusCode(200);

        given().when().get("/Process/a1e139d5-4e77-48c9-84ae-34578e904e5a")
                .then()
                .statusCode(200);

        given().when().get("/DomainExplorer")
                .then()
                .statusCode(200);

        given().when().get("/DomainExplorer/travels")
                .then()
                .statusCode(200);

        given().when().get("/Another")
                .then()
                .statusCode(404);
    }
}
