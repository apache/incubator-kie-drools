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

package org.optaplanner.spring.boot.example.solver;

import org.optaplanner.spring.boot.example.domain.TimeTable;
import org.optaplanner.spring.boot.example.domain.TimeTableView;
import org.optaplanner.spring.boot.example.persistence.TimeTableRepository;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverManager;
import org.optaplanner.spring.boot.example.poc.api.solver.SolverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/timeTable")
public class TimeTableSolverService {

    // There is only one time table, so there is only timeTableId (= problemId).
    public static final Long TIME_TABLE_ID = 1L;

    @Autowired
    private TimeTableRepository timeTableRepository;
    @Autowired
    private SolverManager<TimeTable, Long> solverManager;

    // To try, open http://localhost:8080/timeTable
    @GetMapping()
    public TimeTableView getTimeTableView() {
        TimeTable timeTable = timeTableRepository.find();
        solverManager.updateScore(timeTable);
        SolverStatus solverStatus = solverManager.getSolverStatus(TIME_TABLE_ID);
        return new TimeTableView(timeTable, solverStatus);
    }

    @PostMapping("/solve")
    public void solve() {
        solverManager.solveObserving(TIME_TABLE_ID, timeTableRepository::find, timeTableRepository::save);
    }

    public void reloadProblem() {
        solverManager.reloadProblem(TIME_TABLE_ID, timeTableRepository::find);
    }

    @PostMapping("/stopSolving")
    public void stopSolving() {
        solverManager.terminateEarly(TIME_TABLE_ID);
    }

}
