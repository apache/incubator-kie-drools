package org.optaplanner.examples.cheaptime.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sumLong;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.overlapping;
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
                        overlapping((taskAssignment, resource) -> taskAssignment.getStartPeriod(),
                                (taskAssignment, resource) -> taskAssignment.getEndPeriod(),
                                Period::getIndex, period -> period.getIndex() + 1))
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
                        overlapping((period, machine) -> period.getIndex(), (period, machine) -> period.getIndex() + 1,
                                TaskAssignment::getStartPeriod, TaskAssignment::getEndPeriod))
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
                        overlapping((machine, brk) -> brk.getPreviousIntervalClusterEnd(),
                                (machine, brk) -> brk.getNextIntervalClusterStart(),
                                Period::getIndex, period -> period.getIndex() + 1))
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
                        overlapping(TaskAssignment::getStartPeriod, TaskAssignment::getEndPeriod,
                                Period::getIndex, period -> period.getIndex() + 1))
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
