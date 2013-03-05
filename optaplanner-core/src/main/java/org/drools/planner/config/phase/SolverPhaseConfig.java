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

package org.drools.planner.config.phase;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.bruteforce.BruteForceSolverPhaseConfig;
import org.drools.planner.config.constructionheuristic.ConstructionHeuristicSolverPhaseConfig;
import org.drools.planner.config.localsearch.LocalSearchSolverPhaseConfig;
import org.drools.planner.config.phase.custom.CustomSolverPhaseConfig;
import org.drools.planner.config.termination.TerminationConfig;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.phase.AbstractSolverPhase;
import org.drools.planner.core.phase.SolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.PhaseToSolverTerminationBridge;
import org.drools.planner.core.termination.Termination;

@XStreamInclude({
        CustomSolverPhaseConfig.class,
        BruteForceSolverPhaseConfig.class,
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
            EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            ScoreDefinition scoreDefinition, Termination solverTermination);

    protected void configureSolverPhase(AbstractSolverPhase solverPhase, int phaseIndex,
            EnvironmentMode environmentMode, ScoreDefinition scoreDefinition, Termination solverTermination) {
        solverPhase.setPhaseIndex(phaseIndex);
        TerminationConfig terminationConfig_ = terminationConfig == null ? new TerminationConfig()
                : terminationConfig;
        solverPhase.setTermination(terminationConfig_.buildTermination(scoreDefinition,
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
