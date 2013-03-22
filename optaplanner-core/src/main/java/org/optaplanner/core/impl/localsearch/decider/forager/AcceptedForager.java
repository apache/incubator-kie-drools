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

package org.optaplanner.core.impl.localsearch.decider.forager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.impl.localsearch.decider.acceptor.Acceptor;
import org.optaplanner.core.impl.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactory;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchMoveScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchSolverPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.api.score.Score;

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
    protected List<LocalSearchMoveScope> maxScoreAcceptedList;
    protected Score maxAcceptedScore;
    protected List<LocalSearchMoveScope> maxScoreUnacceptedList;
    protected Score maxUnacceptedScore;

    protected LocalSearchMoveScope earlyPickedMoveScope;

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
    public void phaseStarted(LocalSearchSolverPhaseScope phaseScope) {
        deciderScoreComparatorFactory.phaseStarted(phaseScope);
    }

    @Override
    public void stepStarted(LocalSearchStepScope stepScope) {
        deciderScoreComparatorFactory.stepStarted(stepScope);
        scoreComparator = deciderScoreComparatorFactory.createDeciderScoreComparator();
        selectedMoveCount = 0L;
        acceptedMoveCount = 0L;
        maxScoreAcceptedList = new ArrayList<LocalSearchMoveScope>(1024);
        maxAcceptedScore = null;
        maxScoreUnacceptedList = new ArrayList<LocalSearchMoveScope>(1024);
        maxUnacceptedScore = null;
        earlyPickedMoveScope = null;
    }

    public boolean supportsNeverEndingMoveSelector() {
        // TODO FIXME magical value Integer.MAX_VALUE coming from ForagerConfig
        return minimalAcceptedSelection < Integer.MAX_VALUE;
    }

    public void addMove(LocalSearchMoveScope moveScope) {
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

    protected void checkPickEarly(LocalSearchMoveScope moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_BEST_SCORE_IMPROVING:
                Score bestScore = moveScope.getStepScope().getPhaseScope().getBestScore();
                if (scoreComparator.compare(moveScope.getScore(), bestScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_LAST_STEP_SCORE_IMPROVING:
                Score lastStepScore = moveScope.getStepScope().getPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (scoreComparator.compare(moveScope.getScore(), lastStepScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented.");
        }
    }

    protected void addToMaxScoreAcceptedList(LocalSearchMoveScope moveScope) {
        if (maxAcceptedScore == null || scoreComparator.compare(moveScope.getScore(), maxAcceptedScore) > 0) {
            maxAcceptedScore = moveScope.getScore();
            maxScoreAcceptedList.clear();
            maxScoreAcceptedList.add(moveScope);
        } else if (moveScope.getScore().equals(maxAcceptedScore)) {
            maxScoreAcceptedList.add(moveScope);
        }
    }

    protected void addToMaxScoreUnacceptedList(LocalSearchMoveScope moveScope) {
        if (maxUnacceptedScore == null || scoreComparator.compare(moveScope.getScore(), maxUnacceptedScore) > 0) {
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

    public LocalSearchMoveScope pickMove(LocalSearchStepScope stepScope) {
        stepScope.setSelectedMoveCount(selectedMoveCount);
        stepScope.setAcceptedMoveCount(acceptedMoveCount);
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return pickMaxScoreMoveScope(stepScope);
        }
    }

    protected LocalSearchMoveScope pickMaxScoreMoveScope(LocalSearchStepScope stepScope) {
        List<LocalSearchMoveScope> maxScoreList;
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
        int randomIndex = stepScope.getWorkingRandom().nextInt(maxScoreList.size());
        return maxScoreList.get(randomIndex);
    }

    @Override
    public void stepEnded(LocalSearchStepScope stepScope) {
        deciderScoreComparatorFactory.stepEnded(stepScope);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope phaseScope) {
        deciderScoreComparatorFactory.phaseEnded(phaseScope);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + pickEarlyType + ", " + minimalAcceptedSelection + ")";
    }

}
