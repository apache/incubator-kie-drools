/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

@XStreamAlias("solverBenchmark")
public class SolverBenchmarkConfig<Solution_> extends AbstractConfig<SolverBenchmarkConfig> {

    private String name = null;

    @XStreamAlias("solver")
    private SolverConfig solverConfig = null;

    @XStreamAlias("problemBenchmarks")
    private ProblemBenchmarksConfig problemBenchmarksConfig = null;

    private Integer subSingleCount = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

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

    public Integer getSubSingleCount() {
        return subSingleCount;
    }

    public void setSubSingleCount(Integer subSingleCount) {
        this.subSingleCount = subSingleCount;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public void buildSolverBenchmark(SolverConfigContext solverConfigContext, ClassLoader classLoader,
            PlannerBenchmarkResult plannerBenchmark, Solution_[] extraProblems) {
        validate();
        SolverBenchmarkResult solverBenchmarkResult = new SolverBenchmarkResult(plannerBenchmark);
        solverBenchmarkResult.setName(name);
        solverBenchmarkResult.setSubSingleCount(ConfigUtils.inheritOverwritableProperty(subSingleCount, 1));
        if (solverConfig.getClassLoader() == null) {
            solverConfig.setClassLoader(classLoader);
        }
        solverBenchmarkResult.setSolverConfig(solverConfig);
        SolutionDescriptor<Object> solutionDescriptor = solverConfig.buildSolutionDescriptor(solverConfigContext);
        for (Solution_ extraProblem : extraProblems) {
            if (!solutionDescriptor.getSolutionClass().isInstance(extraProblem)) {
                throw new IllegalArgumentException("The solverBenchmark name (" + name
                        + ") for solution class (" + solutionDescriptor.getSolutionClass()
                        + ") cannot solve a problem (" + extraProblem
                        + ") of class (" + (extraProblem == null ? null : extraProblem.getClass()) + ").");
            }
        }
        solverBenchmarkResult.setScoreDefinition(
                solutionDescriptor.getScoreDefinition());
        solverBenchmarkResult.setSingleBenchmarkResultList(new ArrayList<>());
        ProblemBenchmarksConfig problemBenchmarksConfig_
                = problemBenchmarksConfig == null ? new ProblemBenchmarksConfig()
                : problemBenchmarksConfig;
        plannerBenchmark.getSolverBenchmarkResultList().add(solverBenchmarkResult);
        problemBenchmarksConfig_.buildProblemBenchmarkList(solverConfigContext, solverBenchmarkResult, extraProblems);
    }

    protected void validate() {
        if (!PlannerBenchmarkConfig.VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalStateException("The solverBenchmark name (" + name
                    + ") is invalid because it does not follow the nameRegex ("
                    + PlannerBenchmarkConfig.VALID_NAME_PATTERN.pattern() + ")" +
                    " which might cause an illegal filename.");
        }
        if (!name.trim().equals(name)) {
            throw new IllegalStateException("The solverBenchmark name (" + name
                    + ") is invalid because it starts or ends with whitespace.");
        }
        if (subSingleCount != null && subSingleCount < 1) {
            throw new IllegalStateException("The solverBenchmark name (" + name
                    + ") is invalid because the subSingleCount (" + subSingleCount + ") must be greater than 1.");
        }
    }

    @Override
    public void inherit(SolverBenchmarkConfig inheritedConfig) {
        solverConfig = ConfigUtils.inheritConfig(solverConfig, inheritedConfig.getSolverConfig());
        problemBenchmarksConfig = ConfigUtils.inheritConfig(problemBenchmarksConfig,
                inheritedConfig.getProblemBenchmarksConfig());
        subSingleCount = ConfigUtils.inheritOverwritableProperty(subSingleCount, inheritedConfig.getSubSingleCount());
    }

}
