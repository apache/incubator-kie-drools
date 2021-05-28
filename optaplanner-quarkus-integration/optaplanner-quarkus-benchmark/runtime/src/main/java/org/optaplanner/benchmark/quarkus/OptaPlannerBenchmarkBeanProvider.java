/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.quarkus;

import java.io.File;
import java.util.Collections;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import io.quarkus.arc.DefaultBean;

public class OptaPlannerBenchmarkBeanProvider {

    @DefaultBean
    @Singleton
    @Produces
    PlannerBenchmarkFactory benchmarkFactory(PlannerBenchmarkConfig plannerBenchmarkConfig,
            OptaPlannerBenchmarkRuntimeConfig benchmarkRuntimeConfig,
            SolverConfig solverConfig) {
        if (plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig() == null) {
            ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
            SolverBenchmarkConfig solverBenchmarkConfig = new SolverBenchmarkConfig();
            SolverConfig benchmarkSolverConfig = new SolverConfig();
            benchmarkSolverConfig.inherit(solverConfig);

            solverBenchmarkConfig.setSolverConfig(benchmarkSolverConfig);
            solverBenchmarkConfig.setProblemBenchmarksConfig(problemBenchmarksConfig);

            plannerBenchmarkConfig.setBenchmarkDirectory(new File(benchmarkRuntimeConfig.resultDirectory));
            plannerBenchmarkConfig.setInheritedSolverBenchmarkConfig(solverBenchmarkConfig);
        }

        if (plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig().getTerminationConfig()
                .getTerminationClass() == null
                || benchmarkRuntimeConfig.terminationBuildTimeConfig.spentLimit.isPresent()) {
            plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig().getSolverConfig()
                    .setTerminationConfig(new TerminationConfig()
                            .withSpentLimit(benchmarkRuntimeConfig.terminationBuildTimeConfig.spentLimit
                                    .orElseThrow(() -> new IllegalStateException(
                                            "Property quarkus.optaplanner.benchmark.solver.termination.spent-limit is required if the inherited solver config does not have termination configured."))));
        }

        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() == null
                && plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList() == null) {
            plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        }

        return PlannerBenchmarkFactory.create(plannerBenchmarkConfig);
    }

}
