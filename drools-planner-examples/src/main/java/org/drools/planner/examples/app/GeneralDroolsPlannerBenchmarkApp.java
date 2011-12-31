/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.app;

import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.common.app.CommonBenchmarkApp;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.manners2009.domain.Manners2009;
import org.drools.planner.examples.nqueens.domain.NQueens;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.pas.domain.PatientAdmissionSchedule;
import org.drools.planner.examples.travelingtournament.domain.TravelingTournament;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanTour;

public class GeneralDroolsPlannerBenchmarkApp extends CommonBenchmarkApp {

    public static final String DEFAULT_SOLVER_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/app/benchmark/generalDroolsPlannerBenchmarkConfig.xml";

    public static void main(String[] args) {
        String solverConfig = DEFAULT_SOLVER_BENCHMARK_CONFIG;
        new GeneralDroolsPlannerBenchmarkApp(solverConfig).process();
    }

    public GeneralDroolsPlannerBenchmarkApp(String solverBenchmarkConfig) {
        super(solverBenchmarkConfig);
    }

}
