package ${package};

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class GreetingsTest {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void testOrderProcess() throws Exception {
        RestAssured.port = randomServerPort;
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
