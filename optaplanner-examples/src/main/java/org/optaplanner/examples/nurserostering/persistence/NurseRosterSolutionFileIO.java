package org.optaplanner.examples.nurserostering.persistence;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;

public class NurseRosterSolutionFileIO extends AbstractJsonSolutionFileIO<NurseRoster> {

    public NurseRosterSolutionFileIO() {
        super(NurseRoster.class);
    }

    @Override
    public NurseRoster read(File inputSolutionFile) {
        NurseRoster nurseRoster = super.read(inputSolutionFile);

        /*
         * Replace the duplicate Shift/ShiftDate instances by references to instances from the shiftList/shiftDateList.
         */
        var requestsById = nurseRoster.getShiftDateList().stream()
                .collect(Collectors.toMap(ShiftDate::getId, Function.identity()));
        var shiftsById = nurseRoster.getShiftList().stream()
                .collect(Collectors.toMap(Shift::getId, Function.identity()));
        for (Employee employee : nurseRoster.getEmployeeList()) {
            employee.setDayOffRequestMap(deduplicateMap(employee.getDayOffRequestMap(), requestsById, ShiftDate::getId));
            employee.setDayOnRequestMap(deduplicateMap(employee.getDayOnRequestMap(), requestsById, ShiftDate::getId));
            employee.setShiftOffRequestMap(deduplicateMap(employee.getShiftOffRequestMap(), shiftsById, Shift::getId));
            employee.setShiftOnRequestMap(deduplicateMap(employee.getShiftOnRequestMap(), shiftsById, Shift::getId));
        }

        return nurseRoster;
    }

}
