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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.Termination;

/**
 * A {@link NoChangePhase} is a {@link Phase} which does nothing.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see Phase
 * @see AbstractPhase
 */
public class NoChangePhase<Solution_> extends AbstractPhase<Solution_> {

    private NoChangePhase(Builder<Solution_> builder) {
        super(builder);
    }

    @Override
    public String getPhaseTypeString() {
        return "No Change";
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solve(SolverScope<Solution_> solverScope) {
        logger.info("{}No Change phase ({}) ended.",
                logIndentation,
                phaseIndex);
    }

    public static class Builder<Solution_> extends AbstractPhase.Builder<Solution_> {

        public Builder(int phaseIndex, String logIndentation, Termination<Solution_> phaseTermination) {
            super(phaseIndex, logIndentation, phaseTermination);
        }

        @Override
        public NoChangePhase<Solution_> build() {
            return new NoChangePhase<>(this);
        }
    }
}
