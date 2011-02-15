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

package org.drools.planner.core.localsearch.decider;

import java.util.Random;

import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;

public class MoveScope {

    private final LocalSearchStepScope localSearchStepScope;
    private Move move = null;
    private Move undoMove = null;
    private double acceptChance = Double.NaN;
    private Score score = null;

    public MoveScope(LocalSearchStepScope localSearchStepScope) {
        this.localSearchStepScope = localSearchStepScope;
    }

    public LocalSearchStepScope getLocalSearchStepScope() {
        return localSearchStepScope;
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

    public double getAcceptChance() {
        return acceptChance;
    }

    public void setAcceptChance(double acceptChance) {
        this.acceptChance = acceptChance;
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

    public Solution getWorkingSolution() {
        return localSearchStepScope.getWorkingSolution();
    }

    public WorkingMemory getWorkingMemory() {
        return localSearchStepScope.getWorkingMemory();
    }

    public Random getWorkingRandom() {
        return localSearchStepScope.getWorkingRandom();
    }

}
