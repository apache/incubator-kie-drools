/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.nurserostering.optional.score;

import java.time.DayOfWeek;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.common.experimental.ConsecutiveData;
import org.optaplanner.examples.common.experimental.ExperimentalConstraintCollectors;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftTypeSkillRequirement;
import org.optaplanner.examples.nurserostering.domain.SkillProficiency;
import org.optaplanner.examples.nurserostering.domain.contract.BooleanContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
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
import org.optaplanner.examples.nurserostering.score.drools.EmployeeConsecutiveAssignmentEnd;
import org.optaplanner.examples.nurserostering.score.drools.EmployeeConsecutiveAssignmentStart;

public class NurseRosteringConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                oneShiftPerDay(constraintFactory),
                minimumAndMaximumNumberOfAssignments(constraintFactory),
                minimumNumberOfAssignmentsNoAssignments(constraintFactory),
                consecutiveWorkingDays(constraintFactory),
                consecutiveFreeDays(constraintFactory),
                maximumConsecutiveFreeDaysNoAssignments(constraintFactory),
                consecutiveFreeDaysFirstBreak(constraintFactory),
                consecutiveFreeDaysFinalBreak(constraintFactory),
                consecutiveWorkingWeekends(constraintFactory),
                startOnNotFirstDayOfWeekend(constraintFactory),
                endOnNotLastDayOfWeekend(constraintFactory),
                identicalShiftTypesDuringWeekend(constraintFactory),
                dayOffRequest(constraintFactory),
                dayOnRequest(constraintFactory),
                shiftOffRequest(constraintFactory),
                shiftOnRequest(constraintFactory),
                alternativeSkill(constraintFactory),
                unwantedPatternFreeBefore2DaysWithAWorkDayPattern(constraintFactory),
                unwantedPatternShiftType2DaysPattern(constraintFactory),
                unwantedPatternShiftType3DaysPattern(constraintFactory),
        };
    }

    // ############################################################################
    // Hard constraints
    // ############################################################################

    // A nurse can only work one shift per day, i.e. no two shift can be assigned to the same nurse on a day.
    Constraint oneShiftPerDay(ConstraintFactory constraintFactory) {
        return constraintFactory
                .fromUniquePair(ShiftAssignment.class,
                        Joiners.equal(ShiftAssignment::getEmployee),
                        Joiners.equal(ShiftAssignment::getShiftDate))
                .penalize("oneShiftPerDay", HardSoftScore.ONE_HARD);
    }

    // ############################################################################
    // Soft constraints
    // ############################################################################
    Constraint minimumAndMaximumNumberOfAssignments(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine.getContractLineType() == ContractLineType.TOTAL_ASSIGNMENTS &&
                        minMaxContractLine.isEnabled())
                .join(ShiftAssignment.class, Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((line, shift) -> shift.getEmployee(), (line, shift) -> line, ConstraintCollectors.countBi())
                .filter((employee, contract, shiftCount) -> employee != null && contract.isViolated(shiftCount))
                .penalize("Minimum and maximum number of assignments", HardSoftScore.ONE_SOFT,
                        (employee, contract, shiftCount) -> contract.getViolationAmount(shiftCount));
    }

    Constraint minimumNumberOfAssignmentsNoAssignments(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine.getContractLineType() == ContractLineType.TOTAL_ASSIGNMENTS &&
                        minMaxContractLine.isEnabled())
                .join(Employee.class,
                        Joiners.equal(MinMaxContractLine::getContract, Employee::getContract))
                .ifNotExists(ShiftAssignment.class,
                        Joiners.equal((contractLine, employee) -> employee, ShiftAssignment::getEmployee))
                .filter((contract, employee) -> contract.isViolated(0))
                .penalize("Minimum and maximum number of assignments (no assignments)", HardSoftScore.ONE_SOFT,
                        (contract, employee) -> contract.getViolationAmount(0));
    }

    // Min/Max consecutive working days
    Constraint consecutiveWorkingDays(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_WORKING_DAYS &&
                        minMaxContractLine.isEnabled())
                .join(ShiftAssignment.class, Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shift) -> shift.getEmployee(), (contract, shift) -> contract,
                        ExperimentalConstraintCollectors.consecutive((contract, shift) -> shift.getShiftDate(),
                                ShiftDate::getDayIndex))
                .flattenLast(ConsecutiveData::getConsecutiveSequences)
                .filter((employee, contract, shiftList) -> contract.isViolated(shiftList.getLength()))
                .penalize("consecutiveWorkingDays", HardSoftScore.ONE_SOFT,
                        (employee, contract, shiftList) -> contract.getViolationAmount(shiftList.getLength()));
    }

    // Min/Max consecutive free days
    Constraint consecutiveFreeDays(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_FREE_DAYS &&
                        minMaxContractLine.isEnabled())
                .join(ShiftAssignment.class, Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shift) -> shift.getEmployee(), (contract, shift) -> contract,
                        ExperimentalConstraintCollectors.consecutive((contract, shift) -> shift.getShiftDate(),
                                ShiftDate::getDayIndex))
                .flattenLast(ConsecutiveData::getBreaks)
                .filter((employee, contract, breakInfo) -> contract.isViolated(breakInfo.getLength() - 1))
                .penalize("consecutiveFreeDays", HardSoftScore.ONE_SOFT,
                        (employee, contract, breakInfo) -> contract
                                .getViolationAmount(breakInfo.getLength() - 1));
    }

    Constraint maximumConsecutiveFreeDaysNoAssignments(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_FREE_DAYS &&
                        minMaxContractLine.isMaximumEnabled())
                .join(Employee.class, Joiners.equal(MinMaxContractLine::getContract, Employee::getContract))
                .ifNotExists(ShiftAssignment.class,
                        Joiners.equal((contract, employee) -> employee, ShiftAssignment::getEmployee))
                .join(NurseRosterParametrization.class, Joiners.lessThan((contract, employee) -> contract.getMaximumValue(),
                        nrp -> nrp.getLastShiftDateDayIndex() - nrp.getFirstShiftDateDayIndex() + 1))
                .penalize("maximumConsecutiveFreeDays (no shifts)", HardSoftScore.ONE_SOFT,
                        (contract, employee, nrp) -> contract
                                .getViolationAmount(nrp.getLastShiftDateDayIndex() - nrp.getFirstShiftDateDayIndex() + 1));
    }

    Constraint consecutiveFreeDaysFirstBreak(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_FREE_DAYS &&
                        minMaxContractLine.isEnabled())
                .join(ShiftAssignment.class,
                        Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shiftAssignment) -> contract,
                        (contract, shiftAssignment) -> shiftAssignment.getEmployee(),
                        ConstraintCollectors.<MinMaxContractLine, ShiftAssignment, ShiftAssignment> min(
                                (contract, shiftAssignment) -> shiftAssignment))
                .join(NurseRosterParametrization.class)
                .filter((contract, employee, shiftAssignment,
                        nrp) -> nrp.getFirstShiftDate() != shiftAssignment.getShiftDate() && contract
                                .isViolated(shiftAssignment.getShiftDateDayIndex() - nrp.getFirstShiftDateDayIndex()))
                .penalize("consecutiveFreeDays (first break)",
                        HardSoftScore.ONE_SOFT,
                        (contractLine, employee, shiftAssignment, nrp) -> contractLine
                                .getViolationAmount(shiftAssignment.getShiftDateDayIndex() - nrp.getFirstShiftDateDayIndex()));
    }

    Constraint consecutiveFreeDaysFinalBreak(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_FREE_DAYS &&
                        minMaxContractLine.isEnabled())
                .join(ShiftAssignment.class,
                        Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shiftAssignment) -> contract,
                        (contract, shiftAssignment) -> shiftAssignment.getEmployee(),
                        ConstraintCollectors.<MinMaxContractLine, ShiftAssignment, ShiftAssignment> max(
                                (contract, shiftAssignment) -> shiftAssignment))
                .join(NurseRosterParametrization.class)
                .filter((contractLine, employee, shiftAssignment,
                        nrp) -> nrp.getLastShiftDate() != shiftAssignment.getShiftDate() &&
                                contractLine
                                        .isViolated(nrp.getLastShiftDateDayIndex() - shiftAssignment.getShiftDateDayIndex()))
                .penalize("consecutiveFreeDays (final break)",
                        HardSoftScore.ONE_SOFT,
                        (contractLine, employee, shiftAssignment, nrp) -> contractLine
                                .getViolationAmount(nrp.getLastShiftDateDayIndex() - shiftAssignment.getShiftDateDayIndex()));
    }

    // Min/Max consecutive working weekends
    Constraint consecutiveWorkingWeekends(ConstraintFactory constraintFactory) {
        return constraintFactory.from(MinMaxContractLine.class)
                .filter(minMaxContractLine -> minMaxContractLine
                        .getContractLineType() == ContractLineType.CONSECUTIVE_WORKING_WEEKENDS &&
                        minMaxContractLine.isEnabled())
                .join(constraintFactory.from(ShiftAssignment.class).filter(ShiftAssignment::isWeekend),
                        Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shift) -> shift.getEmployee(), (contract, shift) -> contract,
                        ExperimentalConstraintCollectors.consecutive((contract, shift) -> shift.getShiftDate(),
                                shiftDate -> shiftDate.getWeekendSundayIndex() / 7))
                .flattenLast(ConsecutiveData::getConsecutiveSequences)
                .filter((employee, contract, shiftList) -> contract.isViolated(shiftList.getLength()))
                .penalize("consecutiveWorkingWeekends", HardSoftScore.ONE_SOFT,
                        (employee, contract, shiftList) -> contract.getViolationAmount(shiftList.getLength()));
    }

    // Complete Weekends
    Constraint startOnNotFirstDayOfWeekend(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BooleanContractLine.class)
                .filter(booleanContractLine -> booleanContractLine.getContractLineType() == ContractLineType.COMPLETE_WEEKENDS
                        &&
                        booleanContractLine.isEnabled())
                .join(ShiftAssignment.class, Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shift) -> shift.getEmployee(), (contract, shift) -> contract,
                        ExperimentalConstraintCollectors.consecutive((contract, shift) -> shift.getShiftDate(),
                                ShiftDate::getDayIndex))
                .flattenLast(ConsecutiveData::getConsecutiveSequences)
                .filter((employee, contract, shiftList) -> new EmployeeConsecutiveAssignmentStart(
                        employee, shiftList.getItems().first()).isWeekendAndNotFirstDayOfWeekend())
                .penalize("startOnNotFirstDayOfWeekend", HardSoftScore.ONE_SOFT,
                        (employee, contract,
                                shiftList) -> new EmployeeConsecutiveAssignmentStart(employee, shiftList.getItems().first())
                                        .getDistanceToFirstDayOfWeekend() * contract.getWeight());
    }

    Constraint endOnNotLastDayOfWeekend(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BooleanContractLine.class)
                .filter(booleanContractLine -> booleanContractLine
                        .getContractLineType() == ContractLineType.COMPLETE_WEEKENDS &&
                        booleanContractLine.isEnabled())
                .join(ShiftAssignment.class, Joiners.equal(ContractLine::getContract, ShiftAssignment::getContract))
                .groupBy((contract, shift) -> shift.getEmployee(), (contract, shift) -> contract,
                        ExperimentalConstraintCollectors.consecutive((contract, shift) -> shift.getShiftDate(),
                                ShiftDate::getDayIndex))
                .flattenLast(ConsecutiveData::getConsecutiveSequences)
                .filter((employee, contract, shiftList) -> new EmployeeConsecutiveAssignmentEnd(employee,
                        shiftList.getItems().last()).isWeekendAndNotLastDayOfWeekend())
                .penalize("endOnNotLastDayOfWeekend", HardSoftScore.ONE_SOFT,
                        (employee, contract, shiftList) -> new EmployeeConsecutiveAssignmentEnd(employee,
                                shiftList.getItems().last()).getDistanceToLastDayOfWeekend() * contract.getWeight());
    }

    // Identical shiftTypes during a weekend
    Constraint identicalShiftTypesDuringWeekend(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BooleanContractLine.class)
                .filter(booleanContractLine -> booleanContractLine
                        .getContractLineType() == ContractLineType.IDENTICAL_SHIFT_TYPES_DURING_WEEKEND &&
                        booleanContractLine.isEnabled())
                .join(constraintFactory.from(ShiftDate.class)
                        .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY))
                .join(constraintFactory.from(ShiftAssignment.class).filter(ShiftAssignment::isWeekend),
                        Joiners.equal((contract, date) -> date.getWeekendSundayIndex(), ShiftAssignment::getWeekendSundayIndex),
                        Joiners.equal((contract, date) -> contract.getContract(), ShiftAssignment::getContract))
                .groupBy((contract, date, sa) -> contract,
                        (contract, date, sa) -> sa.getEmployee(),
                        (contract, date, sa) -> ImmutablePair.of(sa.getShiftType(), date), // No 4-key groupBy overload
                        ConstraintCollectors.countTri())
                .filter((contract, employee, type, count) -> count < employee.getWeekendLength())
                .penalize("identicalShiftTypesDuringWeekend", HardSoftScore.ONE_SOFT,
                        (contract, employee, type, count) -> (employee.getWeekendLength() - count) * contract.getWeight());
    }

    // Requested day on/off
    Constraint dayOffRequest(ConstraintFactory constraintFactory) {
        return constraintFactory.from(DayOffRequest.class)
                .join(ShiftAssignment.class, Joiners.equal(DayOffRequest::getEmployee, ShiftAssignment::getEmployee),
                        Joiners.equal(DayOffRequest::getShiftDate, ShiftAssignment::getShiftDate))
                .penalize("dayOffRequest", HardSoftScore.ONE_SOFT,
                        (dayOffRequest, shiftAssignment) -> dayOffRequest.getWeight());
    }

    Constraint dayOnRequest(ConstraintFactory constraintFactory) {
        return constraintFactory.from(DayOnRequest.class)
                .ifNotExists(ShiftAssignment.class, Joiners.equal(DayOnRequest::getEmployee, ShiftAssignment::getEmployee),
                        Joiners.equal(DayOnRequest::getShiftDate, ShiftAssignment::getShiftDate))
                .penalize("dayOnRequest", HardSoftScore.ONE_SOFT,
                        DayOnRequest::getWeight);
    }

    // Requested shift on/off
    Constraint shiftOffRequest(ConstraintFactory constraintFactory) {
        return constraintFactory.from(ShiftOffRequest.class)
                .join(ShiftAssignment.class, Joiners.equal(ShiftOffRequest::getEmployee, ShiftAssignment::getEmployee),
                        Joiners.equal(ShiftOffRequest::getShift, ShiftAssignment::getShift))
                .penalize("shiftOffRequest", HardSoftScore.ONE_SOFT,
                        (shiftOffRequest, shiftAssignment) -> shiftOffRequest.getWeight());
    }

    Constraint shiftOnRequest(ConstraintFactory constraintFactory) {
        return constraintFactory.from(ShiftOnRequest.class)
                .ifNotExists(ShiftAssignment.class, Joiners.equal(ShiftOnRequest::getEmployee, ShiftAssignment::getEmployee),
                        Joiners.equal(ShiftOnRequest::getShift, ShiftAssignment::getShift))
                .penalize("shiftOnRequest", HardSoftScore.ONE_SOFT,
                        ShiftOnRequest::getWeight);
    }

    // Alternative skill
    Constraint alternativeSkill(ConstraintFactory constraintFactory) {
        return constraintFactory.from(BooleanContractLine.class)
                .filter(booleanContractLine -> booleanContractLine.getContractLineType()
                        .equals(ContractLineType.ALTERNATIVE_SKILL_CATEGORY))
                .join(ShiftAssignment.class, Joiners.equal(BooleanContractLine::getContract, ShiftAssignment::getContract))
                .join(ShiftTypeSkillRequirement.class,
                        Joiners.equal((contract, shiftAssignment) -> shiftAssignment.getShiftType(),
                                ShiftTypeSkillRequirement::getShiftType))
                .ifNotExists(SkillProficiency.class,
                        Joiners.equal((contract, shiftAssignment, skillRequirement) -> shiftAssignment.getEmployee(),
                                SkillProficiency::getEmployee),
                        Joiners.equal((contract, shiftAssignment, skillRequirement) -> skillRequirement.getSkill(),
                                SkillProficiency::getSkill))
                .penalize("alternativeSkill", HardSoftScore.ONE_SOFT,
                        (contractLine, shiftAssignment, skillRequirement) -> contractLine.getWeight());
    }

    // Unwanted patterns
    Constraint unwantedPatternFreeBefore2DaysWithAWorkDayPattern(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PatternContractLine.class)
                .filter(patternContractLine -> patternContractLine.getPattern() instanceof FreeBefore2DaysWithAWorkDayPattern)
                .join(ShiftDate.class,
                        Joiners.equal(
                                contract -> ((FreeBefore2DaysWithAWorkDayPattern) contract.getPattern()).getFreeDayOfWeek(),
                                ShiftDate::getDayOfWeek))
                .join(Employee.class,
                        Joiners.equal((contractLine, date) -> contractLine.getContract(), Employee::getContract))
                .ifNotExists(ShiftAssignment.class,
                        Joiners.equal((contractLine, date, employee) -> employee, ShiftAssignment::getEmployee),
                        Joiners.equal((contractLine, date, employee) -> date.getDayIndex(),
                                ShiftAssignment::getShiftDateDayIndex))
                .ifExists(ShiftAssignment.class,
                        Joiners.equal((contractLine, date, employee) -> employee, ShiftAssignment::getEmployee),
                        Joiners.lessThanOrEqual((contractLine, date, employee) -> date.getDayIndex() + 1,
                                ShiftAssignment::getShiftDateDayIndex),
                        Joiners.greaterThanOrEqual((contractLine, date, employee) -> date.getDayIndex() + 2,
                                ShiftAssignment::getShiftDateDayIndex))
                .penalize("unwantedPatternFreeBefore2DaysWithAWorkDayPattern", HardSoftScore.ONE_SOFT,
                        (contractLine, date, employee) -> contractLine.getPattern().getWeight());
    }

    Constraint unwantedPatternShiftType2DaysPattern(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PatternContractLine.class)
                .filter(patternContractLine -> patternContractLine.getPattern() instanceof ShiftType2DaysPattern)
                .join(ShiftAssignment.class,
                        Joiners.equal(
                                contractLine -> ((ShiftType2DaysPattern) contractLine.getPattern()).getDayIndex0ShiftType(),
                                ShiftAssignment::getShiftType),
                        Joiners.equal(PatternContractLine::getContract, ShiftAssignment::getContract))
                .join(ShiftAssignment.class,
                        Joiners.equal((contractLine, shift) -> shift.getEmployee(), ShiftAssignment::getEmployee),
                        Joiners.equal((contractLine, shift) -> shift.getShiftDateDayIndex() + 1,
                                ShiftAssignment::getShiftDateDayIndex),
                        Joiners.filtering((contractLine, shift1, shift2) -> {
                            ShiftType2DaysPattern pattern = (ShiftType2DaysPattern) contractLine.getPattern();
                            return pattern.getDayIndex1ShiftType() == null
                                    || shift2.getShiftType() == pattern.getDayIndex1ShiftType();
                        }))
                .penalize("unwantedPatternShiftType2DaysPattern", HardSoftScore.ONE_SOFT,
                        (contractLine, shift1, shift2) -> contractLine.getPattern().getWeight());
    }

    Constraint unwantedPatternShiftType3DaysPattern(ConstraintFactory constraintFactory) {
        return constraintFactory.from(PatternContractLine.class) // ShiftType3DaysPattern
                .filter(patternContractLine -> patternContractLine.getPattern() instanceof ShiftType3DaysPattern)
                .join(ShiftAssignment.class,
                        Joiners.equal(
                                contractLine -> ((ShiftType3DaysPattern) contractLine.getPattern()).getDayIndex0ShiftType(),
                                ShiftAssignment::getShiftType),
                        Joiners.equal(PatternContractLine::getContract, ShiftAssignment::getContract))
                // Join and not if exist for consistency with DRL
                .join(ShiftAssignment.class,
                        Joiners.equal((contractLine, shift) -> shift.getEmployee(), ShiftAssignment::getEmployee),
                        Joiners.equal((contractLine, shift) -> shift.getShiftDateDayIndex() + 1,
                                ShiftAssignment::getShiftDateDayIndex),
                        Joiners.equal(
                                (contractLine, shift) -> ((ShiftType3DaysPattern) contractLine.getPattern())
                                        .getDayIndex1ShiftType(),
                                ShiftAssignment::getShiftType))
                .join(ShiftAssignment.class,
                        Joiners.equal((contractLine, shift1, shift2) -> shift1.getEmployee(),
                                ShiftAssignment::getEmployee),
                        Joiners.equal((contractLine, shift1, shift2) -> shift1.getShiftDateDayIndex() + 2,
                                ShiftAssignment::getShiftDateDayIndex),
                        Joiners.equal(
                                (contractLine, shift1, shift2) -> ((ShiftType3DaysPattern) contractLine.getPattern())
                                        .getDayIndex2ShiftType(),
                                ShiftAssignment::getShiftType))
                .penalize("unwantedPatternShiftType3DaysPattern", HardSoftScore.ONE_SOFT,
                        (contractLine, shift1, shift2, shift3) -> contractLine.getPattern().getWeight());
    }
}
