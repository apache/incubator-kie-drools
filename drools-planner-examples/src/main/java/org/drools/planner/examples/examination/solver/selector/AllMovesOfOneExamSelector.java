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

package org.drools.planner.examples.examination.solver.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.localsearch.LocalSearchSolverScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;
import org.drools.planner.core.localsearch.decider.Decider;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.localsearch.decider.selector.AbstractSelector;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.solver.move.factory.ExamSwitchMoveFactory;
import org.drools.planner.examples.examination.solver.move.factory.PeriodChangeMoveFactory;
import org.drools.planner.examples.examination.solver.move.factory.RoomChangeMoveFactory;

/**
 * A custom selector implementation for the Examination example.
 */
public class AllMovesOfOneExamSelector extends AbstractSelector {

    protected PeriodChangeMoveFactory periodChangeMoveFactory = new PeriodChangeMoveFactory();
    protected RoomChangeMoveFactory roomChangeMoveFactory = new RoomChangeMoveFactory();
    protected ExamSwitchMoveFactory examSwitchMoveFactory = new ExamSwitchMoveFactory();

    protected Map<Exam, List<Move>> cachedExamToMoveMap;
    protected List<Exam> shuffledExamList;
    protected int nextShuffledExamListIndex;

    @Override
    public void setDecider(Decider decider) {
        super.setDecider(decider);
        periodChangeMoveFactory.setDecider(decider);
        roomChangeMoveFactory.setDecider(decider);
        examSwitchMoveFactory.setDecider(decider);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void solvingStarted(LocalSearchSolverScope localSearchSolverScope) {
        periodChangeMoveFactory.solvingStarted(localSearchSolverScope);
        roomChangeMoveFactory.solvingStarted(localSearchSolverScope);
        examSwitchMoveFactory.solvingStarted(localSearchSolverScope);
        createCachedExamToMoveMap(localSearchSolverScope);
    }

    private void createCachedExamToMoveMap(LocalSearchSolverScope localSearchSolverScope) {
        Examination examination = (Examination) localSearchSolverScope.getWorkingSolution();
        int examListSize = examination.getExamList().size();
        List<Move> cachedPeriodChangeMoveList = periodChangeMoveFactory.getCachedMoveList();
        List<Move> cachedRoomChangeMoveList = roomChangeMoveFactory.getCachedMoveList();
        List<Move> cachedExamSwitchMoveList = examSwitchMoveFactory.getCachedMoveList();
        cachedExamToMoveMap = new HashMap<Exam, List<Move>>(cachedPeriodChangeMoveList.size()
                + cachedRoomChangeMoveList.size() + cachedExamSwitchMoveList.size());
        addToCachedExamToMoveMap(examListSize, cachedPeriodChangeMoveList);
        addToCachedExamToMoveMap(examListSize, cachedRoomChangeMoveList);
        addToCachedExamToMoveMap(examListSize, cachedExamSwitchMoveList);
        shuffledExamList = new ArrayList<Exam>(cachedExamToMoveMap.keySet());
        // shuffling is lazy (just in time in the selectMoveList method)
        nextShuffledExamListIndex = Integer.MAX_VALUE;
    }

    private void addToCachedExamToMoveMap(int examListSize, List<Move> cachedMoveList) {
        for (Move cachedMove : cachedMoveList) {
            TabuPropertyEnabled tabuPropertyEnabledMove = (TabuPropertyEnabled) cachedMove;
            for (Object o : tabuPropertyEnabledMove.getTabuProperties()) {
                Exam exam = (Exam) o;
                List<Move> moveList = cachedExamToMoveMap.get(exam);
                if (moveList == null) {
                    moveList = new ArrayList<Move>(examListSize);
                    cachedExamToMoveMap.put(exam, moveList);
                }
                moveList.add(cachedMove);
            }
        }
    }

    @Override
    public void beforeDeciding(LocalSearchStepScope localSearchStepScope) {
        periodChangeMoveFactory.beforeDeciding(localSearchStepScope);
        roomChangeMoveFactory.beforeDeciding(localSearchStepScope);
        examSwitchMoveFactory.beforeDeciding(localSearchStepScope);
    }

    public Iterator<Move> moveIterator(LocalSearchStepScope localSearchStepScope) {
        if (nextShuffledExamListIndex >= shuffledExamList.size()) {
            // Just in time shuffling
            Collections.shuffle(shuffledExamList, localSearchStepScope.getWorkingRandom());
            nextShuffledExamListIndex = 0;
        }
        Exam exam = shuffledExamList.get(nextShuffledExamListIndex);
        List<Move> moveList = cachedExamToMoveMap.get(exam);
        nextShuffledExamListIndex++;
        return moveList.iterator();
    }

    @Override
    public void stepDecided(LocalSearchStepScope localSearchStepScope) {
        periodChangeMoveFactory.stepDecided(localSearchStepScope);
        roomChangeMoveFactory.stepDecided(localSearchStepScope);
        examSwitchMoveFactory.stepDecided(localSearchStepScope);
    }

    @Override
    public void stepTaken(LocalSearchStepScope localSearchStepScope) {
        periodChangeMoveFactory.stepTaken(localSearchStepScope);
        roomChangeMoveFactory.stepTaken(localSearchStepScope);
        examSwitchMoveFactory.stepTaken(localSearchStepScope);
    }

    @Override
    public void solvingEnded(LocalSearchSolverScope localSearchSolverScope) {
        periodChangeMoveFactory.solvingEnded(localSearchSolverScope);
        roomChangeMoveFactory.solvingEnded(localSearchSolverScope);
        examSwitchMoveFactory.solvingEnded(localSearchSolverScope);
    }

}
