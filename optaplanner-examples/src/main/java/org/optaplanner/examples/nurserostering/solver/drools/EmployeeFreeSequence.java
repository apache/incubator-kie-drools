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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.nurserostering.domain.Employee;

public class EmployeeFreeSequence implements Comparable<EmployeeFreeSequence>, Serializable {

    private Employee employee;
    private int firstDayIndex;
    private int lastDayIndex;

    public EmployeeFreeSequence(Employee employee, int firstDayIndex, int lastDayIndex) {
        this.employee = employee;
        this.firstDayIndex = firstDayIndex;
        this.lastDayIndex = lastDayIndex;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getFirstDayIndex() {
        return firstDayIndex;
    }

    public void setFirstDayIndex(int firstDayIndex) {
        this.firstDayIndex = firstDayIndex;
    }

    public int getLastDayIndex() {
        return lastDayIndex;
    }

    public void setLastDayIndex(int lastDayIndex) {
        this.lastDayIndex = lastDayIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeFreeSequence) {
            EmployeeFreeSequence other = (EmployeeFreeSequence) o;
            return new EqualsBuilder()
                    .append(employee, other.employee)
                    .append(firstDayIndex, other.firstDayIndex)
                    .append(lastDayIndex, other.lastDayIndex)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(employee)
                .append(firstDayIndex)
                .append(lastDayIndex)
                .toHashCode();
    }

    @Override
    public int compareTo(EmployeeFreeSequence other) {
        return new CompareToBuilder()
                .append(employee, other.employee)
                .append(firstDayIndex, other.firstDayIndex)
                .append(lastDayIndex, other.lastDayIndex)
                .toComparison();
    }

    @Override
    public String toString() {
        return employee + " is free between " + firstDayIndex + " - " + lastDayIndex;
    }

    public int getDayLength() {
        return lastDayIndex - firstDayIndex + 1;
    }

}
