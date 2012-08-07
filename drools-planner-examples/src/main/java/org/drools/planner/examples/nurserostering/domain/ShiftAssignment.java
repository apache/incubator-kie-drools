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

package org.drools.planner.examples.nurserostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.entity.PlanningEntity;
import org.drools.planner.api.domain.variable.PlanningVariable;
import org.drools.planner.api.domain.variable.ValueRange;
import org.drools.planner.api.domain.variable.ValueRangeType;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.nurserostering.domain.contract.Contract;
import org.drools.planner.examples.nurserostering.domain.solver.MovableShiftAssignmentSelectionFilter;
import org.drools.planner.examples.nurserostering.domain.solver.ShiftAssignmentDifficultyComparator;
import org.drools.planner.examples.nurserostering.domain.solver.EmployeeStrengthComparator;

@PlanningEntity(difficultyComparatorClass = ShiftAssignmentDifficultyComparator.class,
        movableEntitySelectionFilter = MovableShiftAssignmentSelectionFilter.class)
@XStreamAlias("ShiftAssignment")
public class ShiftAssignment extends AbstractPersistable {

    private Shift shift;
    private int indexInShift;

    // Planning variables: changes during planning, between score calculations.
    private Employee employee;

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public int getIndexInShift() {
        return indexInShift;
    }

    public void setIndexInShift(int indexInShift) {
        this.indexInShift = indexInShift;
    }

    @PlanningVariable(strengthComparatorClass = EmployeeStrengthComparator.class)
    @ValueRange(type = ValueRangeType.FROM_SOLUTION_PROPERTY, solutionProperty = "employeeList")
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public ShiftDate getShiftDate() {
        return shift.getShiftDate();
    }

    public ShiftType getShiftType() {
        return shift.getShiftType();
    }

    public int getShiftDateDayIndex() {
        return shift.getShiftDate().getDayIndex();
    }

    public DayOfWeek getShiftDateDayOfWeek() {
        return shift.getShiftDate().getDayOfWeek();
    }

    public Contract getContract() {
        if (employee == null) {
            return null;
        }
        return employee.getContract();
    }

    public boolean isWeekend() {
        if (employee == null) {
            return false;
        }
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shift.getShiftDate().getDayOfWeek();
        return weekendDefinition.isWeekend(dayOfWeek);
    }

    public int getWeekendSundayIndex() {
        return shift.getShiftDate().getWeekendSundayIndex();
    }

    public ShiftAssignment clone() {
        ShiftAssignment clone = new ShiftAssignment();
        clone.id = id;
        clone.shift = shift;
        clone.indexInShift = indexInShift;
        clone.employee = employee;
        return clone;
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ShiftAssignment) {
            ShiftAssignment other = (ShiftAssignment) o;
            return new EqualsBuilder()
                    .append(id, other.id)
                    .append(shift, other.shift)
                    .append(employee, other.employee)
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(shift)
                .append(employee)
                .toHashCode();
    }

    @Override
    public String toString() {
        return shift + "->" + employee;
    }

}
