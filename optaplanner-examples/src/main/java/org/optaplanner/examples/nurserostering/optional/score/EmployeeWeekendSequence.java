package org.optaplanner.examples.nurserostering.optional.score;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;

public class EmployeeWeekendSequence implements Comparable<EmployeeWeekendSequence> {

    private static final Comparator<EmployeeWeekendSequence> COMPARATOR = Comparator
            .comparing(EmployeeWeekendSequence::getEmployee)
            .thenComparingInt(EmployeeWeekendSequence::getFirstSundayIndex)
            .thenComparingInt(EmployeeWeekendSequence::getLastSundayIndex);

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
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeWeekendSequence other = (EmployeeWeekendSequence) o;
        return Objects.equals(employee, other.employee) &&
                firstSundayIndex == other.firstSundayIndex &&
                lastSundayIndex == other.lastSundayIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, firstSundayIndex, lastSundayIndex);
    }

    @Override
    public int compareTo(EmployeeWeekendSequence other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " is working the weekend of " + firstSundayIndex + " - " + lastSundayIndex;
    }

    public int getWeekendLength() {
        return ((lastSundayIndex - firstSundayIndex) / 7) + 1;
    }

}
