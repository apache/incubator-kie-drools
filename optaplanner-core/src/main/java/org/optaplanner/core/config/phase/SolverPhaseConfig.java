/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.phase;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicSolverPhaseConfig;
import org.optaplanner.core.config.exhaustivesearch.ExhaustiveSearchSolverPhaseConfig;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.localsearch.LocalSearchSolverPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomSolverPhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.phase.AbstractSolverPhase;
import org.optaplanner.core.impl.phase.SolverPhase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.PhaseToSolverTerminationBridge;
import org.optaplanner.core.impl.solver.termination.Termination;

@XStreamInclude({
        CustomSolverPhaseConfig.class,
        ExhaustiveSearchSolverPhaseConfig.class,
        ConstructionHeuristicSolverPhaseConfig.class,
        LocalSearchSolverPhaseConfig.class
})
public abstract class SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamAlias("termination")
    private TerminationConfig terminationConfig = null;

    public TerminationConfig getTerminationConfig() {
        return terminationConfig;
    }

    public void setTerminationConfig(TerminationConfig terminationConfig) {
        this.terminationConfig = terminationConfig;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public abstract SolverPhase buildSolverPhase(int phaseIndex,
            HeuristicConfigPolicy solverConfigPolicy, BestSolutionRecaller bestSolutionRecaller, Termination solverTermination);

    protected void configureSolverPhase(AbstractSolverPhase solverPhase, int phaseIndex,
            HeuristicConfigPolicy configPolicy, BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        solverPhase.setPhaseIndex(phaseIndex);
        solverPhase.setBestSolutionRecaller(bestSolutionRecaller);
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        solverPhase.setTermination(terminationConfig_.buildTermination(configPolicy,
                new PhaseToSolverTerminationBridge(solverTermination)));
    }

    public void inherit(SolverPhaseConfig inheritedConfig) {
        if (terminationConfig == null) {
            terminationConfig = inheritedConfig.getTerminationConfig();
        } else if (inheritedConfig.getTerminationConfig() != null) {
            terminationConfig.inherit(inheritedConfig.getTerminationConfig());
        }
    }

}
