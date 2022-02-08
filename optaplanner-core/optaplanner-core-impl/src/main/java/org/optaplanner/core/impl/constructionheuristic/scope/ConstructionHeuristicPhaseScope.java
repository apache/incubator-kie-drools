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

package org.optaplanner.core.impl.constructionheuristic.scope;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.SolverScope;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstructionHeuristicPhaseScope<Solution_> extends AbstractPhaseScope<Solution_> {

    private ConstructionHeuristicStepScope<Solution_> lastCompletedStepScope;

    public ConstructionHeuristicPhaseScope(SolverScope<Solution_> solverScope) {
        super(solverScope);
        lastCompletedStepScope = new ConstructionHeuristicStepScope<>(this, -1);
    }

    @Override
    public ConstructionHeuristicStepScope<Solution_> getLastCompletedStepScope() {
        return lastCompletedStepScope;
    }

    public void setLastCompletedStepScope(ConstructionHeuristicStepScope<Solution_> lastCompletedStepScope) {
        this.lastCompletedStepScope = lastCompletedStepScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
