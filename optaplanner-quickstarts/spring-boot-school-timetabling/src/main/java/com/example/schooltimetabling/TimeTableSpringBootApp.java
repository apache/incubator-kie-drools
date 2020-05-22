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

package com.example.schooltimetabling;

import java.time.DayOfWeek;
import java.time.LocalTime;

import com.example.schooltimetabling.domain.Lesson;
import com.example.schooltimetabling.domain.Room;
import com.example.schooltimetabling.domain.Timeslot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;

import com.example.schooltimetabling.persistence.LessonRepository;
import com.example.schooltimetabling.persistence.RoomRepository;
import com.example.schooltimetabling.persistence.TimeslotRepository;

@SpringBootApplication
public class TimeTableSpringBootApp {

    public static void main(String[] args) {
        SpringApplication.run(TimeTableSpringBootApp.class, args);
    }

    @Value("${timeTable.demoData:SMALL}")
    private DemoData demoData;

    @Bean
    public CommandLineRunner demoData(
            TimeslotRepository timeslotRepository,
            RoomRepository roomRepository,
            LessonRepository lessonRepository) {
        return (args) -> {
            if (demoData == DemoData.NONE) {
                return;
            }

            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
            timeslotRepository.save(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
            if (demoData == DemoData.LARGE) {
                timeslotRepository.save(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
                timeslotRepository.save(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));
            }

            roomRepository.save(new Room("Room A"));
            roomRepository.save(new Room("Room B"));
            roomRepository.save(new Room("Room C"));
            if (demoData == DemoData.LARGE) {
                roomRepository.save(new Room("Room D"));
                roomRepository.save(new Room("Room E"));
                roomRepository.save(new Room("Room F"));
            }

            lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade"));
            lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade"));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "9th grade"));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "9th grade"));
            lessonRepository.save(new Lesson("Biology", "C. Darwin", "9th grade"));
            lessonRepository.save(new Lesson("History", "I. Jones", "9th grade"));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade"));
            lessonRepository.save(new Lesson("English", "I. Jones", "9th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "9th grade"));
            if (demoData == DemoData.LARGE) {
                lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "9th grade"));
                lessonRepository.save(new Lesson("ICT", "A. Turing", "9th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "9th grade"));
                lessonRepository.save(new Lesson("Geography", "C. Darwin", "9th grade"));
                lessonRepository.save(new Lesson("Geology", "C. Darwin", "9th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "9th grade"));
                lessonRepository.save(new Lesson("English", "I. Jones", "9th grade"));
                lessonRepository.save(new Lesson("Drama", "I. Jones", "9th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "9th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "9th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "9th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "9th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "9th grade"));
            }

            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade"));
            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade"));
            lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade"));
            lessonRepository.save(new Lesson("Physics", "M. Curie", "10th grade"));
            lessonRepository.save(new Lesson("Chemistry", "M. Curie", "10th grade"));
            lessonRepository.save(new Lesson("French", "M. Curie", "10th grade"));
            lessonRepository.save(new Lesson("Geography", "C. Darwin", "10th grade"));
            lessonRepository.save(new Lesson("History", "I. Jones", "10th grade"));
            lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade"));
            lessonRepository.save(new Lesson("Spanish", "P. Cruz", "10th grade"));
            if (demoData == DemoData.LARGE) {
                lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "10th grade"));
                lessonRepository.save(new Lesson("ICT", "A. Turing", "10th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "10th grade"));
                lessonRepository.save(new Lesson("Biology", "C. Darwin", "10th grade"));
                lessonRepository.save(new Lesson("Geology", "C. Darwin", "10th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "10th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "10th grade"));
                lessonRepository.save(new Lesson("Drama", "I. Jones", "10th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "10th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "10th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "10th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "10th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "10th grade"));

                lessonRepository.save(new Lesson("Math", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("ICT", "A. Turing", "11th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "11th grade"));
                lessonRepository.save(new Lesson("Chemistry", "M. Curie", "11th grade"));
                lessonRepository.save(new Lesson("French", "M. Curie", "11th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "11th grade"));
                lessonRepository.save(new Lesson("Geography", "C. Darwin", "11th grade"));
                lessonRepository.save(new Lesson("Biology", "C. Darwin", "11th grade"));
                lessonRepository.save(new Lesson("Geology", "C. Darwin", "11th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "11th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "11th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "11th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "11th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "11th grade"));
                lessonRepository.save(new Lesson("Spanish", "P. Cruz", "11th grade"));
                lessonRepository.save(new Lesson("Drama", "P. Cruz", "11th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "11th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "11th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "11th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "11th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "11th grade"));

                lessonRepository.save(new Lesson("Math", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("Math", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("ICT", "A. Turing", "12th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "12th grade"));
                lessonRepository.save(new Lesson("Chemistry", "M. Curie", "12th grade"));
                lessonRepository.save(new Lesson("French", "M. Curie", "12th grade"));
                lessonRepository.save(new Lesson("Physics", "M. Curie", "12th grade"));
                lessonRepository.save(new Lesson("Geography", "C. Darwin", "12th grade"));
                lessonRepository.save(new Lesson("Biology", "C. Darwin", "12th grade"));
                lessonRepository.save(new Lesson("Geology", "C. Darwin", "12th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "12th grade"));
                lessonRepository.save(new Lesson("History", "I. Jones", "12th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "12th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "12th grade"));
                lessonRepository.save(new Lesson("English", "P. Cruz", "12th grade"));
                lessonRepository.save(new Lesson("Spanish", "P. Cruz", "12th grade"));
                lessonRepository.save(new Lesson("Drama", "P. Cruz", "12th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "12th grade"));
                lessonRepository.save(new Lesson("Art", "S. Dali", "12th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "12th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "12th grade"));
                lessonRepository.save(new Lesson("Physical education", "C. Lewis", "12th grade"));
            }

            Lesson lesson = lessonRepository.findAll(Sort.by("id")).iterator().next();
            lesson.setTimeslot(timeslotRepository.findAll(Sort.by("id")).iterator().next());
            lesson.setRoom(roomRepository.findAll(Sort.by("id")).iterator().next());
            lessonRepository.save(lesson);
        };
    }

    public enum DemoData {
        NONE,
        SMALL,
        LARGE
    }

}
