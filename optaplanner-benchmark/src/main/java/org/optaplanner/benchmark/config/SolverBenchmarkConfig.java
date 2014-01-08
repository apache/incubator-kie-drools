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

package org.optaplanner.benchmark.config;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.optaplanner.core.config.solver.SolverConfig;

@XStreamAlias("solverBenchmark")
public class SolverBenchmarkConfig {

    private String name = null;

    @XStreamAlias("solver")
    private SolverConfig solverConfig = null;

    @XStreamAlias("problemBenchmarks")
    private ProblemBenchmarksConfig problemBenchmarksConfig = null;

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

    public ProblemBenchmarksConfig getProblemBenchmarksConfig() {
        return problemBenchmarksConfig;
    }

    public void setProblemBenchmarksConfig(ProblemBenchmarksConfig problemBenchmarksConfig) {
        this.problemBenchmarksConfig = problemBenchmarksConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public SolverBenchmark buildSolverBenchmark(DefaultPlannerBenchmark plannerBenchmark) {
        validate();
        SolverBenchmark solverBenchmark = new SolverBenchmark(plannerBenchmark);
        solverBenchmark.setName(name);
        solverBenchmark.setSolverConfig(solverConfig);
        solverBenchmark.setSingleBenchmarkList(new ArrayList<SingleBenchmark>());
        ProblemBenchmarksConfig problemBenchmarksConfig_
                = problemBenchmarksConfig == null ? new ProblemBenchmarksConfig()
                : problemBenchmarksConfig;
        List<ProblemBenchmark> problemBenchmarkList
                = problemBenchmarksConfig_.buildProblemBenchmarkList(plannerBenchmark, solverBenchmark);
        solverBenchmark.setProblemBenchmarkList(problemBenchmarkList);
        return solverBenchmark;
    }

    private void validate() {
        final String nameRegex = "^[\\w\\d _\\-\\.\\(\\)]+$";
        if (!name.matches(nameRegex)) {
            throw new IllegalStateException("The solverBenchmark name (" + name
                    + ") is invalid because it does not follow the nameRegex (" + nameRegex + ")" +
                    " which might cause an illegal filename.");
        }
    }

    public void inherit(SolverBenchmarkConfig inheritedConfig) {
        if (solverConfig == null) {
            solverConfig = inheritedConfig.getSolverConfig();
        } else if (inheritedConfig.getSolverConfig() != null) {
            solverConfig.inherit(inheritedConfig.getSolverConfig());
        }
        if (problemBenchmarksConfig == null) {
            problemBenchmarksConfig = inheritedConfig.getProblemBenchmarksConfig();
        } else if (inheritedConfig.getProblemBenchmarksConfig() != null) {
            problemBenchmarksConfig.inherit(inheritedConfig.getProblemBenchmarksConfig());
        }
    }

}
