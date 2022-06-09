package org.optaplanner.examples.nurserostering.domain;

import java.time.DayOfWeek;
import java.util.Comparator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.solver.EmployeeStrengthComparator;
import org.optaplanner.examples.nurserostering.domain.solver.ShiftAssignmentDifficultyComparator;
import org.optaplanner.examples.nurserostering.domain.solver.ShiftAssignmentPinningFilter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(pinningFilter = ShiftAssignmentPinningFilter.class,
        difficultyComparatorClass = ShiftAssignmentDifficultyComparator.class)
@XStreamAlias("ShiftAssignment")
public class ShiftAssignment extends AbstractPersistable implements Comparable<ShiftAssignment> {

    private static final Comparator<Shift> COMPARATOR = Comparator.comparing(Shift::getShiftDate)
            .thenComparing(a -> a.getShiftType().getStartTimeString())
            .thenComparing(a -> a.getShiftType().getEndTimeString());

    private Shift shift;
    private int indexInShift;

    // Planning variables: changes during planning, between score calculations.
    @PlanningVariable(valueRangeProviderRefs = { "employeeRange" }, strengthComparatorClass = EmployeeStrengthComparator.class)
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

    @Override
    public String toString() {
        return shift.toString();
    }

    @Override
    public int compareTo(ShiftAssignment o) {
        return COMPARATOR.compare(shift, o.shift);
    }
}
