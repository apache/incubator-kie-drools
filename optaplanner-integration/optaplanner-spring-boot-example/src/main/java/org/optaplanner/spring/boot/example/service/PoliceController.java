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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.spring.boot.example.domain.Detective;
import org.optaplanner.spring.boot.example.domain.Investigation;
import org.optaplanner.spring.boot.example.domain.PoliceSolution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/police")
public class PoliceController {

    @Autowired
    SolverManager<PoliceSolution> solverManager;

    // To trigger this, open http://localhost:8080/police/solve?investigationListSize=5
    @RequestMapping("/solve")
    public UUID solve(@RequestParam int investigationListSize) {
        PoliceSolution problem = generateProblem(investigationListSize);
        return solverManager.solve(problem);
    }

    private PoliceSolution generateProblem(int investigationListSize) {
        Random random = new Random(37);
        int detectiveListSize = (investigationListSize + 3) / 4;
        List<Detective> detectiveList = new ArrayList<>(detectiveListSize);
        for (int i = 0; i < detectiveListSize; i++) {
            // A quarter of all detective work part-time
            Duration workDuration = (i % 4 != 3) ? Duration.ofHours(40) : Duration.ofHours(20);
            detectiveList.add(new Detective("Detective " + i, workDuration));
        }
        List<Investigation> investigationList = new ArrayList<>(investigationListSize);
        for (int i = 0; i < investigationListSize; i++) {
            // On average, there is a capacity of 8.75 hours per investigation
            // Random distribution of on average 7 hours for the estimated duration per investigation
            Duration estimatedDuration = Duration.ofHours(1 + random.nextInt(12));
            investigationList.add(new Investigation(i, estimatedDuration));
        }
        return new PoliceSolution(detectiveList, investigationList);
    }

}
