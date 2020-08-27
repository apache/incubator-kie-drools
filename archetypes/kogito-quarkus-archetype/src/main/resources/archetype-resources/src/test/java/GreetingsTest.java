package ${package};

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class GreetingsTest {

    @Test
    public void testHelloEndpoint() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body("{}")
        .when()
            .post("/greetings")
        .then()
            .statusCode(201)
            .header("Location", notNullValue())
            .body("id", notNullValue());
    }

}