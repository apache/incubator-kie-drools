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

import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.NoChangePhase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("noChangePhase")
public class NoChangePhaseConfig extends PhaseConfig<NoChangePhaseConfig> {

    public static final String XML_ELEMENT_NAME = "noChangePhase";

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public NoChangePhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        return new NoChangePhase(
                phaseIndex, solverConfigPolicy.getLogIndentation(), bestSolutionRecaller,
                buildPhaseTermination(phaseConfigPolicy, solverTermination));
    }

    @Override
    public NoChangePhaseConfig inherit(NoChangePhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        return this;
    }

    @Override
    public NoChangePhaseConfig copyConfig() {
        return new NoChangePhaseConfig().inherit(this);
    }

}
