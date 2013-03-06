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

package org.drools.planner.config.bruteforce;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.optaplanner.core.bruteforce.BruteForceEntityWalker;
import org.optaplanner.core.bruteforce.BruteForceSolverPhase;
import org.optaplanner.core.bruteforce.DefaultBruteForceSolverPhase;
import org.optaplanner.core.domain.solution.SolutionDescriptor;
import org.optaplanner.core.score.definition.ScoreDefinition;
import org.optaplanner.core.termination.Termination;

@XStreamAlias("bruteForce")
public class BruteForceSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public BruteForceSolverPhase buildSolverPhase(int phaseIndex, EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition, Termination solverTermination) {
        DefaultBruteForceSolverPhase bruteForceSolverPhase = new DefaultBruteForceSolverPhase();
        configureSolverPhase(bruteForceSolverPhase, phaseIndex, environmentMode, scoreDefinition, solverTermination);
        bruteForceSolverPhase.setBruteForceEntityWalker(new BruteForceEntityWalker(solutionDescriptor));
        return bruteForceSolverPhase;
    }

    public void inherit(BruteForceSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
    }

}
