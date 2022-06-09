package org.optaplanner.examples.nurserostering.optional.score;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;

public class EmployeeWorkSequence implements Comparable<EmployeeWorkSequence> {

    private static final Comparator<EmployeeWorkSequence> COMPARATOR = Comparator.comparing(EmployeeWorkSequence::getEmployee)
            .thenComparingInt(EmployeeWorkSequence::getFirstDayIndex)
            .thenComparingInt(EmployeeWorkSequence::getLastDayIndex);

    private Employee employee;
    private int firstDayIndex;
    private int lastDayIndex;

    public EmployeeWorkSequence(Employee employee, int firstDayIndex, int lastDayIndex) {
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
        final EmployeeWorkSequence other = (EmployeeWorkSequence) o;
        return Objects.equals(employee, other.employee) &&
                firstDayIndex == other.firstDayIndex &&
                lastDayIndex == other.lastDayIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, firstDayIndex, lastDayIndex);
    }

    @Override
    public int compareTo(EmployeeWorkSequence other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " is working between " + firstDayIndex + " - " + lastDayIndex;
    }

    public int getDayLength() {
        return lastDayIndex - firstDayIndex + 1;
    }

}
