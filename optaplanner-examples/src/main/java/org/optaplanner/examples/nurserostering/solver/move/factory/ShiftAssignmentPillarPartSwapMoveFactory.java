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

package org.optaplanner.examples.nurserostering.solver.move.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.solver.MovableShiftAssignmentSelectionFilter;
import org.optaplanner.examples.nurserostering.solver.drools.EmployeeWorkSequence;
import org.optaplanner.examples.nurserostering.solver.move.EmployeeMultipleChangeMove;

public class ShiftAssignmentPillarPartSwapMoveFactory implements MoveListFactory<NurseRoster> {

    private MovableShiftAssignmentSelectionFilter filter = new MovableShiftAssignmentSelectionFilter();

    @Override
    public List<Move<NurseRoster>> createMoveList(NurseRoster nurseRoster) {
        List<Employee> employeeList = nurseRoster.getEmployeeList();
        // This code assumes the shiftAssignmentList is sorted
        // Filter out every immovable ShiftAssignment
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>(
                nurseRoster.getShiftAssignmentList());
        shiftAssignmentList.removeIf(shiftAssignment -> !filter.accept(nurseRoster, shiftAssignment));

        // Hash the assignments per employee
        Map<Employee, List<AssignmentSequence>> employeeToAssignmentSequenceListMap
                = new HashMap<>(employeeList.size());
        int assignmentSequenceCapacity = nurseRoster.getShiftDateList().size() + 1 / 2;
        for (Employee employee : employeeList) {
            employeeToAssignmentSequenceListMap.put(employee,
                    new ArrayList<>(assignmentSequenceCapacity));
        }
        for (ShiftAssignment shiftAssignment : shiftAssignmentList) {
            Employee employee = shiftAssignment.getEmployee();
            List<AssignmentSequence> assignmentSequenceList = employeeToAssignmentSequenceListMap.get(employee);
            if (assignmentSequenceList.isEmpty()) {
                AssignmentSequence assignmentSequence = new AssignmentSequence(employee, shiftAssignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(shiftAssignment)) {
                    lastAssignmentSequence.add(shiftAssignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(employee, shiftAssignment);
                    assignmentSequenceList.add(assignmentSequence);
                }
            }
        }

        // The create the move list
        List<Move<NurseRoster>> moveList = new ArrayList<>();
        // For every 2 distinct employees
        for (ListIterator<Employee> leftEmployeeIt = employeeList.listIterator(); leftEmployeeIt.hasNext();) {
            Employee leftEmployee = leftEmployeeIt.next();
            List<AssignmentSequence> leftAssignmentSequenceList
                    = employeeToAssignmentSequenceListMap.get(leftEmployee);
            for (ListIterator<Employee> rightEmployeeIt = employeeList.listIterator(leftEmployeeIt.nextIndex());
                    rightEmployeeIt.hasNext();) {
                Employee rightEmployee = rightEmployeeIt.next();
                List<AssignmentSequence> rightAssignmentSequenceList = employeeToAssignmentSequenceListMap.get(
                        rightEmployee);

                LowestDayIndexAssignmentSequenceIterator lowestIt = new LowestDayIndexAssignmentSequenceIterator(
                        leftAssignmentSequenceList, rightAssignmentSequenceList);
                // For every pillar part duo
                while (lowestIt.hasNext()) {
                    AssignmentSequence pillarPartAssignmentSequence = lowestIt.next();
                    // Note: the initialCapacity is probably too high,
                    // which is bad for memory, but the opposite is bad for performance (which is worse)
                    List<EmployeeMultipleChangeMove> moveListByPillarPartDuo = new ArrayList<>(
                            leftAssignmentSequenceList.size() + rightAssignmentSequenceList.size());
                    int lastDayIndex = pillarPartAssignmentSequence.getLastDayIndex();
                    Employee otherEmployee;
                    int leftMinimumFirstDayIndex = Integer.MIN_VALUE;
                    int rightMinimumFirstDayIndex = Integer.MIN_VALUE;
                    if (lowestIt.isLastNextWasLeft()) {
                        otherEmployee = rightEmployee;
                        leftMinimumFirstDayIndex = lastDayIndex;
                    } else {
                        otherEmployee = leftEmployee;
                        rightMinimumFirstDayIndex = lastDayIndex;
                    }
                    moveListByPillarPartDuo.add(new EmployeeMultipleChangeMove(
                            pillarPartAssignmentSequence.getEmployee(),
                            pillarPartAssignmentSequence.getShiftAssignmentList(),
                            otherEmployee));
                    // For every AssignmentSequence in that pillar part duo
                    while (lowestIt.hasNextWithMaximumFirstDayIndexes(
                            leftMinimumFirstDayIndex, rightMinimumFirstDayIndex)) {
                        pillarPartAssignmentSequence = lowestIt.next();
                        lastDayIndex = pillarPartAssignmentSequence.getLastDayIndex();
                        if (lowestIt.isLastNextWasLeft()) {
                            otherEmployee = rightEmployee;
                            leftMinimumFirstDayIndex = Math.max(leftMinimumFirstDayIndex, lastDayIndex);
                        } else {
                            otherEmployee = leftEmployee;
                            rightMinimumFirstDayIndex = Math.max(rightMinimumFirstDayIndex, lastDayIndex);
                        }
                        moveListByPillarPartDuo.add(new EmployeeMultipleChangeMove(
                                pillarPartAssignmentSequence.getEmployee(),
                                pillarPartAssignmentSequence.getShiftAssignmentList(),
                                otherEmployee));
                    }
                    moveList.add(CompositeMove.buildMove(moveListByPillarPartDuo));
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link EmployeeWorkSequence}.
     */
    private static class AssignmentSequence {

        private Employee employee;
        private List<ShiftAssignment> shiftAssignmentList;
        private int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(Employee employee, ShiftAssignment shiftAssignment) {
            this.employee = employee;
            shiftAssignmentList = new ArrayList<>();
            shiftAssignmentList.add(shiftAssignment);
            firstDayIndex = shiftAssignment.getShiftDateDayIndex();
            lastDayIndex = firstDayIndex;
        }

        public Employee getEmployee() {
            return employee;
        }

        public List<ShiftAssignment> getShiftAssignmentList() {
            return shiftAssignmentList;
        }

        public int getFirstDayIndex() {
            return firstDayIndex;
        }

        public int getLastDayIndex() {
            return lastDayIndex;
        }

        private void add(ShiftAssignment shiftAssignment) {
            shiftAssignmentList.add(shiftAssignment);
            int dayIndex = shiftAssignment.getShiftDateDayIndex();
            if (dayIndex < lastDayIndex) {
                throw new IllegalStateException("The shiftAssignmentList is expected to be sorted by shiftDate.");
            }
            lastDayIndex = dayIndex;
        }

        private boolean belongsHere(ShiftAssignment shiftAssignment) {
            return shiftAssignment.getShiftDateDayIndex() <= (lastDayIndex + 1);
        }

    }

    private static class LowestDayIndexAssignmentSequenceIterator implements Iterator<AssignmentSequence> {

        private Iterator<AssignmentSequence> leftIterator;
        private Iterator<AssignmentSequence> rightIterator;

        private boolean leftHasNext = true;
        private boolean rightHasNext = true;

        private AssignmentSequence nextLeft;
        private AssignmentSequence nextRight;

        private boolean lastNextWasLeft;

        public LowestDayIndexAssignmentSequenceIterator(
                List<AssignmentSequence> leftAssignmentList, List<AssignmentSequence> rightAssignmentList) {
            // Buffer the nextLeft and nextRight
            leftIterator = leftAssignmentList.iterator();
            if (leftIterator.hasNext()) {
                nextLeft = leftIterator.next();
            } else {
                leftHasNext = false;
                nextLeft = null;
            }
            rightIterator = rightAssignmentList.iterator();
            if (rightIterator.hasNext()) {
                nextRight = rightIterator.next();
            } else {
                rightHasNext = false;
                nextRight = null;
            }
        }

        @Override
        public boolean hasNext() {
            return leftHasNext || rightHasNext;
        }

        public boolean hasNextWithMaximumFirstDayIndexes(
                int leftMinimumFirstDayIndex, int rightMinimumFirstDayIndex) {
            if (!hasNext()) {
                return false;
            }
            boolean nextIsLeft = nextIsLeft();
            if (nextIsLeft) {
                int firstDayIndex = nextLeft.getFirstDayIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstDayIndex > leftMinimumFirstDayIndex && firstDayIndex <= rightMinimumFirstDayIndex;
            } else {
                int firstDayIndex = nextRight.getFirstDayIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstDayIndex > rightMinimumFirstDayIndex && firstDayIndex <= leftMinimumFirstDayIndex;
            }
        }

        @Override
        public AssignmentSequence next() {
            lastNextWasLeft = nextIsLeft();
            // Buffer the nextLeft or nextRight
            AssignmentSequence lowest;
            if (lastNextWasLeft) {
                lowest = nextLeft;
                if (leftIterator.hasNext()) {
                    nextLeft = leftIterator.next();
                } else {
                    leftHasNext = false;
                    nextLeft = null;
                }
            } else {
                lowest = nextRight;
                if (rightIterator.hasNext()) {
                    nextRight = rightIterator.next();
                } else {
                    rightHasNext = false;
                    nextRight = null;
                }
            }
            return lowest;
        }

        private boolean nextIsLeft() {
            boolean returnLeft;
            if (leftHasNext) {
                if (rightHasNext) {
                    int leftFirstDayIndex = nextLeft.getFirstDayIndex();
                    int rightFirstDayIndex = nextRight.getFirstDayIndex();
                    returnLeft = leftFirstDayIndex <= rightFirstDayIndex;
                } else {
                    returnLeft = true;
                }
            } else {
                if (rightHasNext) {
                    returnLeft = false;
                } else {
                    throw new NoSuchElementException();
                }
            }
            return returnLeft;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

        public boolean isLastNextWasLeft() {
            return lastNextWasLeft;
        }

    }

}
