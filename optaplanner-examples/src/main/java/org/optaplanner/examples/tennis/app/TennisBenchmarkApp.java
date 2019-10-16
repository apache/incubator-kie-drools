/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tennis.app;

import java.io.File;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.persistence.TennisGenerator;

public class TennisBenchmarkApp extends LoggingMain {

    public static void main(String[] args) {
        new TennisBenchmarkApp().benchmark();
    }

    private final PlannerBenchmarkFactory benchmarkFactory;

    public TennisBenchmarkApp() {
        benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
                TennisApp.SOLVER_CONFIG, new File("local/data/tennis"));
    }

    public void benchmark() {
        TennisSolution problem = new TennisGenerator().createTennisSolution();
        PlannerBenchmark plannerBenchmark = benchmarkFactory.buildPlannerBenchmark(problem);
        plannerBenchmark.benchmark();
    }

}
