/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;

public class ShiftAssignmentDifficultyComparator implements Comparator<ShiftAssignment>, Serializable {

    @Override
    public int compare(ShiftAssignment a, ShiftAssignment b) {
        Shift aShift = a.getShift();
        Shift bShift = b.getShift();
        return new CompareToBuilder()
                // At least for Construction Heuristics, scheduling the shifts by starting time
                // is better than by employee size
                .append(bShift.getShiftDate(), aShift.getShiftDate()) // Descending
                .append(bShift.getShiftType(), aShift.getShiftType()) // Descending
                .append(aShift.getRequiredEmployeeSize(), bShift.getRequiredEmployeeSize())
                .toComparison();
    }

}
