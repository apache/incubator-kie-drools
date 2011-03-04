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

package org.drools.planner.examples.nurserostering.solver.move;

import org.drools.WorkingMemory;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.runtime.rule.FactHandle;

public class NurseRosteringMoveHelper {

    public static void moveEmployee(WorkingMemory workingMemory, Assignment assignment, Employee toEmployee) {
        FactHandle factHandle = workingMemory.getFactHandle(assignment);
        assignment.setEmployee(toEmployee);
        workingMemory.update(factHandle, assignment);
    }

    private NurseRosteringMoveHelper() {
    }

}
