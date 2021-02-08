package ${package};

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TrafficViolationTest {
    
    @Test
    public void testEvaluateTrafficViolation() {
        given()
               .body("{\n" +
                     "    \"Driver\": {\n" +
                     "        \"Points\": 2\n" +
                     "    },\n" +
                     "    \"Violation\": {\n" +
                     "        \"Type\": \"speed\",\n" +
                     "        \"Actual Speed\": 120,\n" +
                     "        \"Speed Limit\": 100\n" +
                     "    }\n" +
                     "}")
               .contentType(ContentType.JSON)
          .when()
               .post("/Traffic Violation")
          .then()
             .statusCode(200)
               .body("'Should the driver be suspended?'", is("No"));
    }
}