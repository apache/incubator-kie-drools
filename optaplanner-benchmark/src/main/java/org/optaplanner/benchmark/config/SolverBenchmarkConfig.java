/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.benchmark.config;

import java.util.function.Consumer;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "name",
        "solverConfig",
        "problemBenchmarksConfig",
        "subSingleCount"
})
public class SolverBenchmarkConfig extends AbstractConfig<SolverBenchmarkConfig> {

    private String name = null;

    @XmlElement(name = SolverConfig.XML_ELEMENT_NAME, namespace = SolverConfig.XML_NAMESPACE)
    private SolverConfig solverConfig = null;

    @XmlElement(name = "problemBenchmarks")
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
    // With methods
    // ************************************************************************

    public SolverBenchmarkConfig withName(String name) {
        this.setName(name);
        return this;
    }

    public SolverBenchmarkConfig withSolverConfig(SolverConfig solverConfig) {
        this.setSolverConfig(solverConfig);
        return this;
    }

    public SolverBenchmarkConfig withProblemBenchmarksConfig(ProblemBenchmarksConfig problemBenchmarksConfig) {
        this.setProblemBenchmarksConfig(problemBenchmarksConfig);
        return this;
    }

    public SolverBenchmarkConfig withSubSingleCount(Integer subSingleCount) {
        this.setSubSingleCount(subSingleCount);
        return this;
    }

    @Override
    public SolverBenchmarkConfig inherit(SolverBenchmarkConfig inheritedConfig) {
        solverConfig = ConfigUtils.inheritConfig(solverConfig, inheritedConfig.getSolverConfig());
        problemBenchmarksConfig = ConfigUtils.inheritConfig(problemBenchmarksConfig,
                inheritedConfig.getProblemBenchmarksConfig());
        subSingleCount = ConfigUtils.inheritOverwritableProperty(subSingleCount, inheritedConfig.getSubSingleCount());
        return this;
    }

    @Override
    public SolverBenchmarkConfig copyConfig() {
        return new SolverBenchmarkConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (solverConfig != null) {
            solverConfig.visitReferencedClasses(classVisitor);
        }
        if (problemBenchmarksConfig != null) {
            problemBenchmarksConfig.visitReferencedClasses(classVisitor);
        }
    }

}
