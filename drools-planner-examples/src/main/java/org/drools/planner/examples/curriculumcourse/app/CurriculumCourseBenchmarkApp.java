/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.curriculumcourse.app;

import java.io.File;

import org.drools.planner.examples.common.app.CommonBenchmarkApp;
import org.drools.planner.examples.curriculumcourse.domain.CurriculumCourseSchedule;

public class CurriculumCourseBenchmarkApp extends CommonBenchmarkApp {

    public static final String DEFAULT_SOLVER_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/curriculumcourse/benchmark/curriculumCourseSolverBenchmarkConfig.xml";
    public static final String STEP_LIMIT_SOLVER_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/curriculumcourse/benchmark/curriculumCourseStepLimitSolverBenchmarkConfig.xml";

    public static void main(String[] args) {
        String solverConfig;
        if (args.length > 0) {
            if (args[0].equals("default")) {
                solverConfig = DEFAULT_SOLVER_BENCHMARK_CONFIG;
            } else if (args[0].equals("stepLimit")) {
                solverConfig = STEP_LIMIT_SOLVER_BENCHMARK_CONFIG;
            } else {
                throw new IllegalArgumentException("The program argument (" + args[0] + ") is not supported.");
            }
        } else {
            solverConfig = DEFAULT_SOLVER_BENCHMARK_CONFIG;
        }
        new CurriculumCourseBenchmarkApp(solverConfig).process();
    }

    public CurriculumCourseBenchmarkApp(String solverBenchmarkConfig) {
        super(solverBenchmarkConfig, CurriculumCourseSchedule.class);
    }

}
