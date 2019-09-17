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

package org.optaplanner.examples.machinereassignment.domain.solver;

import java.io.Serializable;
import java.util.Comparator;

import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;

public class MrProcessAssignmentDifficultyComparator implements Comparator<MrProcessAssignment>,
        Serializable {

    private static final Comparator<MrProcessAssignment> COMPARATOR =
            Comparator.comparingInt((MrProcessAssignment assignment) -> assignment.getProcess().getUsageMultiplicand())
                    .thenComparingLong(MrProcessAssignment::getId);

    @Override
    public int compare(MrProcessAssignment a, MrProcessAssignment b) {
        return COMPARATOR.compare(a, b);
    }
}
