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

package org.optaplanner.core.impl.localsearch.scope;

import java.util.Random;

import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solution.Solution;

public class LocalSearchMoveScope {

    private final LocalSearchStepScope stepScope;

    private int moveIndex;
    private Move move = null;
    private Move undoMove = null;
    private Score score = null;
    private Boolean accepted = null;

    public LocalSearchMoveScope(LocalSearchStepScope stepScope) {
        this.stepScope = stepScope;
    }

    public LocalSearchStepScope getStepScope() {
        return stepScope;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Move getUndoMove() {
        return undoMove;
    }

    public void setUndoMove(Move undoMove) {
        this.undoMove = undoMove;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public ScoreDirector getScoreDirector() {
        return stepScope.getScoreDirector();
    }

    public Solution getWorkingSolution() {
        return stepScope.getWorkingSolution();
    }

    public Random getWorkingRandom() {
        return stepScope.getWorkingRandom();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + moveIndex + ")";
    }

}
