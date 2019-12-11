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

package org.optaplanner.examples.nurserostering.solver.drools;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class EmployeeConsecutiveAssignmentStart implements Comparable<EmployeeConsecutiveAssignmentStart>,
        Serializable {

    private static final Comparator<EmployeeConsecutiveAssignmentStart> COMPARATOR =
            Comparator.comparing(EmployeeConsecutiveAssignmentStart::getEmployee)
                    .thenComparing(EmployeeConsecutiveAssignmentStart::getShiftDate);

    private Employee employee;
    private ShiftDate shiftDate;

    public EmployeeConsecutiveAssignmentStart(Employee employee, ShiftDate shiftDate) {
        this.employee = employee;
        this.shiftDate = shiftDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public ShiftDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(ShiftDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeConsecutiveAssignmentStart other = (EmployeeConsecutiveAssignmentStart) o;
        return Objects.equals(employee, other.employee) &&
                Objects.equals(shiftDate, other.shiftDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, shiftDate);
    }

    @Override
    public int compareTo(EmployeeConsecutiveAssignmentStart other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " " + shiftDate + " - ...";
    }

    public Contract getContract() {
        return employee.getContract();
    }

    public int getShiftDateDayIndex() {
        return shiftDate.getDayIndex();
    }

    public boolean isWeekendAndNotFirstDayOfWeekend() {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        return weekendDefinition.isWeekend(dayOfWeek) && weekendDefinition.getFirstDayOfWeekend() != dayOfWeek;
    }

    public int getDistanceToFirstDayOfWeekend() {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        DayOfWeek firstDayOfWeekend = weekendDefinition.getFirstDayOfWeekend();
        int distance = dayOfWeek.getValue() - firstDayOfWeekend.getValue();
        if (distance < 0) {
            distance += 7;
        }
        return distance;
    }

}
