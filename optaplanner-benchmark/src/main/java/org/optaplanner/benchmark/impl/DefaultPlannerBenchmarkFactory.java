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

package org.optaplanner.benchmark.impl;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.SolverConfigContext;

/**
 * @see PlannerBenchmarkFactory
 */
public class DefaultPlannerBenchmarkFactory extends PlannerBenchmarkFactory {

    protected final PlannerBenchmarkConfig plannerBenchmarkConfig;
    protected final SolverConfigContext solverConfigContext;

    public DefaultPlannerBenchmarkFactory(PlannerBenchmarkConfig plannerBenchmarkConfig) {
        this(plannerBenchmarkConfig, new SolverConfigContext());
    }

    public DefaultPlannerBenchmarkFactory(PlannerBenchmarkConfig plannerBenchmarkConfig, SolverConfigContext solverConfigContext) {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") cannot be null.");
        }
        this.plannerBenchmarkConfig = plannerBenchmarkConfig;
        this.solverConfigContext = solverConfigContext;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public PlannerBenchmark buildPlannerBenchmark() {
        return plannerBenchmarkConfig.buildPlannerBenchmark(solverConfigContext);
    }

    @Override
    @SafeVarargs
    public final <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems) {
        return plannerBenchmarkConfig.buildPlannerBenchmark(solverConfigContext, problems);
    }

    @Override
    @Deprecated
    public PlannerBenchmarkConfig getPlannerBenchmarkConfig() {
        return plannerBenchmarkConfig;
    }

    public SolverConfigContext getSolverConfigContext() {
        return solverConfigContext;
    }

}
