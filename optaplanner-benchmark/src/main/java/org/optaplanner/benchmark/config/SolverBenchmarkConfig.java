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
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.solver.SolverConfig;

@XStreamAlias("solverBenchmark")
public class SolverBenchmarkConfig {

    private String name = null;

    private Integer subSingleCount = null;

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

    public Integer getSubSingleCount() {
        return subSingleCount;
    }

    public void setSubSingleCount(Integer subSingleCount) {
        this.subSingleCount = subSingleCount;
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

    public void buildSolverBenchmark(PlannerBenchmarkResult plannerBenchmark) {
        validate();
        SolverBenchmarkResult solverBenchmarkResult = new SolverBenchmarkResult(plannerBenchmark);
        solverBenchmarkResult.setName(name);
        solverBenchmarkResult.setSubSingleCount(subSingleCount);
        solverBenchmarkResult.setSolverConfig(solverConfig);
        solverBenchmarkResult.setSingleBenchmarkResultList(new ArrayList<SingleBenchmarkResult>());
        ProblemBenchmarksConfig problemBenchmarksConfig_
                = problemBenchmarksConfig == null ? new ProblemBenchmarksConfig()
                : problemBenchmarksConfig;
        plannerBenchmark.getSolverBenchmarkResultList().add(solverBenchmarkResult);
        problemBenchmarksConfig_.buildProblemBenchmarkList(solverBenchmarkResult);
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
        if (subSingleCount == null || subSingleCount < 1) {
            throw new IllegalStateException("The solverBenchmark name (" + name
                    + ") is invalid because the subSingleCount (" + subSingleCount + ") must be greater than 1.");
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
        if (subSingleCount == null) {
            subSingleCount = ObjectUtils.defaultIfNull(inheritedConfig.getSubSingleCount(), 1);
        }
    }

}
