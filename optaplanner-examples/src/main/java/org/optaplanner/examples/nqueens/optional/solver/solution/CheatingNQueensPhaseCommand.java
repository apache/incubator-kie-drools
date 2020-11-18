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

package org.optaplanner.examples.nqueens.optional.solver.solution;

import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.domain.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Because N Queens is not NP-complete or NP-hard, it can be cheated.
 * For this reason, N queens should not be used for benchmarking purposes.
 * <p>
 * This class solves any N Queens instance using a polynomial time algorithm
 * (<a href="https://en.wikipedia.org/wiki/Eight_queens_puzzle#Explicit_solutions">explicit solutions algorithm</a>).
 */
public class CheatingNQueensPhaseCommand implements CustomPhaseCommand<NQueens> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void changeWorkingSolution(ScoreDirector<NQueens> scoreDirector) {
        NQueens nQueens = scoreDirector.getWorkingSolution();
        int n = nQueens.getN();
        List<Queen> queenList = nQueens.getQueenList();
        List<Row> rowList = nQueens.getRowList();

        if (n % 2 != 0) {
            Queen a = queenList.get(n - 1);
            scoreDirector.beforeVariableChanged(a, "row");
            a.setRow(rowList.get(n - 1));
            scoreDirector.afterVariableChanged(a, "row");
            n--;
        }
        int halfN = n / 2;
        if (n % 6 != 2) {
            for (int i = 0; i < halfN; i++) {
                Queen a = queenList.get(i);
                scoreDirector.beforeVariableChanged(a, "row");
                a.setRow(rowList.get((2 * i) + 1));
                scoreDirector.afterVariableChanged(a, "row");

                Queen b = queenList.get(halfN + i);
                scoreDirector.beforeVariableChanged(b, "row");
                b.setRow(rowList.get(2 * i));
                scoreDirector.afterVariableChanged(b, "row");
            }
        } else {
            for (int i = 0; i < halfN; i++) {
                Queen a = queenList.get(i);
                scoreDirector.beforeVariableChanged(a, "row");
                a.setRow(rowList.get((halfN + (2 * i) - 1) % n));
                scoreDirector.afterVariableChanged(a, "row");

                Queen b = queenList.get(n - i - 1);
                scoreDirector.beforeVariableChanged(b, "row");
                b.setRow(rowList.get(n - 1 - ((halfN + (2 * i) - 1) % n)));
                scoreDirector.afterVariableChanged(b, "row");
            }
        }
        scoreDirector.triggerVariableListeners();
    }

}
