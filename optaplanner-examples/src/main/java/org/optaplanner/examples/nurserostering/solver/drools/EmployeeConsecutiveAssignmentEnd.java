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

public class EmployeeConsecutiveAssignmentEnd implements Comparable<EmployeeConsecutiveAssignmentEnd>, Serializable {

    private static final Comparator<EmployeeConsecutiveAssignmentEnd> COMPARATOR =
            Comparator.comparing(EmployeeConsecutiveAssignmentEnd::getEmployee)
                    .thenComparing(EmployeeConsecutiveAssignmentEnd::getShiftDate);

    private Employee employee;
    private ShiftDate shiftDate;

    public EmployeeConsecutiveAssignmentEnd(Employee employee, ShiftDate shiftDate) {
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
        final EmployeeConsecutiveAssignmentEnd other = (EmployeeConsecutiveAssignmentEnd) o;
        return Objects.equals(employee, other.employee) &&
                Objects.equals(shiftDate, other.shiftDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, shiftDate);
    }

    @Override
    public int compareTo(EmployeeConsecutiveAssignmentEnd other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " ... - " + shiftDate;
    }

    public Contract getContract() {
        return employee.getContract();
    }

    public int getShiftDateDayIndex() {
        return shiftDate.getDayIndex();
    }

    public boolean isWeekendAndNotLastDayOfWeekend() {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        return weekendDefinition.isWeekend(dayOfWeek) && weekendDefinition.getLastDayOfWeekend() != dayOfWeek;
    }

    public int getDistanceToLastDayOfWeekend() {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        DayOfWeek lastDayOfWeekend = weekendDefinition.getLastDayOfWeekend();
        int distance = lastDayOfWeekend.getValue() - dayOfWeek.getValue();
        if (distance < 0) {
            distance += 7;
        }
        return distance;
    }

}
