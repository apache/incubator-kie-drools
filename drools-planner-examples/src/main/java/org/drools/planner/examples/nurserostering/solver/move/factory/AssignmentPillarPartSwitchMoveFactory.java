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

package org.drools.planner.examples.nurserostering.solver.move.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.drools.planner.core.move.CompositeMove;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.solver.EmployeeWorkSequence;
import org.drools.planner.examples.nurserostering.solver.move.EmployeeMultipleChangeMove;

public class AssignmentPillarPartSwitchMoveFactory extends AbstractMoveFactory {

    public List<Move> createMoveList(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        List<Employee> employeeList = nurseRoster.getEmployeeList();
        // This code assumes the assignmentList is sorted
        List<Assignment> assignmentList = nurseRoster.getAssignmentList();

        // Hash the assignments per employee
        Map<Employee, List<AssignmentSequence>> employeeToAssignmentSequenceListMap
                = new HashMap<Employee, List<AssignmentSequence>>(employeeList.size());
        int assignmentSequenceCapacity = nurseRoster.getShiftDateList().size() + 1 / 2;
        for (Employee employee : employeeList) {
            employeeToAssignmentSequenceListMap.put(employee,
                    new ArrayList<AssignmentSequence>(assignmentSequenceCapacity));
        }
        for (Assignment assignment : assignmentList) {
            Employee employee = assignment.getEmployee();
            List<AssignmentSequence> assignmentSequenceList = employeeToAssignmentSequenceListMap.get(employee);
            if (assignmentSequenceList.isEmpty()) {
                AssignmentSequence assignmentSequence = new AssignmentSequence(employee, assignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(assignment)) {
                    lastAssignmentSequence.add(assignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(employee, assignment);
                    assignmentSequenceList.add(assignmentSequence);
                }
            }
        }

        // The create the move list
        List<Move> moveList = new ArrayList<Move>();
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
                    // Note: the initialCapacity is probably to high,
                    // which is bad for memory, but the opposite is bad for performance (which is worse)
                    List<Move> moveListByPillarPartDuo = new ArrayList<Move>(
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
                            pillarPartAssignmentSequence.getAssignmentList(),
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
                                pillarPartAssignmentSequence.getAssignmentList(),
                                otherEmployee));
                    }
                    moveList.add(new CompositeMove(moveListByPillarPartDuo));
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link EmployeeWorkSequence}
     */
    private static class AssignmentSequence {

        private Employee employee;
        private List<Assignment> assignmentList;
        private int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(Employee employee, Assignment assignment) {
            this.employee = employee;
            assignmentList = new ArrayList<Assignment>();
            assignmentList.add(assignment);
            firstDayIndex = assignment.getShiftDateDayIndex();
            lastDayIndex = firstDayIndex;
        }

        public Employee getEmployee() {
            return employee;
        }

        public List<Assignment> getAssignmentList() {
            return assignmentList;
        }

        public int getFirstDayIndex() {
            return firstDayIndex;
        }

        public int getLastDayIndex() {
            return lastDayIndex;
        }

        private void add(Assignment assignment) {
            assignmentList.add(assignment);
            int dayIndex = assignment.getShiftDateDayIndex();
            if (dayIndex < lastDayIndex) {
                throw new IllegalStateException("The assignmentList is expected to be sorted by shiftDate.");
            }
            lastDayIndex = dayIndex;
        }

        private boolean belongsHere(Assignment assignment) {
            return assignment.getShiftDateDayIndex() <= (lastDayIndex + 1);
        }

    }

    private class LowestDayIndexAssignmentSequenceIterator implements Iterator<AssignmentSequence> {

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

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported.");
        }

        public boolean isLastNextWasLeft() {
            return lastNextWasLeft;
        }

    }

}
