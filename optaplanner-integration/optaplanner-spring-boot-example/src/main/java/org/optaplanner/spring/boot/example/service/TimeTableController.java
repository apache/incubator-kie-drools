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

package org.optaplanner.spring.boot.example.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.spring.boot.example.domain.Lesson;
import org.optaplanner.spring.boot.example.domain.Room;
import org.optaplanner.spring.boot.example.domain.TimeTable;
import org.optaplanner.spring.boot.example.domain.Timeslot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeTable/{problemId}")
public class TimeTableController {

    @Autowired
    SolverManager<TimeTable> solverManager;

    private ConcurrentMap<Long, TimeTable> problemIdToTimeTableMap = new ConcurrentHashMap<>();

    // To try, open http://localhost:8080/timeTable/7
    @RequestMapping()
    public TimeTable get(@PathVariable long problemId) {
        return problemIdToTimeTableMap.computeIfAbsent(problemId, this::generateProblem);
    }

    // To try:  curl -d '{"name":"Room Z"}' -H "Content-Type: application/json" -X POST http://localhost:8080/timeTable/7/addRoom
    @PostMapping("/addRoom")
    public void addRoom(@PathVariable long problemId, @RequestBody Room room) {
        // TODO Race condition with proper synchronization of these 3 actions
        TimeTable timeTable = get(problemId);
        long nextRoomId = timeTable.getRoomList().stream()
                .map(Room::getId).max(Comparator.naturalOrder())
                .orElse(0L)
                + 1L;
        timeTable.getRoomList().add(new Room(nextRoomId, room.getName()));
    }

    @PostMapping("/solve")
    public void solve(@PathVariable long problemId) {
        // TODO Race condition ?
        TimeTable timeTable = get(problemId);
        solverManager.solve(timeTable);
    }

    public TimeTable generateProblem(Long problemId) {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        long timeslotId = 1L;
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(timeslotId++, DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        List<Room> roomList = new ArrayList<>(3);
        long roomId = 1L;
        roomList.add(new Room(roomId++, "Room A"));
        roomList.add(new Room(roomId++, "Room B"));
        roomList.add(new Room(roomId++, "Room C"));

        List< Lesson > lessonList = new ArrayList<>();
        long lessonId = 1L;
        lessonList.add(new Lesson(lessonId++, "Math", "B. May", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Math", "B. May", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Physics", "M. Curie", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Chemistry", "M. Curie", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Geography", "M. Polo", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "History", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "English", "I. Jones", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Spanish", "P. Cruz", "9th grade"));
        lessonList.add(new Lesson(lessonId++, "Spanish", "P. Cruz", "9th grade"));

        lessonList.add(new Lesson(lessonId++, "Math", "B. May", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Math", "B. May", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Math", "B. May", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Physics", "M. Curie", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Chemistry", "M. Curie", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Geography", "M. Polo", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "History", "I. Jones", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "English", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "Spanish", "P. Cruz", "10th grade"));
        lessonList.add(new Lesson(lessonId++, "French", "M. Curie", "10th grade"));

        lessonList.get(4).setTimeslot(timeslotList.get(2));
        lessonList.get(4).setRoom(roomList.get(0));
        lessonList.get(5).setTimeslot(timeslotList.get(3));
        lessonList.get(5).setRoom(roomList.get(1));
        lessonList.get(6).setTimeslot(timeslotList.get(3));
        lessonList.get(6).setRoom(roomList.get(1));

        return new TimeTable(problemId, timeslotList, roomList, lessonList);
    }

}
