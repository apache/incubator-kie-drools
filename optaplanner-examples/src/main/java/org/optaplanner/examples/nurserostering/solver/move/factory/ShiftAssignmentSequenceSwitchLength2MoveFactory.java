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

import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.solver.MovableShiftAssignmentSelectionFilter;
import org.optaplanner.examples.nurserostering.solver.drools.EmployeeWorkSequence;
import org.optaplanner.examples.nurserostering.solver.move.EmployeeChangeMove;

public class ShiftAssignmentSequenceSwitchLength2MoveFactory implements MoveListFactory<NurseRoster> {

    private MovableShiftAssignmentSelectionFilter filter = new MovableShiftAssignmentSelectionFilter();

    @Override
    public List<Move<NurseRoster>> createMoveList(NurseRoster nurseRoster) {
        List<Employee> employeeList = nurseRoster.getEmployeeList();
        // This code assumes the shiftAssignmentList is sorted
        // Filter out every immovable ShiftAssignment
        List<ShiftAssignment> shiftAssignmentList = new ArrayList<>(
                nurseRoster.getShiftAssignmentList());
        for (Iterator<ShiftAssignment> it = shiftAssignmentList.iterator(); it.hasNext(); ) {
            ShiftAssignment shiftAssignment = it.next();
            if (!filter.accept(nurseRoster, shiftAssignment)) {
                it.remove();
            }
        }

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
                AssignmentSequence assignmentSequence = new AssignmentSequence(shiftAssignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(shiftAssignment)) {
                    lastAssignmentSequence.add(shiftAssignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(shiftAssignment);
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
                List<AssignmentSequence> rightAssignmentSequenceList
                        = employeeToAssignmentSequenceListMap.get(rightEmployee);

                final int SWITCH_LENGTH = 2;
                for (AssignmentSequence leftAssignmentSequence : leftAssignmentSequenceList) {
                    List<ShiftAssignment> leftShiftAssignmentList = leftAssignmentSequence.getShiftAssignmentList();
                    for (int leftIndex = 0; leftIndex <= leftShiftAssignmentList.size() - SWITCH_LENGTH; leftIndex++) {

                        for (AssignmentSequence rightAssignmentSequence : rightAssignmentSequenceList) {
                            List<ShiftAssignment> rightShiftAssignmentList = rightAssignmentSequence.getShiftAssignmentList();
                            for (int rightIndex = 0; rightIndex <= rightShiftAssignmentList.size() - SWITCH_LENGTH; rightIndex++) {

                                List<EmployeeChangeMove> subMoveList = new ArrayList<>(SWITCH_LENGTH * 2);
                                for (ShiftAssignment leftShiftAssignment : leftShiftAssignmentList
                                        .subList(leftIndex, leftIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new EmployeeChangeMove(leftShiftAssignment, rightEmployee));
                                }
                                for (ShiftAssignment rightShiftAssignment : rightShiftAssignmentList
                                        .subList(rightIndex, rightIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new EmployeeChangeMove(rightShiftAssignment, leftEmployee));
                                }
                                moveList.add(CompositeMove.buildMove(subMoveList));
                            }
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link EmployeeWorkSequence}.
     */
    private static class AssignmentSequence {

        private List<ShiftAssignment> shiftAssignmentList;
        private int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(ShiftAssignment shiftAssignment) {
            shiftAssignmentList = new ArrayList<>();
            shiftAssignmentList.add(shiftAssignment);
            firstDayIndex = shiftAssignment.getShiftDateDayIndex();
            lastDayIndex = firstDayIndex;
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

}
