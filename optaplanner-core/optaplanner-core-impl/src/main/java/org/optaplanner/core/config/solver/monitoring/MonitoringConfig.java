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

package org.optaplanner.core.config.solver.monitoring;

import java.util.List;
import java.util.function.Consumer;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XmlType(propOrder = {
        "solverMetricList",
})
public class MonitoringConfig extends AbstractConfig<MonitoringConfig> {
    @XmlElement(name = "metric")
    protected List<SolverMetric> solverMetricList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************
    public List<SolverMetric> getSolverMetricList() {
        return solverMetricList;
    }

    public void setSolverMetricList(List<SolverMetric> solverMetricList) {
        this.solverMetricList = solverMetricList;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public MonitoringConfig withSolverMetricList(List<SolverMetric> solverMetricList) {
        this.solverMetricList = solverMetricList;
        return this;
    }

    @Override
    public MonitoringConfig inherit(MonitoringConfig inheritedConfig) {
        solverMetricList = ConfigUtils.inheritMergeableListProperty(solverMetricList, inheritedConfig.solverMetricList);
        return this;
    }

    @Override
    public MonitoringConfig copyConfig() {
        return new MonitoringConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        // No referenced classes currently
        // If we add custom metrics here, then this should
        // register the custom metrics
    }
}
