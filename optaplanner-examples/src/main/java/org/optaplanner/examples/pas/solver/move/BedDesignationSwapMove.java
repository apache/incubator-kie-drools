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

package org.optaplanner.examples.pas.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

public class BedDesignationSwapMove extends AbstractMove<PatientAdmissionSchedule> {

    private BedDesignation leftBedDesignation;
    private BedDesignation rightBedDesignation;

    public BedDesignationSwapMove(BedDesignation leftBedDesignation, BedDesignation rightBedDesignation) {
        this.leftBedDesignation = leftBedDesignation;
        this.rightBedDesignation = rightBedDesignation;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        return !Objects.equals(leftBedDesignation.getBed(), rightBedDesignation.getBed());
    }

    @Override
    public BedDesignationSwapMove createUndoMove(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        return new BedDesignationSwapMove(rightBedDesignation, leftBedDesignation);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<PatientAdmissionSchedule> scoreDirector) {
        Bed oldLeftBed = leftBedDesignation.getBed();
        Bed oldRightBed = rightBedDesignation.getBed();
        PatientAdmissionMoveHelper.moveBed(scoreDirector, leftBedDesignation, oldRightBed);
        PatientAdmissionMoveHelper.moveBed(scoreDirector, rightBedDesignation, oldLeftBed);
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.<BedDesignation>asList(leftBedDesignation, rightBedDesignation);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.<Bed>asList(leftBedDesignation.getBed(), rightBedDesignation.getBed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BedDesignationSwapMove) {
            BedDesignationSwapMove other = (BedDesignationSwapMove) o;
            return new EqualsBuilder()
                    .append(leftBedDesignation, other.leftBedDesignation)
                    .append(rightBedDesignation, other.rightBedDesignation)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftBedDesignation)
                .append(rightBedDesignation)
                .toHashCode();
    }

    @Override
    public String toString() {
        return leftBedDesignation + " {" + leftBedDesignation.getBed() + "} <-> "
                + rightBedDesignation + " {" + rightBedDesignation.getBed() + "}";
    }

}
