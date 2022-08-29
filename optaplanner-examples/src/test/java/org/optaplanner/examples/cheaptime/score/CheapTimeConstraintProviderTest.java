package org.optaplanner.examples.cheaptime.score;

import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.MachineCapacity;
import org.optaplanner.examples.cheaptime.domain.Period;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class CheapTimeConstraintProviderTest extends
        AbstractConstraintProviderTest<CheapTimeConstraintProvider, CheapTimeSolution> {

    private static final Period PERIOD_0 = new Period(0, 100_000);
    private static final Period PERIOD_1 = new Period(1, 150_000);
    private static final Period PERIOD_2 = new Period(2, 250_000);
    private static final Period PERIOD_3 = new Period(3, 500_000);
    private static final Resource RESOURCE_0 = new Resource(0);
    private static final Resource RESOURCE_1 = new Resource(1);
    private static final Resource RESOURCE_2 = new Resource(2);
    private static final MachineCapacity CAPACITY_MACHINE0_RESOURCE0 = new MachineCapacity(0, RESOURCE_0, 1);
    private static final MachineCapacity CAPACITY_MACHINE0_RESOURCE1 = new MachineCapacity(1, RESOURCE_1, 2);
    private static final MachineCapacity CAPACITY_MACHINE0_RESOURCE2 = new MachineCapacity(2, RESOURCE_2, 3);
    private static final MachineCapacity CAPACITY_MACHINE1_RESOURCE0 = new MachineCapacity(3, RESOURCE_0, 0);
    private static final MachineCapacity CAPACITY_MACHINE1_RESOURCE1 = new MachineCapacity(4, RESOURCE_1, 1);
    private static final MachineCapacity CAPACITY_MACHINE1_RESOURCE2 = new MachineCapacity(5, RESOURCE_2, 2);
    private static final Machine MACHINE_O = new Machine(0, 1_000_000, 5,
            CAPACITY_MACHINE0_RESOURCE0, CAPACITY_MACHINE0_RESOURCE1, CAPACITY_MACHINE0_RESOURCE2);
    private static final Machine MACHINE_1 = new Machine(1, 2_000_000, 10,
            CAPACITY_MACHINE1_RESOURCE0, CAPACITY_MACHINE1_RESOURCE1, CAPACITY_MACHINE1_RESOURCE2);
    private static final TaskRequirement REQUIREMENT_TASK0_RESOURCE0 = new TaskRequirement(0, RESOURCE_0, 1);
    private static final TaskRequirement REQUIREMENT_TASK0_RESOURCE1 = new TaskRequirement(1, RESOURCE_1, 2);
    private static final TaskRequirement REQUIREMENT_TASK0_RESOURCE2 = new TaskRequirement(2, RESOURCE_2, 0);
    private static final TaskRequirement REQUIREMENT_TASK1_RESOURCE0 = new TaskRequirement(3, RESOURCE_0, 0);
    private static final TaskRequirement REQUIREMENT_TASK1_RESOURCE1 = new TaskRequirement(4, RESOURCE_1, 1);
    private static final TaskRequirement REQUIREMENT_TASK1_RESOURCE2 = new TaskRequirement(5, RESOURCE_2, 2);
    private static final TaskRequirement REQUIREMENT_TASK2_RESOURCE0 = new TaskRequirement(6, RESOURCE_0, 1);
    private static final TaskRequirement REQUIREMENT_TASK2_RESOURCE1 = new TaskRequirement(7, RESOURCE_1, 0);
    private static final TaskRequirement REQUIREMENT_TASK2_RESOURCE2 = new TaskRequirement(8, RESOURCE_2, 2);
    private static final Task TASK_0 = new Task(0, PERIOD_0, PERIOD_1, 2, 1_000_000,
            REQUIREMENT_TASK0_RESOURCE0, REQUIREMENT_TASK0_RESOURCE1, REQUIREMENT_TASK0_RESOURCE2);
    private static final Task TASK_1 = new Task(1, PERIOD_1, PERIOD_2, 1, 2_000_000,
            REQUIREMENT_TASK1_RESOURCE0, REQUIREMENT_TASK1_RESOURCE1, REQUIREMENT_TASK1_RESOURCE2);
    private static final Task TASK_2 = new Task(2, PERIOD_1, PERIOD_3, 1, 3_000_000,
            REQUIREMENT_TASK2_RESOURCE0, REQUIREMENT_TASK2_RESOURCE1, REQUIREMENT_TASK2_RESOURCE2);

    @ConstraintProviderTest
    void startTimeLimitsFrom(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        // Actual task assignment falls on the minimum prescribed by the task.
        TaskAssignment correctTaskAssignment1 = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        // Actual task assignment falls past the minimum prescribed by the task.
        TaskAssignment correctTaskAssignment2 = new TaskAssignment(TASK_1, MACHINE_O, PERIOD_2);
        // Actual assignment start is before the minimum prescribed by the task.
        TaskAssignment wrongTaskAssignment = new TaskAssignment(TASK_2, MACHINE_O, PERIOD_0);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::startTimeLimitsFrom)
                .given(correctTaskAssignment1, correctTaskAssignment2, wrongTaskAssignment)
                .penalizesBy(1); // Wrong task assignment is penalized by one period.
    }

    @ConstraintProviderTest
    void startTimeLimitsTo(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        // Actual task assignment falls on the maximum prescribed by the task.
        TaskAssignment correctTaskAssignment1 = new TaskAssignment(TASK_2, MACHINE_O, PERIOD_3);
        // Actual task assignment falls before the maximum prescribed by the task.
        TaskAssignment correctTaskAssignment2 = new TaskAssignment(TASK_1, MACHINE_O, PERIOD_1);
        // Actual assignment start is after the maximum prescribed by the task.
        TaskAssignment wrongTaskAssignment = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_2);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::startTimeLimitsTo)
                .given(correctTaskAssignment1, correctTaskAssignment2, wrongTaskAssignment)
                .penalizesBy(1); // Wrong task assignment is penalized by one period.
    }

    @ConstraintProviderTest
    void maximumCapacity(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        TaskAssignment taskAssignment1 = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        TaskAssignment taskAssignment2 = new TaskAssignment(TASK_1, MACHINE_O, PERIOD_1);
        TaskAssignment taskAssignment3 = new TaskAssignment(TASK_2, MACHINE_1, PERIOD_1);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::maximumCapacity)
                .given(taskAssignment1, taskAssignment2, taskAssignment3,
                        RESOURCE_0, RESOURCE_1, RESOURCE_2, PERIOD_0, PERIOD_1, PERIOD_2, PERIOD_3)
                .penalizesBy(2);
    }

    @ConstraintProviderTest
    void activeMachinePowerCost(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        // Machines with task assignments are penalized based on task duration.
        TaskAssignment taskAssignment = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::activeMachinePowerCost)
                .given(taskAssignment, MACHINE_O, MACHINE_1, PERIOD_0, PERIOD_1, PERIOD_2, PERIOD_3)
                /*
                 * Machine 0 runs during periods 0 and 1, therefore period power costs are 100_000 and 150_000.
                 * Power consumption of machine 0 is 1_000_000 per period.
                 * Therefore round(1.5 + 1.0) = 3.
                 * Machine 1 is not on, as there are no task assignments for it.
                 */
                .penalizesBy(3);
    }

    @ConstraintProviderTest
    void activeMachineSpinUpAndDownCost(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        TaskAssignment taskAssignment = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::activeMachineSpinUpAndDownCost)
                .given(taskAssignment, MACHINE_O, MACHINE_1, PERIOD_0, PERIOD_1, PERIOD_2, PERIOD_3)
                /*
                 * Machine 0 runs, costing 5 to spin up and then down.
                 * Machine 1 does not run.
                 */
                .penalizesBy(5);
    }

    @ConstraintProviderTest
    void idleCosts(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        // When a machine is on without a task, we incur a cost.
        TaskAssignment taskAssignment1 = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        TaskAssignment taskAssignment2 = new TaskAssignment(TASK_2, MACHINE_O, PERIOD_3);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::idleCosts)
                .given(taskAssignment1, taskAssignment2, PERIOD_0, PERIOD_1, PERIOD_2, PERIOD_3)
                .penalizesBy(3); // Between tasks 0 and 2, there is an idle period 2.

    }

    @ConstraintProviderTest
    void taskPowerCost(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        TaskAssignment taskAssignment1 = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_0);
        TaskAssignment taskAssignment2 = new TaskAssignment(TASK_1, MACHINE_1, PERIOD_1);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::taskPowerCost)
                .given(taskAssignment1, taskAssignment2, PERIOD_0, PERIOD_1, PERIOD_2, PERIOD_3)
                /*
                 * Task 0 runs during periods 0 and 1,
                 * Task 1 runs during period 1.
                 * Therefore round(1 + 1.5 + 3) = 6.
                 */
                .penalizesBy(6);
    }

    @ConstraintProviderTest
    void startEarly(ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> constraintVerifier) {
        TaskAssignment taskAssignment1 = new TaskAssignment(TASK_0, MACHINE_O, PERIOD_1);
        TaskAssignment taskAssignment2 = new TaskAssignment(TASK_1, MACHINE_1, PERIOD_2);
        constraintVerifier.verifyThat(CheapTimeConstraintProvider::startEarly)
                .given(taskAssignment1, taskAssignment2)
                .penalizesBy(taskAssignment1.getStartPeriod() + taskAssignment2.getStartPeriod());
    }

    @Override
    protected ConstraintVerifier<CheapTimeConstraintProvider, CheapTimeSolution> createConstraintVerifier() {
        return ConstraintVerifier.build(new CheapTimeConstraintProvider(), CheapTimeSolution.class, TaskAssignment.class);
    }

}
