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

package org.drools.planner.core.localsearch.decider.forager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactory;
import org.drools.planner.core.score.Score;

/**
 * An AcceptedForager is a Forager which forages accepted moves and ignores unaccepted moves.
 * @see Forager
 * @see Acceptor
 */
public class AcceptedForager extends AbstractForager {

    protected DeciderScoreComparatorFactory deciderScoreComparatorFactory;
    // final to allow better hotspot optimization. TODO prove that it indeed makes a difference
    protected final PickEarlyType pickEarlyType;
    protected final int minimalAcceptedSelection;

    protected Comparator<Score> scoreComparator;

    protected long selectedMoveCount;
    protected long acceptedMoveCount;
    protected List<MoveScope> maxScoreAcceptedList;
    protected Score maxAcceptedScore;
    protected List<MoveScope> maxScoreUnacceptedList;
    protected Score maxUnacceptedScore;

    protected MoveScope earlyPickedMoveScope;

    public AcceptedForager(PickEarlyType pickEarlyType, int minimalAcceptedSelection) {
        this.pickEarlyType = pickEarlyType;
        this.minimalAcceptedSelection = minimalAcceptedSelection;
        if (minimalAcceptedSelection < 1) {
            throw new IllegalArgumentException("The minimalAcceptedSelection (" + minimalAcceptedSelection
                    + ") cannot be negative or zero.");
        }
    }

    public void setDeciderScoreComparatorFactory(DeciderScoreComparatorFactory deciderScoreComparator) {
        this.deciderScoreComparatorFactory = deciderScoreComparator;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        deciderScoreComparatorFactory.phaseStarted(localSearchSolverPhaseScope);
    }

    @Override
    public void stepStarted(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepStarted(localSearchStepScope);
        scoreComparator = deciderScoreComparatorFactory.createDeciderScoreComparator();
        selectedMoveCount = 0L;
        acceptedMoveCount = 0L;
        maxScoreAcceptedList = new ArrayList<MoveScope>(1024);
        maxAcceptedScore = localSearchStepScope.getPhaseScope().getScoreDefinition().getPerfectMinimumScore();
        maxScoreUnacceptedList = new ArrayList<MoveScope>(1024);
        maxUnacceptedScore = localSearchStepScope.getPhaseScope().getScoreDefinition().getPerfectMinimumScore();
        earlyPickedMoveScope = null;
    }

    public boolean supportsNeverEndingMoveSelector() {
        // TODO FIXME magical value Integer.MAX_VALUE coming from ForagerConfig
        return minimalAcceptedSelection < Integer.MAX_VALUE;
    }

    public void addMove(MoveScope moveScope) {
        selectedMoveCount++;
        if (moveScope.getAccepted()) {
            acceptedMoveCount++;
            checkPickEarly(moveScope);
            addToMaxScoreAcceptedList(moveScope);
            maxScoreUnacceptedList = null;
        } else if (acceptedMoveCount == 0L) {
            addToMaxScoreUnacceptedList(moveScope);
        }
    }

    protected void checkPickEarly(MoveScope moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_BEST_SCORE_IMPROVING:
                Score bestScore = moveScope.getLocalSearchStepScope().getPhaseScope().getBestScore();
                if (scoreComparator.compare(moveScope.getScore(), bestScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_LAST_STEP_SCORE_IMPROVING:
                Score lastStepScore = moveScope.getLocalSearchStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (scoreComparator.compare(moveScope.getScore(), lastStepScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented");
        }
    }

    protected void addToMaxScoreAcceptedList(MoveScope moveScope) {
        if (scoreComparator.compare(moveScope.getScore(), maxAcceptedScore) > 0) {
            maxAcceptedScore = moveScope.getScore();
            maxScoreAcceptedList.clear();
            maxScoreAcceptedList.add(moveScope);
        } else if (moveScope.getScore().equals(maxAcceptedScore)) {
            maxScoreAcceptedList.add(moveScope);
        }
    }

    protected void addToMaxScoreUnacceptedList(MoveScope moveScope) {
        if (scoreComparator.compare(moveScope.getScore(), maxUnacceptedScore) > 0) {
            maxUnacceptedScore = moveScope.getScore();
            maxScoreUnacceptedList.clear();
            maxScoreUnacceptedList.add(moveScope);
        } else if (moveScope.getScore().equals(maxUnacceptedScore)) {
            maxScoreUnacceptedList.add(moveScope);
        }
    }

    public boolean isQuitEarly() {
        return earlyPickedMoveScope != null || acceptedMoveCount >= minimalAcceptedSelection;
    }

    public MoveScope pickMove(LocalSearchStepScope localSearchStepScope) {
        localSearchStepScope.setSelectedMoveCount(selectedMoveCount);
        localSearchStepScope.setAcceptedMoveCount(acceptedMoveCount);
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return pickMaxScoreMoveScope(localSearchStepScope);
        }
    }

    protected MoveScope pickMaxScoreMoveScope(LocalSearchStepScope localSearchStepScope) {
        List<MoveScope> maxScoreList;
        if (maxScoreAcceptedList.isEmpty()) {
            if (maxScoreUnacceptedList.isEmpty()) {
                return null;
            } else {
                maxScoreList = maxScoreUnacceptedList;
            }
        } else {
            maxScoreList = maxScoreAcceptedList;
        }
        if (maxScoreList.size() == 1) {
            return maxScoreList.get(0);
        }
        int randomIndex = localSearchStepScope.getWorkingRandom().nextInt(maxScoreList.size());
        return maxScoreList.get(randomIndex);
    }

    @Override
    public void stepEnded(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepEnded(localSearchStepScope);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        deciderScoreComparatorFactory.phaseEnded(localSearchSolverPhaseScope);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + pickEarlyType + ", " + minimalAcceptedSelection + ")";
    }

}
