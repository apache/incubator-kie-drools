/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.example;

import java.time.DayOfWeek;
import java.time.LocalTime;

import org.optaplanner.spring.boot.example.domain.Lesson;
import org.optaplanner.spring.boot.example.domain.Room;
import org.optaplanner.spring.boot.example.domain.Timeslot;
import org.optaplanner.spring.boot.example.service.LessonRepository;
import org.optaplanner.spring.boot.example.service.RoomRepository;
import org.optaplanner.spring.boot.example.service.TimeslotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TimeTableSpringBootApp {

    public static void main(String[] args) {
        SpringApplication.run(TimeTableSpringBootApp.class, args);
    }
    
    @Bean
    public CommandLineRunner demoData(
            TimeslotRepository timeslotRepository, RoomRepository roomRepository,
            LessonRepository lessonRepository) {
        return (args) -> {
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            Timeslot timeslotMonday0930 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30));
            timeslotRepository.save(timeslotMonday0930);
            Timeslot timeslotMonday1030 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30));
            timeslotRepository.save(timeslotMonday1030);
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

            Room roomA = new Room("Room A");
            roomRepository.save(roomA);
            Room roomB = new Room("Room B");
            roomRepository.save(roomB);
            roomRepository.save(new Room("Room C"));

            lessonRepository.save(new Lesson("Math", "B. May", "9th grade"));
            lessonRepository.save(new Lesson("Math", "B. May", "9th grade"));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "9th grade"));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "9th grade"));
            Lesson lesson4 = new Lesson("Geography", "M. Polo", "9th grade");
            lesson4.setTimeslot(timeslotMonday0930);
            lesson4.setRoom(roomA);
            lessonRepository.save(lesson4);
            Lesson lesson5 = new Lesson("History", "I. Jones", "9th grade");
            lesson5.setTimeslot(timeslotMonday1030);
            lesson5.setRoom(roomA);
            lessonRepository.save(lesson5);
            Lesson lesson6 = new Lesson("English", "I. Jones", "9th grade");
            lesson6.setTimeslot(timeslotMonday1030);
            lesson6.setRoom(roomB);
            lessonRepository.save(lesson6);
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade"));

            lessonRepository.save(new Lesson("Math", "B. May", "10th grade"));
            lessonRepository.save(new Lesson("Math", "B. May", "10th grade"));
            lessonRepository.save(new Lesson("Math", "B. May", "10th grade"));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "10th grade"));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "10th grade"));
            lessonRepository.save(new Lesson("Geography", "M. Polo", "10th grade"));
            lessonRepository.save(new Lesson("History", "I. Jones", "10th grade"));
            lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "10th grade"));
            lessonRepository.save(new Lesson("French", "M. Curie", "10th grade"));
        };
    }

}
