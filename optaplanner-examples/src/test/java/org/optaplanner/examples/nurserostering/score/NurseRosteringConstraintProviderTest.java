package org.optaplanner.examples.nurserostering.score;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
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
import org.optaplanner.test.api.score.stream.SingleConstraintVerification;

class NurseRosteringConstraintProviderTest
        extends AbstractConstraintProviderTest<NurseRosteringConstraintProvider, NurseRoster> {

    private final ThreadLocal<AtomicLong> idSupplier = ThreadLocal.withInitial(() -> new AtomicLong(0));
    private final ThreadLocal<Map<Pair<Integer, ShiftType>, Shift>> indexShiftTypePairToShiftMap =
            ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<Integer, ShiftDate>> indexToShiftDateMap = ThreadLocal.withInitial(HashMap::new);
    private final ShiftType dayShiftType = new ShiftType(idSupplier.get().getAndIncrement());
    private final ShiftType nightShiftType = new ShiftType(idSupplier.get().getAndIncrement());

    @BeforeEach
    void setup() {
        dayShiftType.setNight(false);
        dayShiftType.setStartTimeString("09:00");
        dayShiftType.setEndTimeString("17:00");
        dayShiftType.setCode("ShiftType - Day");
        dayShiftType.setIndex(0);
        dayShiftType.setDescription("Day Shift");

        nightShiftType.setNight(true);
        nightShiftType.setStartTimeString("07:00");
        nightShiftType.setEndTimeString("04:00");
        nightShiftType.setCode("ShiftType - Night");
        nightShiftType.setIndex(1);
        nightShiftType.setDescription("Night Shift");
    }

    @AfterEach
    void tearDown() {
        idSupplier.remove();
        indexShiftTypePairToShiftMap.remove();
        indexToShiftDateMap.remove();
    }

    @Override
    protected ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> createConstraintVerifier() {
        return ConstraintVerifier.build(new NurseRosteringConstraintProvider(), NurseRoster.class, ShiftAssignment.class);
    }

    private long getNextId() {
        return idSupplier.get().getAndIncrement();
    }

    // ******************************************************
    // Model Factories
    // ******************************************************
    private class MinMaxContractBuilder {
        private Integer minimumValue;
        private Integer maximumValue;
        private Integer minimumWeight;
        private Integer maximumWeight;
        private final ContractLineType contractLineType;
        private final WeekendDefinition weekendDefinition;

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

        public Contract build() {
            long contractId = getNextId();
            Contract contract =
                    new Contract(contractId, "Contract - " + contractId, "Minimum/Maximum " + contractLineType + " Contract");
            contract.setWeekendDefinition(weekendDefinition);

            MinMaxContractLine contractLine =
                    new MinMaxContractLine(getNextId(), contract, contractLineType, minimumValue != null, maximumValue != null);
            if (minimumValue != null) {
                contractLine.setMinimumValue(minimumValue);
                contractLine.setMinimumWeight(minimumWeight);
            }
            if (maximumValue != null) {
                contractLine.setMaximumValue(maximumValue);
                contractLine.setMaximumWeight(maximumWeight);
            }

            contract.setContractLineList(Collections.singletonList(contractLine));
            return contract;
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

        public Contract build() {
            long contractId = getNextId();
            Contract contract =
                    new Contract(contractId, "Contract - " + contractId, "Boolean " + contractLineType + " Contract");
            contract.setWeekendDefinition(weekendDefinition);

            BooleanContractLine contractLine =
                    new BooleanContractLine(getNextId(), contract, contractLineType, enabled, weight);
            contract.setContractLineList(Collections.singletonList(contractLine));
            return contract;
        }
    }

    private class PatternContractBuilder {
        FreeBefore2DaysWithAWorkDayPattern freeBefore2DaysWithAWorkDayPattern;
        ShiftType2DaysPattern shiftType2DaysPattern;
        ShiftType3DaysPattern shiftType3DaysPattern;
        private final WeekendDefinition weekendDefinition = WeekendDefinition.SATURDAY_SUNDAY;
        int weight = 1;

        public PatternContractBuilder freeBefore2DaysWithAWorkDay(DayOfWeek workDay) {
            freeBefore2DaysWithAWorkDayPattern =
                    new FreeBefore2DaysWithAWorkDayPattern(getNextId(), "Free Before 2 Days - " + workDay, workDay);
            return this;
        }

        public PatternContractBuilder shiftType2DaysPattern(ShiftType day0ShiftType, ShiftType day1ShiftType) {
            shiftType2DaysPattern = new ShiftType2DaysPattern(getNextId(),
                    "Shift Type 2 Day Pattern - " + day0ShiftType + ", " + day1ShiftType);
            shiftType2DaysPattern.setDayIndex0ShiftType(day0ShiftType);
            shiftType2DaysPattern.setDayIndex1ShiftType(day1ShiftType);
            return this;
        }

        public PatternContractBuilder shiftType3DaysPattern(ShiftType day0ShiftType, ShiftType day1ShiftType,
                ShiftType day2ShiftType) {
            shiftType3DaysPattern = new ShiftType3DaysPattern(getNextId(),
                    "Shift Type 3 Day Pattern - " + day0ShiftType + ", " + day1ShiftType + ", " + day2ShiftType);
            shiftType3DaysPattern.setDayIndex0ShiftType(day0ShiftType);
            shiftType3DaysPattern.setDayIndex1ShiftType(day1ShiftType);
            shiftType3DaysPattern.setDayIndex2ShiftType(day2ShiftType);
            return this;
        }

        public Pair<PatternContractLine, Contract> build() {
            PatternContractLine patternContractLine = new PatternContractLine(getNextId());
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
            patternContractLine.getPattern().setWeight(weight);
            Contract out = new Contract(getNextId());
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
        long employeeId = getNextId();
        Employee employee = new Employee(employeeId, "Employee " + employeeId, "Employee " + employeeId, contract);
        employee.setDayOffRequestMap(new HashMap<>());
        employee.setDayOnRequestMap(new HashMap<>());
        employee.setShiftOffRequestMap(new HashMap<>());
        employee.setShiftOnRequestMap(new HashMap<>());
        return employee;
    }

    private ShiftDate getShiftDate(int dayIndex) {
        return indexToShiftDateMap.get().computeIfAbsent(dayIndex, key -> {
            ShiftDate shiftDate = new ShiftDate(getNextId(), dayIndex, LocalDate.of(2000, 1, 1).plusDays(dayIndex));
            shiftDate.setShiftList(new ArrayList<>());
            return shiftDate;
        });
    }

    private ShiftAssignment getShiftAssignment(int dayIndex, Employee employee) {
        return getShiftAssignment(dayIndex, employee, dayShiftType);
    }

    private ShiftAssignment getShiftAssignment(int dayIndex, Employee employee, ShiftType shiftType) {
        Shift shift = indexShiftTypePairToShiftMap.get().computeIfAbsent(Pair.of(dayIndex, shiftType), key -> {
            ShiftDate shiftDate = getShiftDate(dayIndex);
            Shift newShift = new Shift(getNextId(), shiftDate, shiftType, 0, 0);
            shiftDate.getShiftList().add(newShift);
            return newShift;
        });
        shift.setRequiredEmployeeSize(shift.getRequiredEmployeeSize() + 1);
        ShiftAssignment shiftAssignment = new ShiftAssignment(getNextId(), shift, 0);
        shiftAssignment.setEmployee(employee);
        return shiftAssignment;
    }

    private DayOffRequest getDayOffRequest(Employee employee, ShiftDate shiftDate, int weight) {
        return new DayOffRequest(getNextId(), employee, shiftDate, weight);
    }

    private DayOnRequest getDayOnRequest(Employee employee, ShiftDate shiftDate, int weight) {
        return new DayOnRequest(getNextId(), employee, shiftDate, weight);
    }

    private ShiftOffRequest getShiftOffRequest(Employee employee, Shift shift, int weight) {
        return new ShiftOffRequest(getNextId(), employee, shift, weight);
    }

    private ShiftOnRequest getShiftOnRequest(Employee employee, Shift shift, int weight) {
        return new ShiftOnRequest(getNextId(), employee, shift, weight);
    }

    private Skill getSkill(String name) {
        return new Skill(getNextId(), "Skill - " + name);
    }

    private SkillProficiency getSkillProficiency(Employee employee, Skill skill) {
        return new SkillProficiency(getNextId(), employee, skill);
    }

    private ShiftTypeSkillRequirement getSkillRequirement(ShiftType shiftType, Skill skill) {
        return new ShiftTypeSkillRequirement(getNextId(), shiftType, skill);
    }

    // ******************************************************
    // TESTS
    // ******************************************************
    @ConstraintProviderTest
    void oneShiftPerDay(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Employee employee = getEmployee();
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift2 = getShiftAssignment(0, employee);
        ShiftAssignment shift3 = getShiftAssignment(1, employee);
        ShiftAssignment shift4 = getShiftAssignment(2, employee);
        ShiftAssignment shift5 = getShiftAssignment(2, employee);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::oneShiftPerDay)
                .given(shift1, shift2, shift3, shift4, shift5).penalizesBy(2);
    }

    @ConstraintProviderTest
    void minimumAndMaximumNumberOfAssignments(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> minimumAndMaximumNumberOfAssignmentsConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumAndMaximumNumberOfAssignments);

        minimumAndMaximumNumberOfAssignmentsConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3, shift4, shift5)
                .penalizesBy(8);

        minimumAndMaximumNumberOfAssignmentsConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(2);

        minimumAndMaximumNumberOfAssignmentsConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3)
                .penalizesBy(0);

        minimumAndMaximumNumberOfAssignmentsConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void minimumNumberOfAssignmentsNoAssignments(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Contract contract = new MinMaxContractBuilder(ContractLineType.TOTAL_ASSIGNMENTS)
                .withMinimum(2)
                .withMinimumWeight(5)
                .build();

        Employee employeeNoShifts = getEmployee(contract);
        Employee employeeWithShifts = getEmployee(contract);

        ShiftAssignment shift = getShiftAssignment(0, employeeWithShifts);

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::minimumNumberOfAssignmentsNoAssignments)
                .given(contract.getFirstConstractLine(),
                        employeeNoShifts, employeeWithShifts,
                        shift)
                .penalizesBy(10);
    }

    @ConstraintProviderTest
    void consecutiveWorkingDays(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> consecutiveWorkingDaysConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingDays);

        consecutiveWorkingDaysConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3, shift4, shift5)
                .penalizesBy(8);

        consecutiveWorkingDaysConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(2);

        consecutiveWorkingDaysConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3)
                .penalizesBy(0);

        consecutiveWorkingDaysConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2)
                .penalizesBy(0);

        consecutiveWorkingDaysConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2, shift4, shift5)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void consecutiveFreeDays(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_FREE_DAYS)
                .withMinimum(2)
                .withMaximum(3)
                .withMinimumWeight(2)
                .withMaximumWeight(4)
                .build();
        Employee employee = getEmployee(contract);
        ShiftAssignment shift1 = getShiftAssignment(0, employee);
        ShiftAssignment shift3 = getShiftAssignment(2, employee);
        ShiftAssignment shift4 = getShiftAssignment(3, employee);
        ShiftAssignment shift5 = getShiftAssignment(4, employee);
        ShiftAssignment shift7 = getShiftAssignment(6, employee);

        NurseRosterParametrization nurseRosterParametrization =
                new NurseRosterParametrization(getNextId(), getShiftDate(0), getShiftDate(6), getShiftDate(0));

        SingleConstraintVerification<NurseRoster> consecutiveFreeDaysConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveFreeDays);

        consecutiveFreeDaysConstraint
                .given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift1, shift7)
                .penalizesBy(8);

        consecutiveFreeDaysConstraint
                .given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift1, shift3)
                .penalizesBy(6);

        consecutiveFreeDaysConstraint
                .given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift1, shift5)
                .penalizesBy(0);

        consecutiveFreeDaysConstraint
                .given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift1, shift4)
                .penalizesBy(0);

        consecutiveFreeDaysConstraint
                .given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift1, shift4, shift7)
                .penalizesBy(0);

        consecutiveFreeDaysConstraint.given(contract.getFirstConstractLine(), employee, nurseRosterParametrization, shift7)
                .penalizesBy(12);
    }

    @ConstraintProviderTest
    void maximumConsecutiveFreeDaysNoAssignments(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Contract contract = new MinMaxContractBuilder(ContractLineType.CONSECUTIVE_FREE_DAYS)
                .withMaximum(1)
                .withMaximumWeight(1)
                .build();

        Employee employeeNoShifts = getEmployee(contract);
        Employee employeeWithShifts = getEmployee(contract);

        ShiftAssignment shift = getShiftAssignment(0, employeeWithShifts);
        NurseRosterParametrization nurseRosterParametrization =
                new NurseRosterParametrization(getNextId(), getShiftDate(0), getShiftDate(5), getShiftDate(0));

        constraintVerifier.verifyThat(NurseRosteringConstraintProvider::maximumConsecutiveFreeDaysNoAssignments)
                .given(contract.getFirstConstractLine(),
                        employeeNoShifts, employeeWithShifts,
                        shift, nurseRosterParametrization)
                .penalizesBy(5);
    }

    @ConstraintProviderTest
    void consecutiveWorkingWeekends(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> consecutiveWorkingWeekendsConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::consecutiveWorkingWeekends);

        consecutiveWorkingWeekendsConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3, shift4, shift5, shift6)
                .penalizesBy(4);

        consecutiveWorkingWeekendsConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(2);

        consecutiveWorkingWeekendsConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3)
                .penalizesBy(0);

        consecutiveWorkingWeekendsConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2)
                .penalizesBy(2);

        consecutiveWorkingWeekendsConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift4, shift5)
                .penalizesBy(4);
    }

    @ConstraintProviderTest
    void startOnNotFirstDayOfWeekend(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> startOnNotFirstDayOfWeekendConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::startOnNotFirstDayOfWeekend);

        startOnNotFirstDayOfWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift2, shift3, shift4, shift5)
                .penalizesBy(3);

        startOnNotFirstDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(0);

        startOnNotFirstDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3)
                .penalizesBy(0);

        startOnNotFirstDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift2, shift6)
                .penalizesBy(6);

        startOnNotFirstDayOfWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3, shift4)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void endOnNotLastDayOfWeekend(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> endOnNotLastDayOfWeekendConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::endOnNotLastDayOfWeekend);

        endOnNotLastDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(3);

        endOnNotLastDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1)
                .penalizesBy(3);

        endOnNotLastDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3)
                .penalizesBy(3);

        endOnNotLastDayOfWeekendConstraint.given(contract.getFirstConstractLine(), employee, shift1, shift5)
                .penalizesBy(6);

        endOnNotLastDayOfWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1, shift2, shift3, shift4)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void identicalShiftTypesDuringWeekend(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Contract contract = new BooleanContractBuilder(ContractLineType.IDENTICAL_SHIFT_TYPES_DURING_WEEKEND)
                .withWeight(3)
                .build();
        Employee employee = getEmployee(contract);

        // Nice thing about January 2000, January 1st is a Saturday!
        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift4Day = getShiftAssignment(8, employee, dayShiftType);

        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(7, employee, nightShiftType);

        SingleConstraintVerification<NurseRoster> identicalShiftTypesDuringWeekendConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::identicalShiftTypesDuringWeekend);

        identicalShiftTypesDuringWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1Day, shift2Night, shift1Day.getShiftDate(),
                        shift2Night.getShiftDate())
                .penalizesBy(6);

        identicalShiftTypesDuringWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1Day, shift1Day.getShiftDate())
                .penalizesBy(0);

        identicalShiftTypesDuringWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1Day, shift2Day, shift3Night,
                        shift1Day.getShiftDate(), shift2Day.getShiftDate(), shift3Night.getShiftDate())
                .penalizesBy(0);

        identicalShiftTypesDuringWeekendConstraint
                .given(contract.getFirstConstractLine(), employee, shift1Day, shift2Night, shift3Night, shift4Day,
                        shift1Day.getShiftDate(), shift2Night.getShiftDate(), shift3Night.getShiftDate(),
                        shift4Day.getShiftDate())
                .penalizesBy(12);
    }

    @ConstraintProviderTest
    void dayOffRequest(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Employee employee = getEmployee();
        DayOffRequest dayOffRequest1 = getDayOffRequest(employee, getShiftDate(1), 3);
        DayOffRequest dayOffRequest2 = getDayOffRequest(employee, getShiftDate(3), 5);

        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        SingleConstraintVerification<NurseRoster> dayOffRequestConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOffRequest);

        dayOffRequestConstraint.given(dayOffRequest1, dayOffRequest2, shift1Day, shift2)
                .penalizesBy(3);

        dayOffRequestConstraint.given(dayOffRequest1, dayOffRequest2, shift1Day, shift1Night)
                .penalizesBy(6);

        dayOffRequestConstraint.given(dayOffRequest1, dayOffRequest2, shift1Day, shift3)
                .penalizesBy(8);

        dayOffRequestConstraint.given(dayOffRequest1, dayOffRequest2, shift2)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void dayOnRequest(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Employee employee = getEmployee();
        DayOnRequest dayOnRequest1 = getDayOnRequest(employee, getShiftDate(1), 3);
        DayOnRequest dayOnRequest2 = getDayOnRequest(employee, getShiftDate(3), 5);

        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        SingleConstraintVerification<NurseRoster> dayOnRequestConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::dayOnRequest);
        dayOnRequestConstraint.given(dayOnRequest1, dayOnRequest2, shift1Day, shift2)
                .penalizesBy(5);

        dayOnRequestConstraint.given(dayOnRequest1, dayOnRequest2, shift1Day, shift1Night)
                .penalizesBy(5);

        dayOnRequestConstraint.given(dayOnRequest1, dayOnRequest2, shift1Day, shift3)
                .penalizesBy(0);

        dayOnRequestConstraint.given(dayOnRequest1, dayOnRequest2, shift2)
                .penalizesBy(8);
    }

    @ConstraintProviderTest
    void shiftOffRequest(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Employee employee = getEmployee();
        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        ShiftOffRequest shiftOffRequest1 = getShiftOffRequest(employee, shift1Day.getShift(), 3);
        ShiftOffRequest shiftOffRequest2 = getShiftOffRequest(employee, shift3.getShift(), 5);

        SingleConstraintVerification<NurseRoster> shiftOffRequestConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOffRequest);

        shiftOffRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift2)
                .penalizesBy(3);

        shiftOffRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift1Night)
                .penalizesBy(3);

        shiftOffRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Night)
                .penalizesBy(0);

        shiftOffRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift3)
                .penalizesBy(8);

        shiftOffRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift2)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void shiftOnRequest(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Employee employee = getEmployee();
        ShiftAssignment shift1Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift1Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift2 = getShiftAssignment(2, employee);
        ShiftAssignment shift3 = getShiftAssignment(3, employee);

        ShiftOnRequest shiftOffRequest1 = getShiftOnRequest(employee, shift1Day.getShift(), 3);
        ShiftOnRequest shiftOffRequest2 = getShiftOnRequest(employee, shift3.getShift(), 5);

        SingleConstraintVerification<NurseRoster> shiftOnRequestConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::shiftOnRequest);

        shiftOnRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift2)
                .penalizesBy(5);

        shiftOnRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift1Night)
                .penalizesBy(5);

        shiftOnRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Night)
                .penalizesBy(8);

        shiftOnRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift1Day, shift3)
                .penalizesBy(0);

        shiftOnRequestConstraint.given(shiftOffRequest1, shiftOffRequest2, shift2)
                .penalizesBy(8);
    }

    @ConstraintProviderTest
    void alternativeSkill(ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> alternativeSkillConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::alternativeSkill);

        alternativeSkillConstraint
                .given(contract.getFirstConstractLine(), shiftTypeSkillRequirement, skillProficiency, dayShift1)
                .penalizesBy(0);

        alternativeSkillConstraint.given(contract.getFirstConstractLine(), shiftTypeSkillRequirement, dayShift1)
                .penalizesBy(3);

        alternativeSkillConstraint
                .given(contract.getFirstConstractLine(), shiftTypeSkillRequirement, dayShift1, nightShift)
                .penalizesBy(3);

        alternativeSkillConstraint.given(contract.getFirstConstractLine(), shiftTypeSkillRequirement, dayShift1, dayShift2)
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void unwantedPatternFreeBefore2DaysWithAWorkDayPattern(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint =
                constraintVerifier
                        .verifyThat(NurseRosteringConstraintProvider::unwantedPatternFreeBefore2DaysWithAWorkDayPattern);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), afterFreeShift2)
                .penalizesBy(1);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), afterFreeShift1, afterFreeShift2)
                .penalizesBy(1);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), afterFreeShift1)
                .penalizesBy(1);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), afterFreeShift3)
                .penalizesBy(0);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), beforeFreeShift1)
                .penalizesBy(0);

        unwantedPatternFreeBefore2DaysWithAWorkDayPatternConstraint
                .given(patternContractLine, employee, freeShift.getShiftDate(), afterFreeShift1, afterFreeShift2, freeShift)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void unwantedPatternShiftType2DaysPattern(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .shiftType2DaysPattern(dayShiftType, nightShiftType)
                .build();

        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);

        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift2Day = getShiftAssignment(1, employee, dayShiftType);
        ShiftAssignment shift3Day = getShiftAssignment(2, employee, dayShiftType);

        ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(2, employee, nightShiftType);
        ShiftAssignment shift4Night = getShiftAssignment(3, employee, nightShiftType);

        SingleConstraintVerification<NurseRoster> unwantedPatternShiftType2DaysPatternConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Night)
                .penalizesBy(1);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Day)
                .penalizesBy(0);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Night, shift2Day)
                .penalizesBy(0);

        unwantedPatternShiftType2DaysPatternConstraint
                .given(patternContractLine, employee, shift1Day, shift2Night, shift3Day, shift4Night)
                .penalizesBy(2);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift3Night)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void unwantedPatternShiftType2DaysPatternNullSecondShiftType(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
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

        SingleConstraintVerification<NurseRoster> unwantedPatternShiftType2DaysPatternConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType2DaysPattern);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Night)
                .penalizesBy(1);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Day)
                .penalizesBy(1);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Night, shift2Day)
                .penalizesBy(0);

        unwantedPatternShiftType2DaysPatternConstraint
                .given(patternContractLine, employee, shift1Day, shift2Night, shift3Day, shift4Day)
                .penalizesBy(2);

        unwantedPatternShiftType2DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift3Night)
                .penalizesBy(0);
    }

    @ConstraintProviderTest
    void unwantedPatternShiftType3DaysPattern(
            ConstraintVerifier<NurseRosteringConstraintProvider, NurseRoster> constraintVerifier) {
        Pair<PatternContractLine, Contract> patternContractPair = new PatternContractBuilder()
                .shiftType3DaysPattern(dayShiftType, nightShiftType, nightShiftType)
                .build();

        PatternContractLine patternContractLine = patternContractPair.getKey();
        Contract contract = patternContractPair.getValue();

        Employee employee = getEmployee(contract);

        ShiftAssignment shift1Day = getShiftAssignment(0, employee, dayShiftType);
        ShiftAssignment shift3Day = getShiftAssignment(2, employee, dayShiftType);
        ShiftAssignment shift4Day = getShiftAssignment(3, employee, dayShiftType);

        ShiftAssignment shift1Night = getShiftAssignment(0, employee, nightShiftType);
        ShiftAssignment shift2Night = getShiftAssignment(1, employee, nightShiftType);
        ShiftAssignment shift3Night = getShiftAssignment(2, employee, nightShiftType);
        ShiftAssignment shift4Night = getShiftAssignment(3, employee, nightShiftType);
        ShiftAssignment shift5Night = getShiftAssignment(4, employee, nightShiftType);
        ShiftAssignment shift6Night = getShiftAssignment(5, employee, nightShiftType);

        SingleConstraintVerification<NurseRoster> unwantedPatternShiftType3DaysPatternConstraint =
                constraintVerifier.verifyThat(NurseRosteringConstraintProvider::unwantedPatternShiftType3DaysPattern);

        unwantedPatternShiftType3DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Night, shift3Night)
                .penalizesBy(1);

        unwantedPatternShiftType3DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Night, shift3Day)
                .penalizesBy(0);

        unwantedPatternShiftType3DaysPatternConstraint.given(patternContractLine, employee, shift1Night, shift2Night, shift3Day)
                .penalizesBy(0);

        unwantedPatternShiftType3DaysPatternConstraint
                .given(patternContractLine, employee, shift1Day, shift2Night, shift3Night, shift4Day, shift5Night, shift6Night)
                .penalizesBy(2);

        unwantedPatternShiftType3DaysPatternConstraint.given(patternContractLine, employee, shift1Day, shift2Night, shift4Night)
                .penalizesBy(0);
    }

}
