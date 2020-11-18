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

package org.optaplanner.examples.nqueens.optional.score;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;

public class NQueensBasicIncrementalScoreCalculator implements IncrementalScoreCalculator<NQueens, SimpleScore> {

    private List<Queen> insertedQueenList;
    private int score;

    @Override
    public void resetWorkingSolution(NQueens nQueens) {
        insertedQueenList = new ArrayList<>(nQueens.getN());
        score = 0;
        for (Queen queen : nQueens.getQueenList()) {
            insert(queen);
        }
    }

    @Override
    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(Object entity) {
        insert((Queen) entity);
    }

    @Override
    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Queen) entity);
    }

    @Override
    public void afterVariableChanged(Object entity, String variableName) {
        insert((Queen) entity);
    }

    @Override
    public void beforeEntityRemoved(Object entity) {
        retract((Queen) entity);
    }

    @Override
    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Queen queen) {
        Row row = queen.getRow();
        if (row != null) {
            for (Queen otherQueen : insertedQueenList) {
                if (queen.getRowIndex() == otherQueen.getRowIndex()) {
                    score--;
                }
                if (queen.getAscendingDiagonalIndex() == otherQueen.getAscendingDiagonalIndex()) {
                    score--;
                }
                if (queen.getDescendingDiagonalIndex() == otherQueen.getDescendingDiagonalIndex()) {
                    score--;
                }
            }
            insertedQueenList.add(queen);
        }
    }

    private void retract(Queen queen) {
        Row row = queen.getRow();
        if (row != null) {
            insertedQueenList.remove(queen);
            for (Queen otherQueen : insertedQueenList) {
                if (queen.getRowIndex() == otherQueen.getRowIndex()) {
                    score++;
                }
                if (queen.getAscendingDiagonalIndex() == otherQueen.getAscendingDiagonalIndex()) {
                    score++;
                }
                if (queen.getDescendingDiagonalIndex() == otherQueen.getDescendingDiagonalIndex()) {
                    score++;
                }
            }
        }
    }

    @Override
    public SimpleScore calculateScore() {
        return SimpleScore.of(score);
    }

}
