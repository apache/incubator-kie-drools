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

package org.drools.planner.benchmark.config;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.benchmark.core.PlannerBenchmarkResult;
import org.drools.planner.benchmark.core.PlanningProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.config.solver.SolverConfig;

@XStreamAlias("solverBenchmark")
public class SolverBenchmarkConfig {

    private String name = null;

    @XStreamAlias("solver")
    private SolverConfig solverConfig = null;

    @XStreamAlias("planningProblemBenchmarkList")
    private PlanningProblemBenchmarkListConfig planningProblemBenchmarkListConfig = new PlanningProblemBenchmarkListConfig();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SolverConfig getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfig solverConfig) {
        this.solverConfig = solverConfig;
    }

    public PlanningProblemBenchmarkListConfig getPlanningProblemBenchmarkListConfig() {
        return planningProblemBenchmarkListConfig;
    }

    public void setPlanningProblemBenchmarkListConfig(PlanningProblemBenchmarkListConfig planningProblemBenchmarkListConfig) {
        this.planningProblemBenchmarkListConfig = planningProblemBenchmarkListConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public SolverBenchmark buildSolverBenchmark(List<PlanningProblemBenchmark> unifiedPlanningProblemBenchmarkList) {
        SolverBenchmark solverBenchmark = new SolverBenchmark();
        solverBenchmark.setName(name);
        solverBenchmark.setSolverConfig(solverConfig);
        solverBenchmark.setPlannerBenchmarkResultList(new ArrayList<PlannerBenchmarkResult>());
        List<PlanningProblemBenchmark> planningProblemBenchmarkList = planningProblemBenchmarkListConfig
                .buildPlanningProblemBenchmarkList(unifiedPlanningProblemBenchmarkList, solverBenchmark);
        solverBenchmark.setPlanningProblemBenchmarkList(planningProblemBenchmarkList);
        return solverBenchmark;
    }

    public void inherit(SolverBenchmarkConfig inheritedConfig) {
        if (solverConfig == null) {
            solverConfig = inheritedConfig.getSolverConfig();
        } else if (inheritedConfig.getSolverConfig() != null) {
            solverConfig.inherit(inheritedConfig.getSolverConfig());
        }
        if (planningProblemBenchmarkListConfig == null) {
            planningProblemBenchmarkListConfig = inheritedConfig.getPlanningProblemBenchmarkListConfig();
        } else if (inheritedConfig.getSolverConfig() != null) {
            planningProblemBenchmarkListConfig.inherit(inheritedConfig.getPlanningProblemBenchmarkListConfig());
        }
    }

}
