/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.schooltimetabling.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.acme.schooltimetabling.domain.Timeslot;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class TimeslotResourceTest {

    @Test
    public void getAll() {
        List<Timeslot> timeslotList = given()
                .when().get("/timeslots")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Timeslot.class);
        assertFalse(timeslotList.isEmpty());
        Timeslot firstTimeslot = timeslotList.get(0);
        assertEquals(DayOfWeek.MONDAY, firstTimeslot.getDayOfWeek());
        assertEquals(LocalTime.of(8, 30), firstTimeslot.getStartTime());
        assertEquals(LocalTime.of(9, 30), firstTimeslot.getEndTime());
    }

    @Test
    void addAndRemove() {
        Timeslot timeslot = given()
                .when()
                .contentType(ContentType.JSON)
                .body(new Timeslot(DayOfWeek.SUNDAY, LocalTime.of(20, 0), LocalTime.of(21, 0)))
                .post("/timeslots")
                .then()
                .statusCode(202)
                .extract().as(Timeslot.class);

        given()
                .when()
                .delete("/timeslots/{id}", timeslot.getId())
                .then()
                .statusCode(200);
    }

}
