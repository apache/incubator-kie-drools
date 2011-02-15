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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.drools.planner.core.move.CompositeMove;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.solver.EmployeeWorkSequence;
import org.drools.planner.examples.nurserostering.solver.move.EmployeeChangeMove;

public class AssignmentSequenceSwitchLength3MoveFactory extends AbstractMoveFactory {

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
                AssignmentSequence assignmentSequence = new AssignmentSequence(assignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(assignment)) {
                    lastAssignmentSequence.add(assignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(assignment);
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
                List<AssignmentSequence> rightAssignmentSequenceList
                        = employeeToAssignmentSequenceListMap.get(rightEmployee);

                final int SWITCH_LENGTH = 3;
                for (AssignmentSequence leftAssignmentSequence : leftAssignmentSequenceList) {
                    List<Assignment> leftAssignmentList = leftAssignmentSequence.getAssignmentList();
                    for (int leftIndex = 0; leftIndex <= leftAssignmentList.size() - SWITCH_LENGTH; leftIndex++) {

                        for (AssignmentSequence rightAssignmentSequence : rightAssignmentSequenceList) {
                            List<Assignment> rightAssignmentList = rightAssignmentSequence.getAssignmentList();
                            for (int rightIndex = 0; rightIndex <= rightAssignmentList.size() - SWITCH_LENGTH; rightIndex++) {

                                List<Move> subMoveList = new ArrayList<Move>(SWITCH_LENGTH * 2);
                                for (Assignment leftAssignment : leftAssignmentList
                                        .subList(leftIndex, leftIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new EmployeeChangeMove(leftAssignment, rightEmployee));
                                }
                                for (Assignment rightAssignment : rightAssignmentList
                                        .subList(rightIndex, rightIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new EmployeeChangeMove(rightAssignment, leftEmployee));
                                }
                                moveList.add(new CompositeMove(subMoveList));
                            }
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link EmployeeWorkSequence}
     */
    private static class AssignmentSequence {

        private List<Assignment> assignmentList;
        private int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(Assignment assignment) {
            assignmentList = new ArrayList<Assignment>();
            assignmentList.add(assignment);
            firstDayIndex = assignment.getShiftDateDayIndex();
            lastDayIndex = firstDayIndex;
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

}
