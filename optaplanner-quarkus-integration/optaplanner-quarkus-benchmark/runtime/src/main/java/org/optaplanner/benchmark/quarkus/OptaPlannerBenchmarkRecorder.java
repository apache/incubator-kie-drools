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
import java.util.function.Supplier;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.quarkus.config.OptaPlannerBenchmarkRuntimeConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerBenchmarkRecorder {
    public Supplier<PlannerBenchmarkConfig> benchmarkConfigSupplier(PlannerBenchmarkConfig benchmarkConfig) {
        return () -> {
            OptaPlannerBenchmarkRuntimeConfig optaPlannerRuntimeConfig =
                    Arc.container().instance(OptaPlannerBenchmarkRuntimeConfig.class).get();
            SolverConfig solverConfig =
                    Arc.container().instance(SolverConfig.class).get();
            updateBenchmarkConfigWithRuntimeProperties(benchmarkConfig, optaPlannerRuntimeConfig, solverConfig);
            return benchmarkConfig;
        };
    }

    private void updateBenchmarkConfigWithRuntimeProperties(PlannerBenchmarkConfig plannerBenchmarkConfig,
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

        TerminationConfig terminationConfig = plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig()
                .getSolverConfig().getTerminationConfig();
        benchmarkRuntimeConfig.termination.spentLimit.ifPresent(terminationConfig::setSpentLimit);
        benchmarkRuntimeConfig.termination.unimprovedSpentLimit
                .ifPresent(terminationConfig::setUnimprovedSpentLimit);
        benchmarkRuntimeConfig.termination.bestScoreLimit.ifPresent(terminationConfig::setBestScoreLimit);

        if (!isTerminationConfigured(terminationConfig)) {
            throw new IllegalStateException("At least one of the properties " +
                    "quarkus.optaplanner.benchmark.solver.termination.spent-limit, " +
                    "quarkus.optaplanner.benchmark.solver.termination.best-score-limit, " +
                    "quarkus.optaplanner.benchmark.solver.termination.unimproved-spent-limit " +
                    "is required if the inherited solver config does not have termination configured.");
        }

        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() == null
                && plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList() == null) {
            plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        }
    }

    private boolean isTerminationConfigured(TerminationConfig terminationConfig) {
        return terminationConfig.getTerminationClass() != null ||
                terminationConfig.getSpentLimit() != null ||
                terminationConfig.getBestScoreLimit() != null ||
                terminationConfig.getUnimprovedSpentLimit() != null ||
                terminationConfig.getStepCountLimit() != null ||
                terminationConfig.getTerminationConfigList() != null;
    }
}
