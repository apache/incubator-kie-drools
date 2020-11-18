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

package org.optaplanner.examples.cheaptime.optional.solver.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;

public class CheapTimePillarSlideMove extends AbstractMove<CheapTimeSolution> {

    private final List<TaskAssignment> pillar;
    private final int startPeriodDiff;

    public CheapTimePillarSlideMove(List<TaskAssignment> pillar, int startPeriodDiff) {
        this.pillar = pillar;
        this.startPeriodDiff = startPeriodDiff;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<CheapTimeSolution> scoreDirector) {
        return true;
    }

    @Override
    public CheapTimePillarSlideMove createUndoMove(ScoreDirector<CheapTimeSolution> scoreDirector) {
        return new CheapTimePillarSlideMove(pillar, -startPeriodDiff);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<CheapTimeSolution> scoreDirector) {
        for (TaskAssignment taskAssignment : pillar) {
            scoreDirector.beforeVariableChanged(taskAssignment, "startPeriod");
            taskAssignment.setStartPeriod(taskAssignment.getStartPeriod() + startPeriodDiff);
            scoreDirector.afterVariableChanged(taskAssignment, "startPeriod");
        }
    }

    @Override
    public CheapTimePillarSlideMove rebase(ScoreDirector<CheapTimeSolution> destinationScoreDirector) {
        return new CheapTimePillarSlideMove(rebaseList(pillar, destinationScoreDirector), startPeriodDiff);
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(pillar);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        // Presumes this method is always called after the move is done.
        List<Integer> startPeriodList = new ArrayList<>(pillar.size());
        for (TaskAssignment taskAssignment : pillar) {
            startPeriodList.add(taskAssignment.getStartPeriod());
        }
        return Collections.singletonList(startPeriodList);
    }

    @Override
    public String toString() {
        return pillar + " {" + (startPeriodDiff < 0 ? startPeriodDiff : "+" + startPeriodDiff) + "}";
    }

}
