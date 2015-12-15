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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;

public class BedChangeMove extends AbstractMove {

    private BedDesignation bedDesignation;
    private Bed toBed;

    public BedChangeMove(BedDesignation bedDesignation, Bed toBed) {
        this.bedDesignation = bedDesignation;
        this.toBed = toBed;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(bedDesignation.getBed(), toBed);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new BedChangeMove(bedDesignation, bedDesignation.getBed());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector scoreDirector) {
        PatientAdmissionMoveHelper.moveBed(scoreDirector, bedDesignation, toBed);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(bedDesignation);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toBed);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BedChangeMove) {
            BedChangeMove other = (BedChangeMove) o;
            return new EqualsBuilder()
                    .append(bedDesignation, other.bedDesignation)
                    .append(toBed, other.toBed)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(bedDesignation)
                .append(toBed)
                .toHashCode();
    }

    public String toString() {
        return bedDesignation + " {" + bedDesignation.getBed() + " -> " + toBed + "}";
    }

}
