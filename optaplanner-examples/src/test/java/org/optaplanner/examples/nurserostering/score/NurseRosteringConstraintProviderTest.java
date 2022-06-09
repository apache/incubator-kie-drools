package org.optaplanner.examples.nurserostering.score;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.examples.common.util.Pair;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftType;
import org.optaplanner.examples.nurserostering.domain.ShiftTypeSkillRequirement;
import org.optaplanner.examples.nurserostering.domain.Skill;
import org.optaplanner.examples.nurserostering.domain.SkillProficiency;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.examples.nurserostering.domain.contract.BooleanContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLineType;
import org.optaplanner.examples.nurserostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.PatternContractLine;
import org.optaplanner.examples.nurserostering.domain.pattern.FreeBefore2DaysWithAWorkDayPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.ShiftType2DaysPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.ShiftType3DaysPattern;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOnRequest;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class NurseRosteringConstraintProviderTest {
    private final ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier =
            ConstraintVerifier.build(new NurseRosteringConstraintProvider(), NurseRoster.class,
                    ShiftAssignment.class);

    private final AtomicLong idSupplier = new AtomicLong(0);
    private final Map<Pair<Integer, ShiftType>, Shift> indexShiftTypePairToShiftMap = new HashMap<>();
    private final Map<Integer, ShiftDate> indexToShiftDateMap = new HashMap<>();
    private final ShiftType dayShiftType = new ShiftType();
    private final ShiftType nightShiftType = new ShiftType();

    @BeforeEach
    void setup() {
        idSupplier.set(0);
        indexShiftTypePairToShiftMap.clear();
        indexToShiftDateMap.clear();

        dayShiftType.setId(idSupplier.incrementAndGet());
        dayShiftType.setNight(false);
        dayShiftType.setStartTimeString("09:00");
        dayShiftType.setEndTimeString("17:00");
        dayShiftType.setCode("ShiftType - Day");
        dayShiftType.setIndex(0);
        dayShiftType.setDescription("Day Shift");

        nightShiftType.setId(idSupplier.incrementAndGet());
        nightShiftType.setNight(true);
        nightShiftType.setStartTimeString("07:00");
        nightShiftType.setEndTimeString("04:00");
        nightShiftType.setCode("ShiftType - Night");
        nightShiftType.setIndex(1);
        nightShiftType.setDescription("Night Shift");
    }

    // ******************************************************
    // Model Factories
    // ******************************************************
    private class MinMaxContractBuilder {
        private Integer minimumValue;
        private Integer maximumValue;
        private Integer minimumWeight;
        private Integer maximumWeight;
        private ContractLineType contractLineType;
        private WeekendDefinition weekendDefinition;

        public MinMaxContractBuilder(ContractLineType contractLineType) {
            this.contractLineType = contractLineType;
            this.weekendDefinition = WeekendDefinition.SATURDAY_SUNDAY;
            this.minimumWeight = 1;
            this.maximumWeight = 1;
        }

        public MinMaxContractBuilder withMinimum(Integer minimum) {
            this.minimumValue = minimum;
            return this;
        }

        public MinMaxContractBuilder withMaximum(Integer maximum) {
            this.maximumValue = maximum;
            return this;
        }

        public MinMaxContractBuilder withMinimumWeight(Integer weight) {
            this.minimumWeight = weight;
            return this;
        }

        public MinMaxContractBuilder withMaximumWeight(Integer weight) {
            this.maximumWeight = weight;
            return this;
        }

        public MinMaxContractBuilder withWeekendDefinition(WeekendDefinition weekendDefinition) {
            this.weekendDefinition = weekendDefinition;
            return this;
        }

        public Contract build() {
            MinMaxContractLine contractLine = new MinMaxContractLine();
            contractLine.setId(idSupplier.incrementAndGet());
            contractLine.setContractLineType(contractLineType);
            if (minimumValue != null) {
                contractLine.setMinimumValue(minimumValue);
                contractLine.setMinimumEnabled(true);
                contractLine.setMinimumWeight(minimumWeight);
            }
            if (maximumValue != null) {
                contractLine.setMaximumValue(maximumValue);
                contractLine.setMaximumEnabled(true);
                contractLine.setMaximumWeight(maximumWeight);
            }

            Contract out = new Contract();
            out.setId(idSupplier.incrementAndGet());
            out.setContractLineList(Collections.singletonList(contractLine));
            contractLine.setContract(out);

            out.setCode("Contract - " + out.getId());
            out.setDescription("Minimum/Maximum " + contractLineType + " Contract");
            out.setWeekendDefinition(weekendDefinition);
            return out;
        }
    }

    private class BooleanContractBuilder {
        private final ContractLineType contractLineType;
        private final WeekendDefinition weekendDefinition;
        int weight;
        boolean enabled;

        public BooleanContractBuilder(ContractLineType contractLineType) {
            this.contractLineType = contractLineType;
            this.weekendDefinition = WeekendDefinition.SATURDAY_SUNDAY;
            this.weight = 1;
            this.enabled = true;
        }

        public BooleanContractBuilder withWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public BooleanContractBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Contract build() {
            BooleanContractLine contractLine = new BooleanContractLine();
            contractLine.setId(idSupplier.incrementAndGet());
            contractLine.setContractLineType(contractLineType);
            contractLine.setWeight(weight);
            contractLine.setEnabled(enabled);

            Contract out = new Contract();
            out.setId(idSupplier.incrementAndGet());
            out.setContractLineList(Collections.singletonList(contractLine));
            contractLine.setContract(out);

            out.setCode("Contract - " + out.getId());
            out.setDescription("Boolean " + contractLineType + " Contract");
            out.setWeekendDefinition(weekendDefinition);
            return out;
        }
    }

    private class PatternContractBuilder {
        FreeBefore2DaysWithAWorkDayPattern freeBefore2DaysWithAWorkDayPattern;
        ShiftType2DaysPattern shiftType2DaysPattern;
        ShiftType3DaysPattern shiftType3DaysPattern;
        private WeekendDefinition weekendDefinition = WeekendDefinition.SATURDAY_SUNDAY;
        int weight = 1;

        public PatternContractBuilder freeBefore2DaysWithAWorkDay(DayOfWeek workDay) {
            freeBefore2DaysWithAWorkDayPattern = new FreeBefore2DaysWithAWorkDayPattern();
            freeBefore2DaysWithAWorkDayPattern.setFreeDayOfWeek(workDay);
            freeBefore2DaysWithAWorkDayPattern.setId(idSupplier.incrementAndGet());
            freeBefore2DaysWithAWorkDayPattern.setCode("Free Before 2 Days - " + workDay);
            return this;
        }

        public PatternContractBuilder shiftType2DaysPattern(ShiftType day0ShiftType, ShiftType day1ShiftType) {
            shiftType2DaysPattern = new ShiftType2DaysPattern();
            shiftType2DaysPattern.setId(idSupplier.incrementAndGet());
            shiftType2DaysPattern.setCode("Shift Type 2 Day Pattern - " + day0ShiftType + ", " + day1ShiftType);
            shiftType2DaysPattern.setDayIndex0ShiftType(day0ShiftType);
            shiftType2DaysPattern.setDayIndex1ShiftType(day1ShiftType);
            return this;
        }

        public PatternContractBuilder shiftType3DaysPattern(ShiftType day0ShiftType, ShiftType day1ShiftType,
                ShiftType day2ShiftType) {
            shiftType3DaysPattern = new ShiftType3DaysPattern();
            shiftType3DaysPattern.setId(idSupplier.incrementAndGet());
            shiftType3DaysPattern
                    .setCode("Shift Type 3 Day Pattern - " + day0ShiftType + ", " + day1ShiftType + ", " + day2ShiftType);
            shiftType3DaysPattern.setDayIndex0ShiftType(day0ShiftType);
            shiftType3DaysPattern.setDayIndex1ShiftType(day1ShiftType);
            shiftType3DaysPattern.setDayIndex2ShiftType(day2ShiftType);
            return this;
        }

        public Pair<PatternContractLine, Contract> build() {
            PatternContractLine patternContractLine = new PatternContractLine();
            if (freeBefore2DaysWithAWorkDayPattern != null) {
                patternContractLine.setPattern(freeBefore2DaysWithAWorkDayPattern);
            }
            if (shiftType2DaysPattern != null) {
                if (patternContractLine.getPattern() != null) {
                    throw new IllegalStateException("Multiple patterns are set on the builder");
                }
                patternContractLine.setPattern(shiftType2DaysPattern);
            }
            if (shiftType3DaysPattern != null) {
                if (patternContractLine.getPattern() != null) {
                    throw new IllegalStateException("Multiple patterns are set on the builder");
                }
                patternContractLine.setPattern(shiftType3DaysPattern);
            }
            if (patternContractLine.getPattern() == null) {
                throw new IllegalStateException("No patterns are set on the builder");
            }
            patternContractLine.setId(idSupplier.incrementAndGet());
            patternContractLine.getPattern().setWeight(weight);
            Contract out = new Contract();
            out.setId(idSupplier.incrementAndGet());
            // PatternContractLine does not extend ContractLine
            out.setContractLineList(Collections.emptyList());
            patternContractLine.setContract(out);

            out.setCode("Contract - " + out.getId());
            out.setDescription("Pattern " + patternContractLine + " Contract");
            out.setWeekendDefinition(weekendDefinition);
            return Pair.of(patternContractLine, out);
        }
    }

    private Employee getEmployee() {
        return getEmployee(new MinMaxContractBuilder(ContractLineType.TOTAL_ASSIGNMENTS).build());
    }

    private Employee getEmployee(Contract contract) {
        Employee employee = new Employee();
        employee.setContract(contract);
        employee.setId(idSupplier.incrementAndGet());
        employee.setName("Employee " + employee.getId());
        employee.setCode(employee.getName());
        employee.setDayOffRequestMap(new HashMap<>());
        employee.setDayOnRequestMap(new HashMap<>());
        employee.setShiftOffRequestMap(new HashMap<>());
        employee.setShiftOnRequestMap(new HashMap<>());
        return employee;
    }

    private ShiftDate getShiftDate(int dayIndex) {
        return indexToShiftDateMap.computeIfAbsent(dayIndex, key -> {
            ShiftDate shiftDate = new ShiftDate();
            shiftDate.setDayIndex(dayIndex);
            shiftDate.setId(idSupplier.incrementAndGet());
            shiftDate.setDate(LocalDate.of(2000, 1, 1).plusDays(dayIndex));
            shiftDate.setShiftList(new ArrayList<>());
            return shiftDate;
        });
    }

    private ShiftAssignment getShiftAssignment(int dayIndex, Employee employee) {
        return getShiftAssignment(dayIndex, employee, dayShiftType);
    }

    private ShiftAssignment getShiftAssignment(int dayIndex, Employee employee, ShiftType shiftType) {
        Shift shift = indexShiftTypePairToShiftMap.computeIfAbsent(Pair.of(dayIndex, shiftType), key -> {
            ShiftDate shiftDate = getShiftDate(dayIndex);

            Shift newShift = new Shift();
            newShift.setShiftType(shiftType);
            newShift.setId(idSupplier.incrementAndGet());
            newShift.setRequiredEmployeeSize(0);
            newShift.setIndex(0);
            newShift.setShiftDate(shiftDate);
            shiftDate.getShiftList().add(newShift);
            return newShift;
        });
        shift.setRequiredEmployeeSize(shift.getRequiredEmployeeSize() + 1);
        ShiftAssignment shiftAssignment = new ShiftAssignment();
        shiftAssignment.setId(idSupplier.incrementAndGet());
        shiftAssignment.setEmployee(employee);
        shiftAssignment.setIndexInShift(0);
        shiftAssignment.setShift(shift);
        return shiftAssignment;
    }

    private DayOffRequest getDayOffRequest(Employee employee, ShiftDate shiftDate, int weight) {
        DayOffRequest dayOffRequest = new DayOffRequest();
        dayOffRequest.setId(idSupplier.incrementAndGet());
        dayOffRequest.setEmployee(employee);
        dayOffRequest.setShiftDate(shiftDate);
        dayOffRequest.setWeight(weight);
        return dayOffRequest;
    }

    private DayOnRequest getDayOnRequest(Employee employee, ShiftDate shiftDate, int weight) {
        DayOnRequest dayOnRequest = new DayOnRequest();
        dayOnRequest.setId(idSupplier.incrementAndGet());
        dayOnRequest.setEmployee(employee);
        dayOnRequest.setShiftDate(shiftDate);
        dayOnRequest.setWeight(weight);
        return dayOnRequest;
    }

    private ShiftOffRequest getShiftOffRequest(Employee employee, Shift shift, int weight) {
        ShiftOffRequest shiftOffRequest = new ShiftOffRequest();
        shiftOffRequest.setId(idSupplier.incrementAndGet());
        shiftOffRequest.setEmployee(employee);
        shiftOffRequest.setShift(shift);
        shiftOffRequest.setWeight(weight);
        return shiftOffRequest;
    }

    private ShiftOnRequest getShiftOnRequest(Employee employee, Shift shift, int weight) {
        ShiftOnRequest shiftOnRequest = new ShiftOnRequest();
        shiftOnRequest.setId(idSupplier.incrementAndGet());
        shiftOnRequest.setEmployee(employee);
        shiftOnRequest.setShift(shift);
        shiftOnRequest.setWeight(weight);
        return shiftOnRequest;
    }

    private Skill getSkill(String name) {
        Skill skill = new Skill();
        skill.setId(idSupplier.incrementAndGet());
        skill.setCode("Skill - " + name);
        return skill;
    }

    private SkillProficiency getSkillProficiency(Employee employee, Skill skill) {
        SkillProficiency skillProficiency = new SkillProficiency();
        skillProficiency.setId(idSupplier.incrementAndGet());
        skillProficiency.setSkill(skill);
        skillProficiency.setEmployee(employee);
        return skillProficiency;
    }

    private ShiftTypeSkillRequirement getSkillRequirement(ShiftType shiftType, Skill skill) {
        ShiftTypeSkillRequirement skillRequirement = new ShiftTypeSkillRequirement();
        skillRequirement.setId(idSupplier.incrementAndGet());
        skillRequirement.setShiftType(shiftType);
        skillRequirement.setSkill(skill);
        return skillRequirement;
    }

    // ******************************************************
    // TESTS
    // ******************************************************
    @Test
    void oneShiftPerDay() {
        Employee employee = getEmployee();
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(0, employee);
        ShiftAssignment shift3 = getShiftAssignment(1, employee);
        ShiftAssignment shift4 = getShiftAssignment(2, employee);
        ShiftAssignment shift5 = getShiftAssignment(2, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::oneShiftPerDay)
                .given(shift1, shift2, shift3, shift4, shift5).penalizesBy(2);
    }

    @Test
    void minimumAndMaximumNumberOfAssignments() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.TOTAL_ASSIGNMENTS)
                .withMinimum(2)
                .withMaximum(3)
                .withMinimumWeight(2)
                .withMaximumWeight(4)
                .build();
        Employee employee = getEmployee(contract);
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(0, employee);
        ShiftAssignment shift3 = getShiftAssignment(1, employee);
        ShiftAssignment shift4 = getShiftAssignment(2, employee);
        ShiftAssignment shift5 = getShiftAssignment(2, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumAndMaximumNumberOfAssignments)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3, shift4, shift5)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumAndMaximumNumberOfAssignments)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumAndMaximumNumberOfAssignments)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumAndMaximumNumberOfAssignments)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2)
                .penalizesBy(0);
    }

    @Test
    void minimumNumberOfAssignmentsNoAssignments() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.TOTAL_ASSIGNMENTS)
                .withMinimum(2)
                .withMinimumWeight(5)
                .build();

        Employee employeeNoShifts = getEmployee(contract);
        Employee employeeWithShifts = getEmployee(contract);

        ShiftAssignment shift = getShiftAssignment(0, employeeWithShifts);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumNumberOfAssignmentsNoAssignments)
                .given(contract.getContractLineList().get(0),
                        employeeNoShifts, employeeWithShifts,
                        shift)
                .penalizesBy(10);
    }

    @Test
    void consecutiveWorkingDays() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_WORKING_DAYS)
                .withMinimum(2)
                .withMaximum(3)
                .withMinimumWeight(2)
                .withMaximumWeight(4)
                .build();
        Employee employee = getEmployee(contract);
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(1, employee);
        ShiftAssignment shift3 = getShiftAssignment(2, employee);
        ShiftAssignment shift4 = getShiftAssignment(3, employee);
        ShiftAssignment shift5 = getShiftAssignment(4, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3, shift4, shift5)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift4, shift5)
                .penalizesBy(0);
    }

    @Test
    void consecutiveFreeDays() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_FREE_DAYS)
                .withMinimum(2)
                .withMaximum(3)
                .withMinimumWeight(2)
                .withMaximumWeight(4)
                .build();
        Employee employee = getEmployee(contract);
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        // ShiftAssignment shift2 = getShiftAssignment(1, employee);
        ShiftAssignment shift3 = getShiftAssignment(2, employee);
        ShiftAssignment shift4 = getShiftAssignment(3, employee);
        ShiftAssignment shift5 = getShiftAssignment(4, employee);
        // ShiftAssignment shift6 = getShiftAssignment(5, employee);
        ShiftAssignment shift7 = getShiftAssignment(6, employee);

        NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization();

        nurseRosterParametrization.setId(idSupplier.incrementAndGet());
        nurseRosterParametrization.setPlanningWindowStart(getShiftDate(0));
        nurseRosterParametrization.setFirstShiftDate(getShiftDate(0));
        nurseRosterParametrization.setLastShiftDate(getShiftDate(6));

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift1, shift7)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift1, shift3)
                .penalizesBy(6);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift1, shift5)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift1, shift4)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift1, shift4, shift7)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays)
                .given(contract.getContractLineList().get(0),
                        employee,
                        nurseRosterParametrization,
                        shift7)
                .penalizesBy(12);
    }

    @Test
    void maximumConsecutiveFreeDaysNoAssignments() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_FREE_DAYS)
                .withMaximum(1)
                .withMaximumWeight(1)
                .build();

        Employee employeeNoShifts = getEmployee(contract);
        Employee employeeWithShifts = getEmployee(contract);

        ShiftAssignment shift = getShiftAssignment(0, employeeWithShifts);
        NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization();

        nurseRosterParametrization.setId(idSupplier.incrementAndGet());
        nurseRosterParametrization.setPlanningWindowStart(getShiftDate(0));
        nurseRosterParametrization.setFirstShiftDate(getShiftDate(0));
        nurseRosterParametrization.setLastShiftDate(getShiftDate(5));

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::maximumConsecutiveFreeDaysNoAssignments)
                .given(contract.getContractLineList().get(0),
                        employeeNoShifts, employeeWithShifts,
                        shift, nurseRosterParametrization)
                .penalizesBy(5);
    }

    @Test
    void consecutiveWorkingWeekends() {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_WORKING_WEEKENDS)
                .withMinimum(2)
                .withMinimumWeight(2)
                .withMaximum(3)
                .withMaximumWeight(4)
                .build();
        Employee employee = getEmployee(contract);

        // Nice thing about January 2000, January 1st is a Saturday!
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(1, employee);
        ShiftAssignment shift3 = getShiftAssignment(8, employee);
        ShiftAssignment shift4 = getShiftAssignment(14, employee);
        ShiftAssignment shift5 = getShiftAssignment(15, employee);
        ShiftAssignment shift6 = getShiftAssignment(21, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3, shift4, shift5, shift6)
                .penalizesBy(4);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2)
                .penalizesBy(2);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift4, shift5)
                .penalizesBy(4);

    }

    @Test
    void startOnNotFirstDayOfWeekend() {
        Contract contract = new BooleanContractBuilder(ContractLineType.COMPLETE_WEEKENDS)
                .withWeight(3)
                .build();
        Employee employee = getEmployee(contract);

        // Nice thing about January 2000, January 1st is a Saturday!
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(1, employee);
        ShiftAssignment shift3 = getShiftAssignment(7, employee);
        ShiftAssignment shift4 = getShiftAssignment(8, employee);
        ShiftAssignment shift5 = getShiftAssignment(14, employee);
        ShiftAssignment shift6 = getShiftAssignment(15, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift2, shift3, shift4, shift5)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3)
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift2, shift6)
                .penalizesBy(6);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3, shift4)
                .penalizesBy(0);

    }

    @Test
    void endOnNotLastDayOfWeekend() {
        Contract contract = new BooleanContractBuilder(ContractLineType.COMPLETE_WEEKENDS)
                .withWeight(3)
                .build();
        Employee employee = getEmployee(contract);

        // Nice thing about January 2000, January 1st is a Saturday!
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(1, employee);
        ShiftAssignment shift3 = getShiftAssignment(7, employee);
        ShiftAssignment shift4 = getShiftAssignment(8, employee);
        ShiftAssignment shift5 = getShiftAssignment(14, employee);
        // ShiftAssignment shift6 = getShiftAssignment(15, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3)
                .penalizesBy(3);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift5)
                .penalizesBy(6);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1, shift2, shift3, shift4)
                .penalizesBy(0);

    }

    @Test
    void identicalShiftTypesDuringWeekend() {
        Contract contract = new BooleanContractBuilder(ContractLineType.IDENTICAL_SHIFT_TYPES_DURING_WEEKEND)
                .withWeight(3)
                .build();
        Employee employee = getEmployee(contract);

        // Nice thing about January 2000, January 1st is a Saturday!
        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        // ShiftAssignment shift3Day = getShiftAssignment(7, employee, dayShiftType);
        ShiftAssignment shift4Day = getShiftAssignment(8, employee, dayShiftType);

        // ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(7, employee, nightShiftType);
        // ShiftAssignment shift4Night = getShiftAssignment(8, employee, nightShiftType);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::identicalShiftTypesDuringWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1Day, shift2Night,
                        shift1Day.getShiftDate(), shift2Night.getShiftDate())
                .penalizesBy(6);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::identicalShiftTypesDuringWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1Day, shift1Day.getShiftDate())
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::identicalShiftTypesDuringWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1Day, shift2Day, shift3Night,
                        shift1Day.getShiftDate(), shift2Day.getShiftDate(), shift3Night.getShiftDate())
                .penalizesBy(0);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::identicalShiftTypesDuringWeekend)
                .given(contract.getContractLineList().get(0),
                        employee,
                        shift1Day, shift2Night, shift3Night, shift4Day,
                        shift1Day.getShiftDate(), shift2Night.getShiftDate(), shift3Night.getShiftDate(),
                        shift4Day.getShiftDate())
                .penalizesBy(12);

    }

    @Test
    void dayOffRequest() {
        Employee employee = getEmployee();
        DayOffRequest dayOffRequest1 = getDayOffRequest(employee, getShiftDate(1), 3);
        DayOffRequest dayOffRequest2 = getDayOffRequest(employee, getShiftDate(3), 5);

        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOffRequest)
                .given(dayOffRequest1, dayOffRequest2,
                        shift1Day, shift2)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOffRequest)
                .given(dayOffRequest1, dayOffRequest2,
                        shift1Day, shift1Night)
                .penalizesBy(6);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOffRequest)
                .given(dayOffRequest1, dayOffRequest2,
                        shift1Day, shift3)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOffRequest)
                .given(dayOffRequest1, dayOffRequest2,
                        shift2)
                .penalizesBy(0);
    }

    @Test
    void dayOnRequest() {
        Employee employee = getEmployee();
        DayOnRequest dayOnRequest1 = getDayOnRequest(employee, getShiftDate(1), 3);
        DayOnRequest dayOnRequest2 = getDayOnRequest(employee, getShiftDate(3), 5);

        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOnRequest)
                .given(dayOnRequest1, dayOnRequest2,
                        shift1Day, shift2)
                .penalizesBy(5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOnRequest)
                .given(dayOnRequest1, dayOnRequest2,
                        shift1Day, shift1Night)
                .penalizesBy(5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOnRequest)
                .given(dayOnRequest1, dayOnRequest2,
                        shift1Day, shift3)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOnRequest)
                .given(dayOnRequest1, dayOnRequest2,
                        shift2)
                .penalizesBy(8);
    }

    @Test
    void shiftOffRequest() {
        Employee employee = getEmployee();
        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        ShiftOffRequest shiftOffRequest1 = getShiftOffRequest(employee, shift1Day.getShift(), 3);
        ShiftOffRequest shiftOffRequest2 = getShiftOffRequest(employee, shift3.getShift(), 5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift2)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift1Night)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Night)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift3)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift2)
                .penalizesBy(0);
    }

    @Test
    void shiftOnRequest() {
        Employee employee = getEmployee();
        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        ShiftOnRequest shiftOffRequest1 = getShiftOnRequest(employee, shift1Day.getShift(), 3);
        ShiftOnRequest shiftOffRequest2 = getShiftOnRequest(employee, shift3.getShift(), 5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift2)
                .penalizesBy(5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift1Night)
                .penalizesBy(5);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Night)
                .penalizesBy(8);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift1Day, shift3)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest)
                .given(shiftOffRequest1, shiftOffRequest2,
                        shift2)
                .penalizesBy(8);
    }

    @Test
    void alternativeSkill() {
        Contract contract = new BooleanContractBuilder(ContractLineType.ALTERNATIVE_SKILL_CATEGORY)
                .withWeight(3)
                .build();
        Employee employee = getEmployee(contract);
        Skill skill = getSkill("daySkill");
        SkillProficiency skillProficiency = getSkillProficiency(employee, skill);
        ShiftTypeSkillRequirement shiftTypeSkillRequirement = getSkillRequirement(dayShiftType, skill);

        ShiftAssignment dayShift1 = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment dayShift2 = getShiftAssignment(2, employee, dayShiftType);
        ShiftAssignment nightShift = getShiftAssignment(3, employee, nightShiftType);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::alternativeSkill)
                .given(contract.getContractLineList().get(0),
                        shiftTypeSkillRequirement, skillProficiency,
                        dayShift1)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::alternativeSkill)
                .given(contract.getContractLineList().get(0),
                        shiftTypeSkillRequirement,
                        dayShift1)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::alternativeSkill)
                .given(contract.getContractLineList().get(0),
                        shiftTypeSkillRequirement,
                        dayShift1, nightShift)
                .penalizesBy(3);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::alternativeSkill)
                .given(contract.getContractLineList().get(0),
                        shiftTypeSkillRequirement,
                        dayShift1, dayShift2)
                .penalizesBy(6);
    }

    @Test
    void unwantedPatternFreeBefore2DaysWithAWorkDayPattern() {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .freeBefore2DaysWithAWorkDay(DayOfWeek.WEDNESDAY)
                .build();
        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);
        ShiftAssignment freeShift = getShiftAssignment(4, employee);
        ShiftAssignment afterFreeShift1 = getShiftAssignment(5, employee);
        ShiftAssignment afterFreeShift2 = getShiftAssignment(6, employee);
        ShiftAssignment afterFreeShift3 = getShiftAssignment(7, employee);
        ShiftAssignment beforeFreeShift1 = getShiftAssignment(3, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        afterFreeShift2)
                .penalizesBy(1);
        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        afterFreeShift1, afterFreeShift2)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        afterFreeShift1)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        afterFreeShift3)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        beforeFreeShift1)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern)
                .given(patternContractLine,
                        employee,
                        freeShift.getShiftDate(),
                        afterFreeShift1, afterFreeShift2, freeShift)
                .penalizesBy(0);
    }

    @Test
    void unwantedPatternShiftType2DaysPattern() {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .shiftType2DaysPattern(dayShiftType, nightShiftType)
                .build();

        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);

        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift3Day = getShiftAssignment(2, employee, dayShiftType);
        // ShiftAssignment shift4Day = getShiftAssignment(3, employee, dayShiftType);

        ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(2, employee, nightShiftType);
        ShiftAssignment shift4Night = getShiftAssignment(3, employee, nightShiftType);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Day)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Night, shift2Day)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift3Day, shift4Night)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift3Night)
                .penalizesBy(0);

    }

    @Test
    void unwantedPatternShiftType2DaysPatternNullSecondShiftType() {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .shiftType2DaysPattern(dayShiftType, null)
                .build();

        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);

        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift3Day = getShiftAssignment(2, employee, dayShiftType);
        ShiftAssignment shift4Day = getShiftAssignment(3, employee, dayShiftType);

        ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(2, employee, nightShiftType);
        // ShiftAssignment shift4Night = getShiftAssignment(3, employee, nightShiftType);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Day)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Night, shift2Day)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift3Day, shift4Day)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift3Night)
                .penalizesBy(0);

    }

    @Test
    void unwantedPatternShiftType3DaysPattern() {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .shiftType3DaysPattern(dayShiftType, nightShiftType, nightShiftType)
                .build();

        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);

        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        // ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift3Day = getShiftAssignment(2, employee, dayShiftType);
        ShiftAssignment shift4Day = getShiftAssignment(3, employee, dayShiftType);
        // ShiftAssignment shift5Day = getShiftAssignment(4, employee, dayShiftType);
        // ShiftAssignment shift6Day = getShiftAssignment(5, employee, dayShiftType);

        ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(2, employee, nightShiftType);
        ShiftAssignment shift4Night = getShiftAssignment(3, employee, nightShiftType);
        ShiftAssignment shift5Night = getShiftAssignment(4, employee, nightShiftType);
        ShiftAssignment shift6Night = getShiftAssignment(5, employee, nightShiftType);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift3Night)
                .penalizesBy(1);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift3Day)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Night, shift2Night, shift3Day)
                .penalizesBy(0);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift3Night, shift4Day, shift5Night, shift6Night)
                .penalizesBy(2);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern)
                .given(patternContractLine,
                        employee,
                        shift1Day, shift2Night, shift4Night)
                .penalizesBy(0);

    }
}
