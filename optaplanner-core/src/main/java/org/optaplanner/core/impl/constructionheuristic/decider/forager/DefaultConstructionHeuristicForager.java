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

package org.optaplanner.core.impl.constructionheuristic.decider.forager;

import java.util.Comparator;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.constructionheuristic.decider.forager.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicMoveScope;
import org.optaplanner.core.impl.constructionheuristic.scope.ConstructionHeuristicStepScope;

public class DefaultConstructionHeuristicForager extends AbstractConstructionHeuristicForager {

    protected final ConstructionHeuristicPickEarlyType pickEarlyType;

    protected final Comparator<Score> scoreComparator = Comparable::compareTo;

    protected long selectedMoveCount;
    protected ConstructionHeuristicMoveScope earlyPickedMoveScope;
    protected ConstructionHeuristicMoveScope maxScoreMoveScope;

    public DefaultConstructionHeuristicForager(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void stepStarted(ConstructionHeuristicStepScope stepScope) {
        super.stepStarted(stepScope);
        selectedMoveCount = 0L;
        earlyPickedMoveScope = null;
        maxScoreMoveScope = null;
    }

    @Override
    public void stepEnded(ConstructionHeuristicStepScope stepScope) {
        super.stepEnded(stepScope);
        earlyPickedMoveScope = null;
        maxScoreMoveScope = null;
    }

    @Override
    public void addMove(ConstructionHeuristicMoveScope moveScope) {
        selectedMoveCount++;
        checkPickEarly(moveScope);
        if (maxScoreMoveScope == null
                || scoreComparator.compare(moveScope.getScore(), maxScoreMoveScope.getScore()) > 0) {
            maxScoreMoveScope = moveScope;
        }
    }

    protected void checkPickEarly(ConstructionHeuristicMoveScope moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_NON_DETERIORATING_SCORE:
                Score lastStepScore = moveScope.getStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (moveScope.getScore().withInitScore(0).compareTo(lastStepScore.withInitScore(0)) >= 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_FEASIBLE_SCORE:
                if (moveScope.getScore().withInitScore(0).isFeasible()) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_FEASIBLE_SCORE_OR_NON_DETERIORATING_HARD:
                Score lastStepScore2 = moveScope.getStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                Score lastStepScoreDifference = moveScope.getScore().withInitScore(0)
                        .subtract(lastStepScore2.withInitScore(0));
                if (lastStepScoreDifference.isFeasible()) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented.");
        }
    }

    @Override
    public boolean isQuitEarly() {
        return earlyPickedMoveScope != null;
    }

    @Override
    public ConstructionHeuristicMoveScope pickMove(ConstructionHeuristicStepScope stepScope) {
        stepScope.setSelectedMoveCount(selectedMoveCount);
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return maxScoreMoveScope;
        }
    }

}
