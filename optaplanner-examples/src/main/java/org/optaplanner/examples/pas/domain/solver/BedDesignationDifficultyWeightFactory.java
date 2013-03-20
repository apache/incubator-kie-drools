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

package org.optaplanner.examples.pas.domain.solver;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.Room;

public class BedDesignationDifficultyWeightFactory
        implements SelectionSorterWeightFactory<PatientAdmissionSchedule, BedDesignation> {

    public Comparable createSorterWeight(PatientAdmissionSchedule schedule, BedDesignation bedDesignation) {
        int disallowedCount = 0;
        for (Room room : schedule.getRoomList()) {
            disallowedCount += (room.countDisallowedAdmissionPart(bedDesignation.getAdmissionPart())
                    * room.getCapacity());
        }
        return new BedDesignationDifficultyWeight(bedDesignation, disallowedCount);
    }

    public static class BedDesignationDifficultyWeight implements Comparable<BedDesignationDifficultyWeight> {

        private final BedDesignation bedDesignation;
        private int nightCount;
        private int disallowedCount;

        public BedDesignationDifficultyWeight(BedDesignation bedDesignation, int disallowedCount) {
            this.bedDesignation = bedDesignation;
            this.nightCount = bedDesignation.getAdmissionPart().getNightCount();
            this.disallowedCount = disallowedCount;
        }

        public int compareTo(BedDesignationDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(nightCount, other.nightCount)
                    .append(disallowedCount, other.disallowedCount)
                    .append(bedDesignation.getId(), other.bedDesignation.getId())
                    .toComparison();
        }

    }

}
