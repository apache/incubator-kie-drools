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

package org.drools.planner.examples.pas.domain.solver;

import java.io.Serializable;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Room;

/**
 * Calculated during initialization, not modified during score calculation.
 */
@Deprecated
public class AdmissionPartSpecialismMissingInRoom implements Serializable {

    private AdmissionPart admissionPart;
    private Room room;
    private int weight;

    public AdmissionPartSpecialismMissingInRoom(AdmissionPart admissionPart, Room room, int weight) {
        this.admissionPart = admissionPart;
        this.room = room;
        this.weight = weight;
    }

    public AdmissionPart getAdmissionPart() {
        return admissionPart;
    }

    public void setAdmissionPart(AdmissionPart admissionPart) {
        this.admissionPart = admissionPart;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int compareTo(AdmissionPartSpecialismMissingInRoom other) {
        return new CompareToBuilder()
                .append(admissionPart, other.admissionPart)
                .append(room, other.room)
                .toComparison();
    }

    @Override
    public String toString() {
        return admissionPart + " & " + room + " = " + weight;
    }

}
