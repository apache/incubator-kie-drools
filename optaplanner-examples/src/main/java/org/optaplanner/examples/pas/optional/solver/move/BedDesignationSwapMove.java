/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.pas.optional.solver.move;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.solver.move.PatientAdmissionMoveHelper;

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
    public BedDesignationSwapMove rebase(ScoreDirector<PatientAdmissionSchedule> destinationScoreDirector) {
        return new BedDesignationSwapMove(destinationScoreDirector.lookUpWorkingObject(leftBedDesignation),
                destinationScoreDirector.lookUpWorkingObject(rightBedDesignation));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftBedDesignation, rightBedDesignation);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftBedDesignation.getBed(), rightBedDesignation.getBed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BedDesignationSwapMove other = (BedDesignationSwapMove) o;
        return Objects.equals(leftBedDesignation, other.leftBedDesignation) &&
                Objects.equals(rightBedDesignation, other.rightBedDesignation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftBedDesignation, rightBedDesignation);
    }

    @Override
    public String toString() {
        return leftBedDesignation + " {" + leftBedDesignation.getBed() + "} <-> "
                + rightBedDesignation + " {" + rightBedDesignation.getBed() + "}";
    }

}
