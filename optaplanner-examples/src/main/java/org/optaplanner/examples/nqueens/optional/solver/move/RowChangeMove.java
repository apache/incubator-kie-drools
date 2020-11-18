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

package org.optaplanner.examples.nqueens.optional.solver.move;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;

public class RowChangeMove extends AbstractMove<NQueens> {

    private Queen queen;
    private Row toRow;

    public RowChangeMove(Queen queen, Row toRow) {
        this.queen = queen;
        this.toRow = toRow;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<NQueens> scoreDirector) {
        return !Objects.equals(queen.getRow(), toRow);
    }

    @Override
    public RowChangeMove createUndoMove(ScoreDirector<NQueens> scoreDirector) {
        return new RowChangeMove(queen, queen.getRow());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<NQueens> scoreDirector) {
        scoreDirector.beforeVariableChanged(queen, "row"); // before changes are made
        queen.setRow(toRow);
        scoreDirector.afterVariableChanged(queen, "row"); // after changes are made
    }

    @Override
    public RowChangeMove rebase(ScoreDirector<NQueens> destinationScoreDirector) {
        return new RowChangeMove(destinationScoreDirector.lookUpWorkingObject(queen),
                destinationScoreDirector.lookUpWorkingObject(toRow));
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(queen);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toRow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RowChangeMove other = (RowChangeMove) o;
        return Objects.equals(queen, other.queen) &&
                Objects.equals(toRow, other.toRow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queen, toRow);
    }

    @Override
    public String toString() {
        return queen + " {" + queen.getRow() + " -> " + toRow + "}";
    }

}
