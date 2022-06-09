package org.optaplanner.examples.nurserostering.domain.solver;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;

import org.optaplanner.examples.nurserostering.domain.Employee;

public class EmployeeStrengthComparator implements Comparator<Employee> {

    private static final Comparator<Employee> COMPARATOR = comparingInt((Employee employee) -> -employee.getWeekendLength()) // Descending
            .thenComparingLong(Employee::getId);

    @Override
    public int compare(Employee a, Employee b) {
        // TODO refactor to DifficultyWeightFactory and use getContract().getContractLineList()
        //  to sum maximumValue and minimumValue etc
        return COMPARATOR.compare(a, b);
    }

}
