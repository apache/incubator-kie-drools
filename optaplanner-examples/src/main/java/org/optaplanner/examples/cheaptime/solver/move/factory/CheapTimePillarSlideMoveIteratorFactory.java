/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.solver.move.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.solver.move.CheapTimePillarSlideMove;

public class CheapTimePillarSlideMoveIteratorFactory implements MoveIteratorFactory {

    @Override
    public long getSize(ScoreDirector scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Move> createOriginalMoveIterator(ScoreDirector scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Move> createRandomMoveIterator(ScoreDirector scoreDirector, Random workingRandom) {
        CheapTimeSolution cheapTimeSolution = (CheapTimeSolution) scoreDirector.getWorkingSolution();
        Map<Machine, List<TaskAssignment>> positivePillarMap = new LinkedHashMap<Machine, List<TaskAssignment>>(
                cheapTimeSolution.getGlobalPeriodRangeTo());
        Map<Machine, List<TaskAssignment>> negativePillarMap = new LinkedHashMap<Machine, List<TaskAssignment>>(
                cheapTimeSolution.getGlobalPeriodRangeTo());
        List<TaskAssignment> taskAssignmentList = cheapTimeSolution.getTaskAssignmentList();
        int pillarCapacity = (taskAssignmentList.size() * 2 / cheapTimeSolution.getMachineList().size()) + 1;
        for (TaskAssignment taskAssignment : taskAssignmentList) {
            Machine machine = taskAssignment.getMachine();
            Task task = taskAssignment.getTask();
            Integer startPeriod = taskAssignment.getStartPeriod();
            if (startPeriod != null) {
                if (startPeriod < task.getStartPeriodRangeTo() - 1) {
                    List<TaskAssignment> pillar = positivePillarMap.get(machine);
                    if (pillar == null) {
                        pillar = new ArrayList<TaskAssignment>(pillarCapacity);
                        positivePillarMap.put(machine, pillar);
                    }
                    pillar.add(taskAssignment);
                }
                if (startPeriod > task.getStartPeriodRangeFrom()) {
                    List<TaskAssignment> pillar = negativePillarMap.get(machine);
                    if (pillar == null) {
                        pillar = new ArrayList<TaskAssignment>(pillarCapacity);
                        negativePillarMap.put(machine, pillar);
                    }
                    pillar.add(taskAssignment);
                }
            }
        }
        List<List<TaskAssignment>> positivePillarList = new ArrayList<List<TaskAssignment>>(positivePillarMap.size());
        for (List<TaskAssignment> pillar : positivePillarMap.values()) {
            if (pillar.size() > 1) {
                positivePillarList.add(pillar);
            }
        }
        List<List<TaskAssignment>> negativePillarList = new ArrayList<List<TaskAssignment>>(negativePillarMap.size());
        for (List<TaskAssignment> pillar : negativePillarMap.values()) {
            if (pillar.size() > 1) {
                negativePillarList.add(pillar);
            }
        }
        return new RandomCheapTimePillarSlideMoveIterator(positivePillarList, negativePillarList, workingRandom);
    }

    private class RandomCheapTimePillarSlideMoveIterator implements Iterator<Move> {

        private final List<List<TaskAssignment>> positivePillarList;
        private final List<List<TaskAssignment>> negativePillarList;
        private final Random workingRandom;
        private final int totalSize;

        public RandomCheapTimePillarSlideMoveIterator(List<List<TaskAssignment>> positivePillarList,
                List<List<TaskAssignment>> negativePillarList, Random workingRandom) {
            this.positivePillarList = positivePillarList;
            this.negativePillarList = negativePillarList;
            this.workingRandom = workingRandom;
            totalSize = positivePillarList.size() + negativePillarList.size();
        }

        public boolean hasNext() {
            return totalSize > 0;
        }

        public Move next() {
            int listIndex = workingRandom.nextInt(totalSize);
            boolean positive = listIndex < positivePillarList.size();
            List<TaskAssignment> basePillar = positive ? positivePillarList.get(listIndex)
                    : negativePillarList.get(listIndex - positivePillarList.size());
            int basePillarSize = basePillar.size();
            int subPillarSize = workingRandom.nextInt(basePillarSize);
            // Random sampling: See http://eyalsch.wordpress.com/2010/04/01/random-sample/
            // Used Swapping instead of Floyd because subPillarSize is large, to avoid hashCode() hit
            TaskAssignment[] sandboxPillar = basePillar.toArray(new TaskAssignment[0]); // Clone to avoid changing basePillar
            List<TaskAssignment> subPillar = new ArrayList<TaskAssignment>(subPillarSize);
            int minimumAbsDiff = Integer.MAX_VALUE;
            for (int i = 0; i < subPillarSize; i++) {
                int index = i + workingRandom.nextInt(basePillarSize - i);
                TaskAssignment taskAssignment = sandboxPillar[index];
                Task task = taskAssignment.getTask();
                int absDiff = positive ? task.getStartPeriodRangeTo() - 1 - taskAssignment.getStartPeriod()
                        : taskAssignment.getStartPeriod() - task.getStartPeriodRangeFrom();
                if (absDiff < minimumAbsDiff) {
                    minimumAbsDiff = absDiff;
                }
                subPillar.add(taskAssignment);
                sandboxPillar[index] = sandboxPillar[i];
            }
            int startPeriodDiff = 1 + workingRandom.nextInt(minimumAbsDiff);
            if (!positive) {
                startPeriodDiff = -startPeriodDiff;
            }
            return new CheapTimePillarSlideMove(subPillar, startPeriodDiff);
        }

        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

    }

}
