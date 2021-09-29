/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.counterfactual;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

/**
 * Counterfactual explainer configuration parameters.
 */
public class CounterfactualConfig {

    private static final double DEFAULT_GOAL_THRESHOLD = 0.01;

    private Executor executor = ForkJoinPool.commonPool();
    private SolverConfig solverConfig = SolverConfigBuilder.builder().build();
    private double goalThreshold = DEFAULT_GOAL_THRESHOLD;
    private Function<SolverConfig, SolverManager<CounterfactualSolution, UUID>> solverManagerFactory =
            solverConfig -> SolverManager.create(solverConfig, new SolverManagerConfig());

    public Function<SolverConfig, SolverManager<CounterfactualSolution, UUID>> getSolverManagerFactory() {
        return solverManagerFactory;
    }

    public CounterfactualConfig withSolverManagerFactory(
            Function<SolverConfig, SolverManager<CounterfactualSolution, UUID>> solverManagerFactory) {
        this.solverManagerFactory = solverManagerFactory;
        return this;
    }

    public Executor getExecutor() {
        return executor;
    }

    public CounterfactualConfig withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    public CounterfactualConfig withGoalThreshold(double threshold) {
        this.goalThreshold = threshold;
        return this;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }

    public CounterfactualConfig withSolverConfig(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
        return this;
    }

    public double getGoalThreshold() {
        return goalThreshold;
    }
}
