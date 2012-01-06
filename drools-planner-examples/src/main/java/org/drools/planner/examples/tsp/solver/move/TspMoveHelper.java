/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.solver.move;

import org.drools.WorkingMemory;
import org.drools.planner.examples.tsp.domain.Journey;
import org.drools.FactHandle;

public class TspMoveHelper {

    public static void moveJourneyAfterJourney(WorkingMemory workingMemory,
            Journey journey, Journey toNextJourney) {
        FactHandle journeyFactHandle = workingMemory.getFactHandle(journey);
        FactHandle toNextJourneyFactHandle = workingMemory.getFactHandle(toNextJourney);

        journey.setNextJourney(toNextJourney);
        toNextJourney.setPreviousJourney(journey);

        workingMemory.update(journeyFactHandle, journey);
        // Note: for the score rules this isn't currently needed (and a performance leak)
        // but removing it would not be clean code.
        workingMemory.update(toNextJourneyFactHandle, toNextJourney);
    }

    private TspMoveHelper() {
    }

}
