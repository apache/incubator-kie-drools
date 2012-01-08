/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.examples.tsp.solver.variable;

import org.drools.WorkingMemory;
import org.drools.planner.api.domain.variable.event.PlanningVariableListener;
import org.drools.planner.core.solution.director.SolutionDirector;
import org.drools.planner.examples.tsp.domain.Journey;

public class PreviousJourneyListener implements PlanningVariableListener {

    public void afterChange(SolutionDirector solutionDirector, Object planningEntity, String variableName,
            Object oldValue, Object newValue) {
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
        Journey changingJourney = (Journey) planningEntity;
        Journey oldPreviousJourney = (Journey) oldValue;
        Journey newPreviousJourney = (Journey) newValue;

        // Close the old chain
        Journey oldNextJourney = changingJourney.getNextJourney();
        oldPreviousJourney.setNextJourney(oldNextJourney);
        workingMemory.update(workingMemory.getFactHandle(oldPreviousJourney), oldPreviousJourney);
        oldNextJourney.setPreviousJourney(oldPreviousJourney);
        workingMemory.update(workingMemory.getFactHandle(oldNextJourney), oldNextJourney);

        // Open the new chain
        Journey newNextJourney = newPreviousJourney.getNextJourney();
        newNextJourney.setPreviousJourney(changingJourney);
        workingMemory.update(workingMemory.getFactHandle(newNextJourney), newNextJourney);
        changingJourney.setNextJourney(newNextJourney);
        workingMemory.update(workingMemory.getFactHandle(changingJourney), changingJourney);
        newPreviousJourney.setNextJourney(changingJourney);
        workingMemory.update(workingMemory.getFactHandle(newPreviousJourney), newPreviousJourney);
        // changingJourney.setPreviousJourney(newPreviousJourney) is already done by Planner
    }

}
