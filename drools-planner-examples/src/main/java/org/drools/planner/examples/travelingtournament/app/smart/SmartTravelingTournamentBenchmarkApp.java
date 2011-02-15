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

package org.drools.planner.examples.travelingtournament.app.smart;

import java.io.File;

import org.drools.planner.examples.common.app.CommonBenchmarkApp;
import org.drools.planner.examples.travelingtournament.domain.TravelingTournament;

public class SmartTravelingTournamentBenchmarkApp extends CommonBenchmarkApp {

    public static final String SOLVER_BENCHMARK_CONFIG_PREFIX
            = "/org/drools/planner/examples/travelingtournament/benchmark/smart/";
    public static final String SOLVER_BENCHMARK_CONFIG
            = SOLVER_BENCHMARK_CONFIG_PREFIX + "smartTravelingTournamentSolverBenchmarkConfig.xml";
    public static final String STEP_LIMIT_SOLVER_BENCHMARK_CONFIG
            = SOLVER_BENCHMARK_CONFIG_PREFIX + "smartTravelingTournamentStepLimitSolverBenchmarkConfig.xml";

    public static void main(String[] args) {
        String solverBenchmarkConfig;
        if (args.length > 0) {
            // default is a workaround for http://jira.codehaus.org/browse/MEXEC-35
            if (args[0].equals("default")) {
                solverBenchmarkConfig = SOLVER_BENCHMARK_CONFIG;
            } else if (args[0].equals("stepLimit")) {
                solverBenchmarkConfig = STEP_LIMIT_SOLVER_BENCHMARK_CONFIG;
            } else {
                solverBenchmarkConfig = SOLVER_BENCHMARK_CONFIG_PREFIX + args[0] + "SolverBenchmarkConfig.xml";
            }
        } else {
            solverBenchmarkConfig = SOLVER_BENCHMARK_CONFIG;
        }
        new SmartTravelingTournamentBenchmarkApp(solverBenchmarkConfig).process();
    }

    public SmartTravelingTournamentBenchmarkApp(String solverBenchmarkConfig) {
        super(solverBenchmarkConfig, TravelingTournament.class);
    }

}
