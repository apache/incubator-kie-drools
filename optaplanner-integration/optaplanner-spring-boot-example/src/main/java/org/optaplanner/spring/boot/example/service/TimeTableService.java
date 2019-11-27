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
import org.optaplanner.spring.boot.example.poc.api.solver.SolverFuture;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeTable")
public class TimeTableService {

    @Autowired
    private TimeslotRepository timeslotRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SolverManager<TimeTable> solverManager;

    private SolverFuture solverFuture = null;

    // To try, open http://localhost:8080/timeTable
    @RequestMapping()
    public TimeTable refreshTimeTable() {
        TimeTable timeTable = new TimeTable(
                timeslotRepository.findAll(),
                roomRepository.findAll(),
                lessonRepository.findAll()
        );
        // TODO add score
        return timeTable;
    }

    @PostMapping("/solve")
    public void solve() {
        TimeTable problemTimeTable = refreshTimeTable();
        // TODO Race condition if room is added while solving and new best solutions still occur
        solverFuture = solverManager.solve(problemTimeTable, solutionTimeTable -> {
            for (Lesson lesson : solutionTimeTable.getLessonList()) {
                // TODO this is awfully naive with optimistic locking
                lessonRepository.save(lesson);
            }
        });
    }

}
