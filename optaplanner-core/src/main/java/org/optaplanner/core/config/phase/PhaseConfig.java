/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.phase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.Phase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.PhaseToSolverTerminationBridge;
import org.optaplanner.core.impl.solver.termination.Termination;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;

@XmlSeeAlso({
        ConstructionHeuristicPhaseConfig.class,
        CustomPhaseConfig.class,
        ExhaustiveSearchPhaseConfig.class,
        LocalSearchPhaseConfig.class,
        NoChangePhaseConfig.class,
        PartitionedSearchPhaseConfig.class
})
@XStreamInclude({
        CustomPhaseConfig.class,
        NoChangePhaseConfig.class,
        ExhaustiveSearchPhaseConfig.class,
        ConstructionHeuristicPhaseConfig.class,
        LocalSearchPhaseConfig.class,
        PartitionedSearchPhaseConfig.class
})
public abstract class PhaseConfig<C extends PhaseConfig> extends AbstractConfig<C> {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XmlElement(name = "termination")
    @XStreamAlias("termination")
    private TerminationConfig terminationConfig = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract Phase buildPhase(int phaseIndex,
            HeuristicConfigPolicy solverConfigPolicy, BestSolutionRecaller bestSolutionRecaller, Termination solverTermination);

    protected Termination buildPhaseTermination(HeuristicConfigPolicy configPolicy, Termination solverTermination) {
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        // In case of childThread PART_THREAD, the solverTermination is actually the parent phase's phaseTermination
        // with the bridge removed, so it's ok to add it again
        Termination phaseTermination = new PhaseToSolverTerminationBridge(solverTermination);
        return terminationConfig_.buildTermination(configPolicy, phaseTermination);
    }

    @Override
    public C inherit(C inheritedConfig) {
        terminationConfig = ConfigUtils.inheritConfig(terminationConfig, inheritedConfig.getTerminationConfig());
        return (C) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
