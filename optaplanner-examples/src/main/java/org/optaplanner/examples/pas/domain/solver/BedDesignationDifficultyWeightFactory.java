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

package org.optaplanner.examples.pas.domain.solver;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.domain.Room;

public class BedDesignationDifficultyWeightFactory
        implements SelectionSorterWeightFactory<PatientAdmissionSchedule, BedDesignation> {

    public Comparable createSorterWeight(PatientAdmissionSchedule schedule, BedDesignation bedDesignation) {
        int hardDisallowedCount = 0;
        int softDisallowedCount = 0;
        for (Room room : schedule.getRoomList()) {
            hardDisallowedCount += (room.countHardDisallowedAdmissionPart(bedDesignation.getAdmissionPart())
                    * room.getCapacity());
            softDisallowedCount += (room.countSoftDisallowedAdmissionPart(bedDesignation.getAdmissionPart())
                    * room.getCapacity());
        }
        return new BedDesignationDifficultyWeight(bedDesignation, hardDisallowedCount, softDisallowedCount);
    }

    public static class BedDesignationDifficultyWeight implements Comparable<BedDesignationDifficultyWeight> {

        private final BedDesignation bedDesignation;
        private int requiredEquipmentCount;
        private int nightCount;
        private int hardDisallowedCount;
        private int softDisallowedCount;

        public BedDesignationDifficultyWeight(BedDesignation bedDesignation,
                int hardDisallowedCount, int softDisallowedCount) {
            this.bedDesignation = bedDesignation;
            requiredEquipmentCount = bedDesignation.getPatient().getRequiredPatientEquipmentList().size();
            this.nightCount = bedDesignation.getAdmissionPart().getNightCount();
            this.hardDisallowedCount = hardDisallowedCount;
            this.softDisallowedCount = softDisallowedCount;
        }

        public int compareTo(BedDesignationDifficultyWeight other) {
            return new CompareToBuilder()
                    .append(requiredEquipmentCount * nightCount,
                            other.requiredEquipmentCount * other.nightCount)
                    .append(hardDisallowedCount * nightCount, other.hardDisallowedCount * other.nightCount)
                    .append(nightCount, other.nightCount)
                    .append(softDisallowedCount * nightCount, other.softDisallowedCount * nightCount)
                    // Descending (earlier nights are more difficult) // TODO probably because less occupancy
                    .append(other.bedDesignation.getAdmissionPart().getFirstNight().getIndex(),
                            bedDesignation.getAdmissionPart().getFirstNight().getIndex())
                    .append(bedDesignation.getId(), other.bedDesignation.getId())
                    .toComparison();
        }

    }

}
