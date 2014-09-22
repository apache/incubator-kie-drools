/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.solver.event;

import java.util.Iterator;

import org.drools.core.event.AbstractEventSupport;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.solver.DefaultSolver;

/**
 * Internal API.
 */
public class SolverEventSupport extends AbstractEventSupport<SolverEventListener> {

    private DefaultSolver solver;

    public SolverEventSupport(DefaultSolver solver) {
        this.solver = solver;
    }

    public void fireBestSolutionChanged(Solution newBestSolution, int newUninitializedVariableCount) {
        final Iterator<SolverEventListener> it = getEventListenersIterator();
        if (it.hasNext()) {
            final BestSolutionChangedEvent event = new BestSolutionChangedEvent(solver,
                    solver.getSolverScope().calculateTimeMillisSpent(), newBestSolution, newUninitializedVariableCount);
            do {
                it.next().bestSolutionChanged(event);
            } while (it.hasNext());
        }
    }

}
