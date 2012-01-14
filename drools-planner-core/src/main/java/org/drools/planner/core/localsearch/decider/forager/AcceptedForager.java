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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.MoveScope;
import org.drools.planner.core.localsearch.decider.acceptor.Acceptor;
import org.drools.planner.core.localsearch.decider.deciderscorecomparator.DeciderScoreComparatorFactory;
import org.drools.planner.core.move.Move;
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
    protected AcceptedMoveScopeComparator acceptedMoveScopeComparator;

    protected int selectedCount;
    protected List<MoveScope> acceptedList;
    protected List<MoveScope> maxScoreAcceptedList;
    protected Score maxScore;

    protected MoveScope earlyPickedMoveScope;

    public AcceptedForager(PickEarlyType pickEarlyType, int minimalAcceptedSelection) {
        this.pickEarlyType = pickEarlyType;
        this.minimalAcceptedSelection = minimalAcceptedSelection;
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
    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.beforeDeciding(localSearchStepScope);
        scoreComparator = deciderScoreComparatorFactory.createDeciderScoreComparator();
        acceptedMoveScopeComparator = new AcceptedMoveScopeComparator(scoreComparator);
        selectedCount = 0;
        acceptedList = new ArrayList<MoveScope>(1024); // TODO use size of moveList in decider
        maxScoreAcceptedList = new ArrayList<MoveScope>(1024); // TODO use size of moveList in decider
        maxScore = localSearchStepScope.getSolverPhaseScope().getScoreDefinition().getPerfectMinimumScore();
        earlyPickedMoveScope = null;
    }

    public void addMove(MoveScope moveScope) {
        selectedCount++;
        if (moveScope.getAccepted()) {
            checkPickEarly(moveScope);
            addMoveScopeToAcceptedList(moveScope);
        }
    }

    protected void checkPickEarly(MoveScope moveScope) {
        switch (pickEarlyType) {
            case NEVER:
                break;
            case FIRST_BEST_SCORE_IMPROVING:
                Score bestScore = moveScope.getLocalSearchStepScope().getSolverPhaseScope().getBestScore();
                if (scoreComparator.compare(moveScope.getScore(), bestScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            case FIRST_LAST_STEP_SCORE_IMPROVING:
                Score lastStepScore = moveScope.getLocalSearchStepScope().getSolverPhaseScope()
                        .getLastCompletedStepScope().getScore();
                if (scoreComparator.compare(moveScope.getScore(), lastStepScore) > 0) {
                    earlyPickedMoveScope = moveScope;
                }
                break;
            default:
                throw new IllegalStateException("The pickEarlyType (" + pickEarlyType + ") is not implemented");
        }
    }

    protected void addMoveScopeToAcceptedList(MoveScope moveScope) {
        acceptedList.add(moveScope);
        if (scoreComparator.compare(moveScope.getScore(), maxScore) > 0) {
            maxScore = moveScope.getScore();
            maxScoreAcceptedList.clear();
            maxScoreAcceptedList.add(moveScope);
        } else if (moveScope.getScore().equals(maxScore)) {
            maxScoreAcceptedList.add(moveScope);
        }
    }

    public boolean isQuitEarly() {
        return earlyPickedMoveScope != null || acceptedList.size() >= minimalAcceptedSelection;
    }

    public MoveScope pickMove(LocalSearchStepScope localSearchStepScope) {
        if (earlyPickedMoveScope != null) {
            return earlyPickedMoveScope;
        } else {
            return pickMaxScoreMoveScopeFromAcceptedList(localSearchStepScope);
        }
    }

    protected MoveScope pickMaxScoreMoveScopeFromAcceptedList(LocalSearchStepScope localSearchStepScope) {
        if (maxScoreAcceptedList.isEmpty()) {
            return null;
        }
        if (maxScoreAcceptedList.size() == 1) {
            return maxScoreAcceptedList.get(0);
        }
        int randomIndex = localSearchStepScope.getWorkingRandom().nextInt(maxScoreAcceptedList.size());
        return maxScoreAcceptedList.get(randomIndex);
    }

    public int getAcceptedMovesSize() {
        return acceptedList.size();
    }

    public List<Move> getTopList(int topSize) {
        List<MoveScope> sortedAcceptedList = new ArrayList<MoveScope>(acceptedList);
        Collections.sort(sortedAcceptedList, acceptedMoveScopeComparator);
        int size = sortedAcceptedList.size();
        List<Move> topList = new ArrayList<Move>(Math.min(topSize, size));
        List<MoveScope> subAcceptedList = sortedAcceptedList.subList(Math.max(0, size - topSize), size);
        for (MoveScope moveScope : subAcceptedList) {
            topList.add(moveScope.getMove());
        }
        return topList;
    }

    @Override
    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepDecided(localSearchStepScope);
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        deciderScoreComparatorFactory.stepTaken(localSearchStepScope);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        deciderScoreComparatorFactory.phaseEnded(localSearchSolverPhaseScope);
    }

}
