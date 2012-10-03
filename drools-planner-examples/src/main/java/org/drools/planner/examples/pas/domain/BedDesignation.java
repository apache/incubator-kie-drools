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

package org.drools.planner.examples.pas.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.pas.domain.solver.BedDesignationDifficultyWeightFactory;
import org.drools.planner.examples.pas.domain.solver.BedStrengthComparator;

@PlanningEntity(difficultyWeightFactoryClass = BedDesignationDifficultyWeightFactory.class)
@XStreamAlias("BedDesignation")
public class BedDesignation extends AbstractPersistable {

    private AdmissionPart admissionPart;
    private Bed bed;

    public AdmissionPart getAdmissionPart() {
        return admissionPart;
    }

    public void setAdmissionPart(AdmissionPart admissionPart) {
        this.admissionPart = admissionPart;
    }

    @PlanningVariable(strengthComparatorClass = BedStrengthComparator.class)
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "bedList")
    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Patient getPatient() {
        return admissionPart.getPatient();
    }

    public Gender getPatientGender() {
        return admissionPart.getPatient().getGender();
    }

    public int getPatientAge() {
        return admissionPart.getPatient().getAge();
    }

    public Integer getPatientPreferredMaximumRoomCapacity() {
        return admissionPart.getPatient().getPreferredMaximumRoomCapacity();
    }

    public Specialism getAdmissionPartSpecialism() {
        return admissionPart.getSpecialism();
    }

    public int getAdmissionPartNightCount() {
        return admissionPart.getNightCount();
    }

    public Room getRoom() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom();
    }

    public int getRoomCapacity() {
        if (bed == null) {
            return Integer.MIN_VALUE;
        }
        return bed.getRoom().getCapacity();
    }

    public Department getDepartment() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom().getDepartment();
    }

    public GenderLimitation getRoomGenderLimitation() {
        if (bed == null) {
            return null;
        }
        return bed.getRoom().getGenderLimitation();
    }

    public BedDesignation clone() {
        BedDesignation clone = new BedDesignation();
        clone.id = id;
        clone.admissionPart = admissionPart;
        clone.bed = bed;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BedDesignation) {
            BedDesignation other = (BedDesignation) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(admissionPart, other.admissionPart)
                    .append(bed, other.bed)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(admissionPart)
                .append(bed)
                .toHashCode();
    }

    @Override
    public String toString() {
        return admissionPart + " @ " + bed;
    }

}
