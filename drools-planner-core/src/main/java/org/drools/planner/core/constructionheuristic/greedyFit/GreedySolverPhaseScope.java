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

package org.drools.planner.core.constructionheuristic.greedyFit;

import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.DefaultSolverScope;

public class GreedySolverPhaseScope extends AbstractSolverPhaseScope {

    private GreedyStepScope lastCompletedGreedyStepScope;

    public GreedySolverPhaseScope(DefaultSolverScope solverScope) {
        super(solverScope);
    }

    public AbstractStepScope getLastCompletedStepScope() {
        return lastCompletedGreedyStepScope;
    }

    public GreedyStepScope getLastCompletedGreedyStepScope() {
        return lastCompletedGreedyStepScope;
    }

    public void setLastCompletedGreedyStepScope(GreedyStepScope lastCompletedGreedyStepScope) {
        this.lastCompletedGreedyStepScope = lastCompletedGreedyStepScope;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

}
