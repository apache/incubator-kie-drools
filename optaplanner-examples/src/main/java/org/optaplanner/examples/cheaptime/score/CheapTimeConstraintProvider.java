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

package org.optaplanner.examples.cheaptime.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sumLong;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThanOrEqual;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThanOrEqual;
import static org.optaplanner.examples.cheaptime.score.CheapTimeCostCalculator.multiplyTwoMicros;
import static org.optaplanner.examples.common.experimental.ExperimentalConstraintCollectors.consecutiveIntervals;

import java.util.function.Function;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Period;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo;

public class CheapTimeConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                startTimeLimitsFrom(constraintFactory),
                startTimeLimitsTo(constraintFactory),
                maximumCapacity(constraintFactory),
                activeMachinePowerCost(constraintFactory),
                activeMachineSpinUpAndDownCost(constraintFactory),
                idleCosts(constraintFactory),
                taskPowerCost(constraintFactory),
                startEarly(constraintFactory)
        };
    }

    protected Constraint startTimeLimitsFrom(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> taskAssignment.getStartPeriod() < taskAssignment.getTask().getStartPeriodRangeFrom())
                .penalizeLong("Task starts too early", HardMediumSoftLongScore.ONE_HARD,
                        taskAssignment -> taskAssignment.getTask().getStartPeriodRangeFrom() - taskAssignment.getStartPeriod());
    }

    protected Constraint startTimeLimitsTo(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .filter(taskAssignment -> taskAssignment.getStartPeriod() >= taskAssignment.getTask().getStartPeriodRangeTo())
                .penalizeLong("Task starts too late", HardMediumSoftLongScore.ONE_HARD,
                        taskAssignment -> taskAssignment.getStartPeriod() - taskAssignment.getTask().getStartPeriodRangeTo());
    }

    protected Constraint maximumCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .join(Resource.class,
                        filtering((taskAssignment, resource) -> taskAssignment.getTask().getUsage(resource) > 0))
                .join(Period.class,
                        lessThanOrEqual((taskAssignment, resource) -> taskAssignment.getStartPeriod(), Period::getIndex),
                        greaterThan((taskAssignment, resource) -> taskAssignment.getEndPeriod(), Period::getIndex))
                .groupBy((taskAssignment, resource, period) -> period,
                        (taskAssignment, resource, period) -> resource,
                        (taskAssignment, resource, period) -> taskAssignment.getMachine(),
                        sum((taskAssignment, resource, period) -> taskAssignment.getTask().getUsage(resource)))
                .filter((period, resource, machine, usage) -> machine.getCapacity(resource) < usage)
                .penalizeLong("Maximum resource capacity", HardMediumSoftLongScore.ONE_HARD,
                        (period, resource, machine, usage) -> usage - machine.getCapacity(resource));
    }

    protected Constraint activeMachinePowerCost(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Period.class)
                .join(Machine.class)
                .ifExists(TaskAssignment.class,
                        equal((period, machine) -> machine, TaskAssignment::getMachine),
                        greaterThanOrEqual((period, machine) -> period.getIndex(), TaskAssignment::getStartPeriod),
                        lessThan((period, machine) -> period.getIndex(), TaskAssignment::getEndPeriod))
                .penalizeLong("Active machine power cost", HardMediumSoftLongScore.ONE_MEDIUM,
                        (period, machine) -> multiplyTwoMicros(machine.getPowerConsumptionMicros(),
                                period.getPowerPriceMicros()));
    }

    protected Constraint activeMachineSpinUpAndDownCost(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Machine.class)
                .ifExists(TaskAssignment.class,
                        equal(Function.identity(), TaskAssignment::getMachine))
                .penalizeLong("Active machine spin up and down cost", HardMediumSoftLongScore.ONE_MEDIUM,
                        Machine::getSpinUpDownCostMicros);
    }

    protected Constraint idleCosts(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .groupBy(TaskAssignment::getMachine,
                        consecutiveIntervals(TaskAssignment::getStartPeriod, TaskAssignment::getEndPeriod, (a, b) -> b - a))
                .flattenLast(ConsecutiveIntervalInfo::getBreaks)
                .join(Period.class,
                        lessThanOrEqual((machine, brk) -> brk.getPreviousIntervalClusterEnd(), Period::getIndex),
                        greaterThan((machine, brk) -> brk.getNextIntervalClusterStart(), Period::getIndex))
                .groupBy((machine, brk, idlePeriod) -> machine,
                        (machine, brk, idlePeriod) -> brk,
                        sumLong((machine, brk, idlePeriod) -> idlePeriod.getPowerPriceMicros()))
                .penalizeLong("Machine idle costs", HardMediumSoftLongScore.ONE_MEDIUM,
                        (machine, brk, powerCost) -> {
                            long idleCost = multiplyTwoMicros(machine.getPowerConsumptionMicros(), powerCost);
                            // Shutting down and restarting the machine may be cheaper than keeping it idle.
                            return Math.min(idleCost, machine.getSpinUpDownCostMicros());
                        });
    }

    protected Constraint taskPowerCost(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .join(Period.class,
                        lessThanOrEqual(TaskAssignment::getStartPeriod, Period::getIndex),
                        greaterThan(TaskAssignment::getEndPeriod, Period::getIndex))
                .penalizeLong("Task power cost", HardMediumSoftLongScore.ONE_MEDIUM,
                        (taskAssignment, period) -> multiplyTwoMicros(taskAssignment.getTask().getPowerConsumptionMicros(),
                                period.getPowerPriceMicros()));
    }

    protected Constraint startEarly(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(TaskAssignment.class)
                .penalize("Prefer early task start", HardMediumSoftLongScore.ONE_SOFT,
                        TaskAssignment::getStartPeriod);
    }

}
