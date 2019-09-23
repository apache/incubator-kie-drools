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
import java.util.Collections;
import java.util.Comparator;

import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftType;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

public class ShiftAssignmentDifficultyComparator implements Comparator<ShiftAssignment>,
        Serializable {

    private static final Comparator<Shift> COMPARATOR =
            comparing(Shift::getShiftDate, Collections.reverseOrder(comparing(ShiftDate::getDate)))
                    .thenComparing(Shift::getShiftType, comparingLong(ShiftType::getId).reversed())
                    .thenComparingInt(Shift::getRequiredEmployeeSize);

    @Override
    public int compare(ShiftAssignment a, ShiftAssignment b) {
        Shift aShift = a.getShift();
        Shift bShift = b.getShift();
        return COMPARATOR.compare(aShift, bShift);
    }
}
