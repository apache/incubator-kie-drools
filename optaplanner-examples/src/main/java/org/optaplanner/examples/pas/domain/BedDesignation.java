/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.pas.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.pas.domain.solver.BedDesignationDifficultyWeightFactory;
import org.optaplanner.examples.pas.domain.solver.BedStrengthComparator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@PlanningEntity(difficultyWeightFactoryClass = BedDesignationDifficultyWeightFactory.class)
@JsonIdentityInfo(scope = BedDesignation.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BedDesignation extends AbstractPersistable {

    private AdmissionPart admissionPart;
    private Bed bed;

    public BedDesignation() {
    }

    public BedDesignation(long id, AdmissionPart admissionPart) {
        super(id);
        this.admissionPart = admissionPart;
    }

    public BedDesignation(long id, AdmissionPart admissionPart, Bed bed) {
        this(id, admissionPart);
        this.bed = bed;
    }

    public AdmissionPart getAdmissionPart() {
        return admissionPart;
    }

    public void setAdmissionPart(AdmissionPart admissionPart) {
        this.admissionPart = admissionPart;
    }

    @PlanningVariable(nullable = true, strengthComparatorClass = BedStrengthComparator.class)
    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @JsonIgnore
    public Patient getPatient() {
        return admissionPart.getPatient();
    }

    @JsonIgnore
    public Gender getPatientGender() {
        return admissionPart.getPatient().getGender();
    }

    @JsonIgnore
    public int getPatientAge() {
        return admissionPart.getPatient().getAge();
    }

    @JsonIgnore
    public Integer getPatientPreferredMaximumRoomCapacity() {
        return admissionPart.getPatient().getPreferredMaximumRoomCapacity();
    }

    @JsonIgnore
    public Specialism getAdmissionPartSpecialism() {
        return admissionPart.getSpecialism();
    }

    @JsonIgnore
    public int getFirstNightIndex() {
        return admissionPart.getFirstNight().getIndex();
    }

    @JsonIgnore
    public int getLastNightIndex() {
        return admissionPart.getLastNight().getIndex();
    }

    @JsonIgnore
    public int getAdmissionPartNightCount() {
        return admissionPart.getNightCount();
    }

    @JsonIgnore
    public Room getRoom() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom();
    }

    @JsonIgnore
    public int getRoomCapacity() {
        if (bed == null) {
            return Integer.MIN_VALUE;
        }
        return bed.getRoom().getCapacity();
    }

    @JsonIgnore
    public Department getDepartment() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom().getDepartment();
    }

    @JsonIgnore
    public GenderLimitation getRoomGenderLimitation() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom().getGenderLimitation();
    }

    @Override
    public String toString() {
        return admissionPart.toString();
    }

}
