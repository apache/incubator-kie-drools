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

import org.optaplanner.spring.boot.example.domain.Lesson;
import org.optaplanner.spring.boot.example.domain.TimeTable;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeTable")
public class TimeTableService {

    public static final Long TENANT_ID = 1L;

    @Autowired
    private TimeslotRepository timeslotRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SolverManager<TimeTable, Long> solverManager;

    // To try, open http://localhost:8080/timeTable
    @GetMapping()
    public TimeTable getTimeTableView() {
        TimeTable timeTable = getTimeTable();
        // TODO add to response: score, solving status, indictments etc
        return timeTable;
    }

    private TimeTable getTimeTable() {
        return new TimeTable(
                    timeslotRepository.findAll(),
                    roomRepository.findAll(),
                    lessonRepository.findAll());
    }

    @PostMapping("/solve")
    public void solve() {
        solverManager.solveObserving(TENANT_ID, this::getTimeTable, this::saveSolution);
    }

    public void saveSolution(TimeTable solution) {
        for (Lesson lesson : solution.getLessonList()) {
            // TODO this is awfully naive with optimistic locking
            lessonRepository.save(lesson);
        }
    }

    public void reloadProblem() {
        solverManager.reloadProblem(TENANT_ID, this::getTimeTable);
    }

    @PostMapping("/stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(TENANT_ID);
    }

}
