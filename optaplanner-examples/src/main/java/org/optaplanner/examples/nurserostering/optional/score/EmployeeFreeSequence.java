package org.optaplanner.examples.nurserostering.optional.score;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;

public class EmployeeFreeSequence implements Comparable<EmployeeFreeSequence> {

    private static final Comparator<EmployeeFreeSequence> COMPARATOR = Comparator.comparing(EmployeeFreeSequence::getEmployee)
            .thenComparingInt(EmployeeFreeSequence::getFirstDayIndex)
            .thenComparingInt(EmployeeFreeSequence::getLastDayIndex);

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
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeFreeSequence other = (EmployeeFreeSequence) o;
        return Objects.equals(employee, other.employee) &&
                firstDayIndex == other.firstDayIndex &&
                lastDayIndex == other.lastDayIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, firstDayIndex, lastDayIndex);
    }

    @Override
    public int compareTo(EmployeeFreeSequence other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " is free between " + firstDayIndex + " - " + lastDayIndex;
    }

    public int getDayLength() {
        return lastDayIndex - firstDayIndex + 1;
    }

}
