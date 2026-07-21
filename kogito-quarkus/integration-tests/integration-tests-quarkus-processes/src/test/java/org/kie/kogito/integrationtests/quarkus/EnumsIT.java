/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.integrationtests.quarkus;

import java.util.HashMap;
import java.util.Map;

import org.acme.examples.model.Movie;
import org.acme.examples.model.MovieGenre;
import org.acme.examples.model.Rating;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@QuarkusIntegrationTest
class EnumsIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testSubmitMovie() {
        Map<String, Object> params = new HashMap<>();
        Movie movie = new Movie().setGenre(MovieGenre.COMEDY).setName("The Holy Grail").setReleaseYear(1975).setRating(Rating.UR);
        params.put("movie", movie);
        params.put("rating", movie.getRating());

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
                .body("movie.releaseYear", equalTo(movie.getReleaseYear()))
                .body("rating", equalTo(movie.getRating().name()))
                .extract()
                .path("id");

        String taskId = given()
                .queryParam("group", "customer")
                .when()
                .get("/cinema/{pid}/tasks", pid)
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].name", is("ReviewRatingTask"))
                .extract()
                .path("[0].id");

        Map<String, Object> reviewedRating = new HashMap<>();
        reviewedRating.put("reviewedRating", Rating.PG_13);
        given()
                .contentType(ContentType.JSON)
                .queryParam("group", "customer")
                .when()
                .body(reviewedRating)
                .post("/cinema/{pid}/ReviewRatingTask/{taskId}", pid, taskId)
                .then()
                .statusCode(200)
                .body("rating", equalTo(Rating.PG_13.name()));
    }
}
