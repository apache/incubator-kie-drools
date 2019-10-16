/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;

@RunWith(Parameterized.class)
public abstract class AbstractBenchmarkConfigTest {

    protected static Collection<Object[]> getArgOptionsAsParameters(CommonBenchmarkApp benchmarkApp) {
        List<Object[]> filesAsParameters = new ArrayList<>();
        for (CommonBenchmarkApp.ArgOption argOption : benchmarkApp.getArgOptions()) {
            filesAsParameters.add(new Object[]{argOption});
        }
        return filesAsParameters;
    }

    protected CommonBenchmarkApp.ArgOption argOption;

    protected AbstractBenchmarkConfigTest(CommonBenchmarkApp.ArgOption argOption) {
        this.argOption = argOption;
    }

    @Test
    public void buildPlannerBenchmark() {
        String benchmarkConfigResource = argOption.getBenchmarkConfigResource();
        PlannerBenchmarkFactory benchmarkFactory;
        if (!argOption.isTemplate()) {
            benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(benchmarkConfigResource);
        } else {
            benchmarkFactory = PlannerBenchmarkFactory.createFromFreemarkerXmlResource(benchmarkConfigResource);
        }
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();
        buildEverySolver(benchmark);
    }

    protected void buildEverySolver(PlannerBenchmark plannerBenchmark) {
        SolverConfigContext configContext = new SolverConfigContext();
        PlannerBenchmarkResult plannerBenchmarkResult = ((DefaultPlannerBenchmark) plannerBenchmark).getPlannerBenchmarkResult();
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            SolverConfig solverConfig = solverBenchmarkResult.getSolverConfig();
            solverConfig.buildSolver(configContext);
        }
    }

}
