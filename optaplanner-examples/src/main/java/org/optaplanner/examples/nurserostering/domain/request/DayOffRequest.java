package org.optaplanner.examples.nurserostering.domain.request;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class DayOffRequest extends AbstractPersistable {

    private Employee employee;
    private ShiftDate shiftDate;
    private int weight;

    public DayOffRequest() {
    }

    public DayOffRequest(long id, Employee employee, ShiftDate shiftDate, int weight) {
        super(id);
        this.employee = employee;
        this.shiftDate = shiftDate;
        this.weight = weight;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return shiftDate + "_OFF_" + employee;
    }

}
