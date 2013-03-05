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

package org.drools.planner.examples.nurserostering.app;

import org.drools.planner.examples.common.app.CommonBenchmarkApp;

public class NurseRosteringBenchmarkApp extends CommonBenchmarkApp {

    public static final String SPRINT_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/nurserostering/benchmark/nurseRosteringSprintBenchmarkConfig.xml";
    public static final String MEDIUM_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/nurserostering/benchmark/nurseRosteringMediumBenchmarkConfig.xml";
    public static final String LONG_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/nurserostering/benchmark/nurseRosteringLongBenchmarkConfig.xml";
    public static final String STEP_LIMIT_BENCHMARK_CONFIG
            = "/org/drools/planner/examples/nurserostering/benchmark/nurseRosteringStepLimitBenchmarkConfig.xml";

    public static void main(String[] args) {
        String benchmarkConfig;
        if (args.length > 0) {
            if (args[0].equals("sprint")) {
                benchmarkConfig = SPRINT_BENCHMARK_CONFIG;
            } else if (args[0].equals("medium")) {
                benchmarkConfig = MEDIUM_BENCHMARK_CONFIG;
            } else if (args[0].equals("long")) {
                benchmarkConfig = LONG_BENCHMARK_CONFIG;
            } else if (args[0].equals("stepLimit")) {
                benchmarkConfig = STEP_LIMIT_BENCHMARK_CONFIG;
            } else {
                throw new IllegalArgumentException("The program argument (" + args[0] + ") is not supported.");
            }
        } else {
            benchmarkConfig = MEDIUM_BENCHMARK_CONFIG;
        }
        new NurseRosteringBenchmarkApp().buildAndBenchmark(benchmarkConfig);
    }

}
