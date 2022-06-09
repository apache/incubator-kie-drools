package org.optaplanner.examples.cheaptime.domain.solver;

import java.util.Comparator;

import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;

public class TaskAssignmentDifficultyComparator implements Comparator<TaskAssignment> {

    private static final Comparator<Task> TASK_COMPARATOR = Comparator.comparingInt(Task::getResourceUsageMultiplicand)
            .thenComparingLong(Task::getPowerConsumptionMicros)
            .thenComparingInt(Task::getDuration);
    private static final Comparator<TaskAssignment> COMPARATOR = Comparator.comparing(TaskAssignment::getTask, TASK_COMPARATOR)
            .thenComparingLong(TaskAssignment::getId);

    @Override
    public int compare(TaskAssignment a, TaskAssignment b) {
        return COMPARATOR.compare(a, b);
    }
}
