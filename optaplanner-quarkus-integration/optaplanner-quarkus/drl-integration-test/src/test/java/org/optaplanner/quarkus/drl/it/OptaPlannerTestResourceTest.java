package org.optaplanner.quarkus.drl.it;

import java.io.StringReader;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Test various OptaPlanner operations running in Quarkus
 */

@QuarkusTest
class OptaPlannerTestResourceTest {

    @Test
    @Timeout(600)
    void solveWithSolverFactory() throws Exception {
        Properties result = new Properties();
        result.load(new StringReader(RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .post("/optaplanner/test/solver-factory")
                .then()
                .extract().body().asString()));
        Assertions.assertEquals("0", result.get("score"));
        Assertions.assertNotNull(result.get("entity.0.fullValue"));
        Assertions.assertNotNull(result.get("entity.1.fullValue"));
        Assertions.assertNotEquals(result.get("entity.0.fullValue"), result.get("entity.1.fullValue"),
                "Both entities have the same value. Maybe property reactive is set to ALWAYS?");
    }

}
