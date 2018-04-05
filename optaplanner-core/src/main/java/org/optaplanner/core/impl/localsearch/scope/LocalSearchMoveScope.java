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

package org.optaplanner.core.impl.localsearch.scope;

import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class LocalSearchMoveScope<Solution_> {

    private final LocalSearchStepScope<Solution_> stepScope;
    private final int moveIndex;
    private final Move<Solution_> move;

    private Score score = null;
    private Boolean accepted = null;

    public LocalSearchMoveScope(LocalSearchStepScope<Solution_> stepScope, int moveIndex, Move<Solution_> move) {
        this.stepScope = stepScope;
        this.moveIndex = moveIndex;
        this.move = move;
    }

    public LocalSearchStepScope<Solution_> getStepScope() {
        return stepScope;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public Move<Solution_> getMove() {
        return move;
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

    public InnerScoreDirector<Solution_> getScoreDirector() {
        return stepScope.getScoreDirector();
    }

    public Solution_ getWorkingSolution() {
        return stepScope.getWorkingSolution();
    }

    public Random getWorkingRandom() {
        return stepScope.getWorkingRandom();
    }

    public LocalSearchMoveScope<Solution_> rebase(LocalSearchStepScope<Solution_> destinationStepScope,
            InnerScoreDirector<Solution_> destinationScoreDirector) {
        if (stepScope.getStepIndex() != destinationStepScope.getStepIndex()) {
            throw new IllegalStateException("Impossible situation: rebasing of MoveScope with stepIndex ("
                    + stepScope.getStepIndex() + ") and moveIndex (" + moveIndex
                    + ") fails because the destinationStepScope has a different stepIndex ("
                    + destinationStepScope.getStepIndex() + ").");
        }
        Move<Solution_> rebasedMove = move.rebase(destinationScoreDirector);
        LocalSearchMoveScope<Solution_> rebasedMoveScope = new LocalSearchMoveScope<>(destinationStepScope, moveIndex, rebasedMove);
        rebasedMoveScope.setScore(score);
        rebasedMoveScope.setAccepted(accepted);
        return rebasedMoveScope;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + stepScope.getStepIndex() + "/" + moveIndex + ")";
    }

}
