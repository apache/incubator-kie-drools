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

package org.acme.schooltimetabling.bootstrap;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Room;
import org.acme.schooltimetabling.domain.Timeslot;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class DemoDataGenerator {

    @ConfigProperty(name = "timeTable.demoData", defaultValue = "SMALL")
    DemoData demoData;

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.NONE) {
            return;
        }

        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        if (demoData == DemoData.LARGE) {
            timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
        }
        Timeslot.persist(timeslotList);

        List<Room> roomList = new ArrayList<>(3);
        roomList.add(new Room("Room A"));
        roomList.add(new Room("Room B"));
        roomList.add(new Room("Room C"));
        if (demoData == DemoData.LARGE) {
            roomList.add(new Room("Room D"));
            roomList.add(new Room("Room E"));
            roomList.add(new Room("Room F"));
        }
        Room.persist(roomList);

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
        lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
        lessonList.add(new Lesson("Physics", "M. Curie", "9th grade"));
        lessonList.add(new Lesson("Chemistry", "M. Curie", "9th grade"));
        lessonList.add(new Lesson("Biology", "C. Darwin", "9th grade"));
        lessonList.add(new Lesson("History", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "9th grade"));
        if (demoData == DemoData.LARGE) {
            lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "9th grade"));
            lessonList.add(new Lesson("ICT", "A. Turing", "9th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "9th grade"));
            lessonList.add(new Lesson("Geography", "C. Darwin", "9th grade"));
            lessonList.add(new Lesson("Geology", "C. Darwin", "9th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "9th grade"));
            lessonList.add(new Lesson("English", "I. Jones", "9th grade"));
            lessonList.add(new Lesson("Drama", "I. Jones", "9th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "9th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "9th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "9th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "9th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "9th grade"));
        }

        lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
        lessonList.add(new Lesson("Physics", "M. Curie", "10th grade"));
        lessonList.add(new Lesson("Chemistry", "M. Curie", "10th grade"));
        lessonList.add(new Lesson("French", "M. Curie", "10th grade"));
        lessonList.add(new Lesson("Geography", "C. Darwin", "10th grade"));
        lessonList.add(new Lesson("History", "I. Jones", "10th grade"));
        lessonList.add(new Lesson("English", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson("Spanish", "P. Cruz", "10th grade"));
        if (demoData == DemoData.LARGE) {
            lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "10th grade"));
            lessonList.add(new Lesson("ICT", "A. Turing", "10th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "10th grade"));
            lessonList.add(new Lesson("Biology", "C. Darwin", "10th grade"));
            lessonList.add(new Lesson("Geology", "C. Darwin", "10th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "10th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "10th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "10th grade"));
            lessonList.add(new Lesson("Drama", "I. Jones", "10th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "10th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "10th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "10th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "10th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "10th grade"));

            lessonList.add(new Lesson("Math", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("ICT", "A. Turing", "11th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "11th grade"));
            lessonList.add(new Lesson("Chemistry", "M. Curie", "11th grade"));
            lessonList.add(new Lesson("French", "M. Curie", "11th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "11th grade"));
            lessonList.add(new Lesson("Geography", "C. Darwin", "11th grade"));
            lessonList.add(new Lesson("Biology", "C. Darwin", "11th grade"));
            lessonList.add(new Lesson("Geology", "C. Darwin", "11th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "11th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "11th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "11th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "11th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "11th grade"));
            lessonList.add(new Lesson("Spanish", "P. Cruz", "11th grade"));
            lessonList.add(new Lesson("Drama", "P. Cruz", "11th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "11th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "11th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "11th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "11th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "11th grade"));

            lessonList.add(new Lesson("Math", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("Math", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("ICT", "A. Turing", "12th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "12th grade"));
            lessonList.add(new Lesson("Chemistry", "M. Curie", "12th grade"));
            lessonList.add(new Lesson("French", "M. Curie", "12th grade"));
            lessonList.add(new Lesson("Physics", "M. Curie", "12th grade"));
            lessonList.add(new Lesson("Geography", "C. Darwin", "12th grade"));
            lessonList.add(new Lesson("Biology", "C. Darwin", "12th grade"));
            lessonList.add(new Lesson("Geology", "C. Darwin", "12th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "12th grade"));
            lessonList.add(new Lesson("History", "I. Jones", "12th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "12th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "12th grade"));
            lessonList.add(new Lesson("English", "P. Cruz", "12th grade"));
            lessonList.add(new Lesson("Spanish", "P. Cruz", "12th grade"));
            lessonList.add(new Lesson("Drama", "P. Cruz", "12th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "12th grade"));
            lessonList.add(new Lesson("Art", "S. Dali", "12th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "12th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "12th grade"));
            lessonList.add(new Lesson("Physical education", "C. Lewis", "12th grade"));
        }

        Lesson lesson = lessonList.get(0);
        lesson.setTimeslot(timeslotList.get(0));
        lesson.setRoom(roomList.get(0));
        Lesson.persist(lessonList);
    }

    public enum DemoData {
        NONE,
        SMALL,
        LARGE
    }

}
