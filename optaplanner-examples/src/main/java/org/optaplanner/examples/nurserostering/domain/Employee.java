package org.optaplanner.examples.nurserostering.domain;

import java.util.Map;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;
import org.optaplanner.examples.common.persistence.jackson.KeySerializer;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOnRequest;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class Employee extends AbstractPersistable implements Labeled, Comparable<Employee> {

    private String code;
    private String name;
    private Contract contract;

    public Employee() {
    }

    public Employee(long id, String code, String name, Contract contract) {
        super(id);
        this.code = code;
        this.name = name;
        this.contract = contract;
    }

    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = ShiftDateKeyDeserializer.class)
    private Map<ShiftDate, DayOffRequest> dayOffRequestMap;
    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = ShiftDateKeyDeserializer.class)
    private Map<ShiftDate, DayOnRequest> dayOnRequestMap;
    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = ShiftKeyDeserializer.class)
    private Map<Shift, ShiftOffRequest> shiftOffRequestMap;
    @JsonSerialize(keyUsing = KeySerializer.class)
    @JsonDeserialize(keyUsing = ShiftKeyDeserializer.class)
    private Map<Shift, ShiftOnRequest> shiftOnRequestMap;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    @JsonIgnore
    public int getWeekendLength() {
        return getContract().getWeekendLength();
    }

    public Map<ShiftDate, DayOffRequest> getDayOffRequestMap() {
        return dayOffRequestMap;
    }

    public void setDayOffRequestMap(Map<ShiftDate, DayOffRequest> dayOffRequestMap) {
        this.dayOffRequestMap = dayOffRequestMap;
    }

    public Map<ShiftDate, DayOnRequest> getDayOnRequestMap() {
        return dayOnRequestMap;
    }

    public void setDayOnRequestMap(Map<ShiftDate, DayOnRequest> dayOnRequestMap) {
        this.dayOnRequestMap = dayOnRequestMap;
    }

    public Map<Shift, ShiftOffRequest> getShiftOffRequestMap() {
        return shiftOffRequestMap;
    }

    public void setShiftOffRequestMap(Map<Shift, ShiftOffRequest> shiftOffRequestMap) {
        this.shiftOffRequestMap = shiftOffRequestMap;
    }

    public Map<Shift, ShiftOnRequest> getShiftOnRequestMap() {
        return shiftOnRequestMap;
    }

    public void setShiftOnRequestMap(Map<Shift, ShiftOnRequest> shiftOnRequestMap) {
        this.shiftOnRequestMap = shiftOnRequestMap;
    }

    @Override
    public String getLabel() {
        return "Employee " + name;
    }

    @Override
    public String toString() {
        if (name == null) {
            return super.toString();
        }
        return name;
    }

    @Override
    public int compareTo(Employee employee) {
        return name.compareTo(employee.name);
    }
}
