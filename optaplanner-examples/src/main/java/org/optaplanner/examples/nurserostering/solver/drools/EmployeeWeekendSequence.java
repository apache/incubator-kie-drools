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

public class EmployeeWeekendSequence implements Comparable<EmployeeWeekendSequence>, Serializable {

    private Employee employee;
    private int firstSundayIndex;
    private int lastSundayIndex;

    public EmployeeWeekendSequence(Employee employee, int firstSundayIndex, int lastSundayIndex) {
        this.employee = employee;
        this.firstSundayIndex = firstSundayIndex;
        this.lastSundayIndex = lastSundayIndex;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getFirstSundayIndex() {
        return firstSundayIndex;
    }

    public void setFirstSundayIndex(int firstSundayIndex) {
        this.firstSundayIndex = firstSundayIndex;
    }

    public int getLastSundayIndex() {
        return lastSundayIndex;
    }

    public void setLastSundayIndex(int lastSundayIndex) {
        this.lastSundayIndex = lastSundayIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof EmployeeWeekendSequence) {
            EmployeeWeekendSequence other = (EmployeeWeekendSequence) o;
            return new EqualsBuilder()
                    .append(employee, other.employee)
                    .append(firstSundayIndex, other.firstSundayIndex)
                    .append(lastSundayIndex, other.lastSundayIndex)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(employee)
                .append(firstSundayIndex)
                .append(lastSundayIndex)
                .toHashCode();
    }

    @Override
    public int compareTo(EmployeeWeekendSequence other) {
        return new CompareToBuilder()
                .append(employee, other.employee)
                .append(firstSundayIndex, other.firstSundayIndex)
                .append(lastSundayIndex, other.lastSundayIndex)
                .toComparison();
    }

    @Override
    public String toString() {
        return employee + " is working the weekend of " + firstSundayIndex + " - " + lastSundayIndex;
    }

    public int getWeekendLength() {
        return ((lastSundayIndex - firstSundayIndex) / 7) + 1;
    }

}
