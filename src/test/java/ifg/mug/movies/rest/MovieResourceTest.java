package ifg.mug.movies.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class MovieResourceTest {

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/movie")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }
}
