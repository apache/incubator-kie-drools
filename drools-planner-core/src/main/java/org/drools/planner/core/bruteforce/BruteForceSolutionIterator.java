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
import java.util.List;

public class BruteForceSolutionIterator {

    private List<BruteForcePlanningEntityIterator> planningEntityIteratorList;

    public void phaseStarted(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        planningEntityIteratorList = new ArrayList<BruteForcePlanningEntityIterator>();
        for (Object planningEntity : bruteForceSolverPhaseScope.getWorkingPlanningEntityList()) {
            BruteForcePlanningEntityIterator planningEntityIterator = new BruteForcePlanningEntityIterator(
                    bruteForceSolverPhaseScope, planningEntity);
            planningEntityIteratorList.add(planningEntityIterator);
        }
    }

    public boolean hasNext() {
        for (BruteForcePlanningEntityIterator planningEntityIterator : planningEntityIteratorList) {
            if (planningEntityIterator.hasNext()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void next() {
        // Find the level to increment (for example in 115999)
        for (BruteForcePlanningEntityIterator planningEntityIterator : planningEntityIteratorList) {
            if (planningEntityIterator.hasNext()) {
                // Increment that level (for example 5 in 115999)
                planningEntityIterator.next();
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningEntityIterator.reset();
            }
        }
    }

    public void phaseEnded(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        planningEntityIteratorList = null;
    }

}
