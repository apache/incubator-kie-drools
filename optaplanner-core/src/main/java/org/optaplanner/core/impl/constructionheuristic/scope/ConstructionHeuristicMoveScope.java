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

package org.optaplanner.core.impl.constructionheuristic.scope;

import java.util.Random;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ConstructionHeuristicMoveScope<Solution_> {

    private final ConstructionHeuristicStepScope<Solution_> stepScope;

    private int moveIndex;
    private Move<Solution_> move = null;
    private Score score = null;

    public ConstructionHeuristicMoveScope(ConstructionHeuristicStepScope<Solution_> stepScope) {
        this.stepScope = stepScope;
    }

    public ConstructionHeuristicStepScope<Solution_> getStepScope() {
        return stepScope;
    }

    public int getMoveIndex() {
        return moveIndex;
    }

    public void setMoveIndex(int moveIndex) {
        this.moveIndex = moveIndex;
    }

    public Move<Solution_> getMove() {
        return move;
    }

    public void setMove(Move<Solution_> move) {
        this.move = move;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + stepScope.getStepIndex() + "/" + moveIndex + ")";
    }

}
