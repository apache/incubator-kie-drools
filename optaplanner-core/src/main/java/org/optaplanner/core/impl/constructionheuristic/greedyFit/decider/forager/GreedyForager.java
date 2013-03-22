/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.forager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.ConstructionHeuristicPickEarlyType;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.decider.GreedyMoveScope;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.constructionheuristic.greedyFit.scope.GreedyFitStepScope;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.comparator.NaturalScoreComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreedyForager extends GreedySolverPhaseLifecycleListenerAdapter {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected Comparator<Score> scoreComparator = new NaturalScoreComparator();
    protected ConstructionHeuristicPickEarlyType pickEarlyType;
    protected int minimalAcceptedSelection = Integer.MAX_VALUE;

    protected int selectedCount;
    protected List<GreedyMoveScope> maxScoreAcceptedList;
    protected Score maxScore;

    protected GreedyMoveScope earlyPickedMoveScope = null;

    public void setPickEarlyType(ConstructionHeuristicPickEarlyType pickEarlyType) {
        this.pickEarlyType = pickEarlyType;
    }

    public void setMinimalAcceptedSelection(int minimalAcceptedSelection) {
        this.minimalAcceptedSelection = minimalAcceptedSelection;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void stepStarted(GreedyFitStepScope greedyStepScope) {
        selectedCount = 0;
        maxScoreAcceptedList = new ArrayList<GreedyMoveScope>(1024); // TODO use size of moveList in decider
        maxScore = null;
        earlyPickedMoveScope = null;
    }

    public void addMove(GreedyMoveScope moveScope) {
        selectedCount++;
        checkPickEarly(moveScope);
        addMoveScopeToAcceptedList(moveScope);
    }

    protected void checkPickEarly(GreedyMoveScope moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_LAST_STEP_SCORE_EQUAL_OR_IMPROVING:
                Score lastStepScore = moveScope.getGreedyFitStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (lastStepScore != null && moveScope.getScore().compareTo(lastStepScore) >= 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented.");
        }
    }

    protected void addMoveScopeToAcceptedList(GreedyMoveScope moveScope) {
        if (maxScore == null || scoreComparator.compare(moveScope.getScore(), maxScore) > 0) {
            maxScore = moveScope.getScore();
            maxScoreAcceptedList.clear();
            maxScoreAcceptedList.add(moveScope);
        } else if (moveScope.getScore().equals(maxScore)) {
            maxScoreAcceptedList.add(moveScope);
        }
    }

    public boolean isQuitEarly() {
        return earlyPickedMoveScope != null || selectedCount >= minimalAcceptedSelection;
    }

    public GreedyMoveScope pickMove(GreedyFitStepScope greedyStepScope) {
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return pickMaxScoreMoveScopeFromAcceptedList(greedyStepScope);
        }
    }

    protected GreedyMoveScope pickMaxScoreMoveScopeFromAcceptedList(GreedyFitStepScope greedyStepScope) {
        if (maxScoreAcceptedList.isEmpty()) {
            return null;
        }
        if (maxScoreAcceptedList.size() == 1) {
            return maxScoreAcceptedList.get(0);
        }
        // TODO policy
        return maxScoreAcceptedList.get(0);
//        int randomIndex = greedyStepScope.getWorkingRandom().nextInt(maxScoreAcceptedList.size());
//        return maxScoreAcceptedList.get(randomIndex);
    }

    public int getSelectedCount() {
        return selectedCount;
    }

}
