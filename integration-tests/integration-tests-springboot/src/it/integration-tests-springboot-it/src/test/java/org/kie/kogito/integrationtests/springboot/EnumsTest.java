/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.integrationtests.springboot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.restassured.http.ContentType;
import org.acme.examples.model.Movie;
import org.acme.examples.model.MovieGenre;
import org.acme.examples.model.Rating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.Conditional.class)
class EnumsTest extends BaseRestTest {

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitMovie() {
        Map<String, Object> params = new HashMap<>();
        Movie movie = new Movie().setGenre(MovieGenre.COMEDY).setName("The Holy Grail").setReleaseYear(1975).setRating(Rating.UR);
        params.put("movie", movie);

        String pid = given()
                .contentType(ContentType.JSON)
            .when()
                .body(params)
                .post("/cinema")
            .then()
                .statusCode(201)
                .body("id", not(emptyOrNullString()))
                .body("movie.name", equalTo(movie.getName()))
                .body("movie.genre", equalTo(movie.getGenre().name()))
                .body("movie.rating", equalTo(movie.getRating().name()))
                .body("movie.releaseYear", equalTo(movie.getReleaseYear()))
            .extract()
                .path("id");

        WorkItem task = given()
            .when()
                .get("/cinema/{pid}/tasks", pid)
            .then()
                .statusCode(200)
                .body("$.size", is(1))
            .extract()
                .as(TestWorkItem[].class)[0];

        assertEquals("ReviewRatingTask", task.getName());

        given()
                .contentType(ContentType.JSON)
            .when()
                .body(Collections.singletonMap("reviewedRating", Rating.PG_13))
                .post("/cinema/{pid}/ReviewRatingTask/{taskId}", pid, task.getId())
            .then()
                .statusCode(200)
                .body("movie.rating", equalTo(Rating.PG_13.name()));
    }
}
