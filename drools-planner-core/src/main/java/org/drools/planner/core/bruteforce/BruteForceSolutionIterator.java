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

package org.drools.planner.core.bruteforce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.planner.core.annotations.PlanningVariableClass;
import org.drools.planner.core.solution.Solution;

public class BruteForceSolutionIterator {

    private BruteForceSolverScope bruteForceSolverScope;

    private List<PlanningVariableHandler> list = new ArrayList<PlanningVariableHandler>();

    public BruteForceSolutionIterator(BruteForceSolverScope bruteForceSolverScope) {
        this.bruteForceSolverScope = bruteForceSolverScope; // TODO move startingSolution etc out of here
        Collection<? extends Object> facts = bruteForceSolverScope.getWorkingSolution().getFacts();
        for (Object fact : facts) {
            PlanningVariableClass planningVariableClass = fact.getClass().getAnnotation(PlanningVariableClass.class);
            if (planningVariableClass != null) {
                PlanningVariableHandler planningVariableHandler = new PlanningVariableHandler(fact, planningVariableClass, facts);
                list.add(planningVariableHandler);
            }
        }
    }

    public boolean hasNext() {
        for (PlanningVariableHandler planningVariableHandler : list) {
            if (planningVariableHandler.hasNext()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void next() {
        // Find the level to increment (for example in 115999)
        for (PlanningVariableHandler planningVariableHandler : list) {
            if (planningVariableHandler.hasNext()) {
                // Increment that level (for example 5 in 115999)
                planningVariableHandler.next(bruteForceSolverScope.getWorkingMemory());
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningVariableHandler.reset(bruteForceSolverScope.getWorkingMemory());
            }
        }
    }

}
