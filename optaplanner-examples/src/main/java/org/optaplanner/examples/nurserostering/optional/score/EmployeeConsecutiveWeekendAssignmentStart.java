package org.optaplanner.examples.nurserostering.optional.score;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class EmployeeConsecutiveWeekendAssignmentStart implements Comparable<EmployeeConsecutiveWeekendAssignmentStart> {

    private static final Comparator<EmployeeConsecutiveWeekendAssignmentStart> COMPARATOR = Comparator
            .comparing(EmployeeConsecutiveWeekendAssignmentStart::getEmployee)
            .thenComparingInt(EmployeeConsecutiveWeekendAssignmentStart::getSundayIndex);

    private Employee employee;
    private int sundayIndex;

    public EmployeeConsecutiveWeekendAssignmentStart(Employee employee, int sundayIndex) {
        this.employee = employee;
        this.sundayIndex = sundayIndex;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getSundayIndex() {
        return sundayIndex;
    }

    public void setSundayIndex(int sundayIndex) {
        this.sundayIndex = sundayIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EmployeeConsecutiveWeekendAssignmentStart other = (EmployeeConsecutiveWeekendAssignmentStart) o;
        return Objects.equals(employee, other.employee) &&
                sundayIndex == other.sundayIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employee, sundayIndex);
    }

    @Override
    public int compareTo(EmployeeConsecutiveWeekendAssignmentStart other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return employee + " weekend " + sundayIndex + " - ...";
    }

    public Contract getContract() {
        return employee.getContract();
    }

}
