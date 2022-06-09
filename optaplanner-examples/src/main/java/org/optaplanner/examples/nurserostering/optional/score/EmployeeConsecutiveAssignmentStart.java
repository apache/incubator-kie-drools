package org.optaplanner.examples.nurserostering.optional.score;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class EmployeeConsecutiveAssignmentStart implements Comparable<EmployeeConsecutiveAssignmentStart> {

    public static boolean isWeekendAndNotFirstDayOfWeekend(Employee employee, ShiftDate shiftDate) {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        return weekendDefinition.isWeekend(dayOfWeek) && weekendDefinition.getFirstDayOfWeekend() != dayOfWeek;
    }

    public static int getDistanceToFirstDayOfWeekend(Employee employee, ShiftDate shiftDate) {
        WeekendDefinition weekendDefinition = employee.getContract().getWeekendDefinition();
        DayOfWeek dayOfWeek = shiftDate.getDayOfWeek();
        DayOfWeek firstDayOfWeekend = weekendDefinition.getFirstDayOfWeekend();
        int distance = dayOfWeek.getValue() - firstDayOfWeekend.getValue();
        if (distance < 0) {
            distance += 7;
        }
        return distance;
    }

    private static final Comparator<EmployeeConsecutiveAssignmentStart> COMPARATOR = Comparator
            .comparing(EmployeeConsecutiveAssignmentStart::getEmployee)
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
        return isWeekendAndNotFirstDayOfWeekend(employee, shiftDate);
    }

    public int getDistanceToFirstDayOfWeekend() {
        return getDistanceToFirstDayOfWeekend(employee, shiftDate);
    }

}
