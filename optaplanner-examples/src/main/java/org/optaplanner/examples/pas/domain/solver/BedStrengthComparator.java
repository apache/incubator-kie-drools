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

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.Department;
import org.optaplanner.examples.pas.domain.Room;

public class BedStrengthComparator implements Comparator<Bed>, Serializable {

    public int compare(Bed a, Bed b) {
        if (a == null) {
            if (b == null) {
                return 0;
            }
            return -1;
        } else if (b == null) {
            return 1;
        }
        Room aRoom = a.getRoom();
        Department aDepartment = aRoom.getDepartment();
        Room bRoom = b.getRoom();
        Department bDepartment = bRoom.getDepartment();
        return new CompareToBuilder()
                // null minimumAge is stronger
                .append(aDepartment.getMinimumAge() == null, bDepartment.getMinimumAge() == null)
                // null maximumAge is stronger
                .append(aDepartment.getMaximumAge() == null, bDepartment.getMaximumAge() == null)
                // Descending, low minimumAge is stronger
                .append(bDepartment.getMinimumAge(), aDepartment.getMinimumAge())
                // High maximumAge is stronger
                .append(aDepartment.getMaximumAge(), bDepartment.getMaximumAge())
                .append(aRoom.getRoomEquipmentList().size(), bRoom.getRoomEquipmentList().size())
                .append(aRoom.getRoomSpecialismList().size(), bRoom.getRoomSpecialismList().size())
                .append(bRoom.getCapacity(), aRoom.getCapacity()) // Descending (smaller rooms are stronger)
                .append(a.getId(), b.getId())
                .toComparison();
    }

}
