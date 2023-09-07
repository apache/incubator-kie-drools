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

package org.optaplanner.core.impl.phase;

import java.util.Objects;

import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.solver.termination.PhaseToSolverTerminationBridge;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.termination.TerminationFactory;

public abstract class AbstractPhaseFactory<Solution_, PhaseConfig_ extends PhaseConfig<PhaseConfig_>>
        implements PhaseFactory<Solution_> {

    protected final PhaseConfig_ phaseConfig;

    public AbstractPhaseFactory(PhaseConfig_ phaseConfig) {
        this.phaseConfig = phaseConfig;
    }

    protected Termination<Solution_> buildPhaseTermination(HeuristicConfigPolicy<Solution_> configPolicy,
            Termination<Solution_> solverTermination) {
        TerminationConfig terminationConfig_ =
                Objects.requireNonNullElseGet(phaseConfig.getTerminationConfig(), TerminationConfig::new);
        // In case of childThread PART_THREAD, the solverTermination is actually the parent phase's phaseTermination
        // with the bridge removed, so it's ok to add it again
        Termination<Solution_> phaseTermination = new PhaseToSolverTerminationBridge<>(solverTermination);
        return TerminationFactory.<Solution_> create(terminationConfig_).buildTermination(configPolicy, phaseTermination);
    }
}
