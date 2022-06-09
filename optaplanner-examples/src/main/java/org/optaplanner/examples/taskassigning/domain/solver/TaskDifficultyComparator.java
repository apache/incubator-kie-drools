package org.optaplanner.examples.taskassigning.domain.solver;

import java.util.Comparator;

import org.optaplanner.examples.taskassigning.domain.Task;

/**
 * Compares tasks by difficulty.
 */
public class TaskDifficultyComparator implements Comparator<Task> {
    // FIXME This class is currently unused until the @PlanningListVariable(comparator = ???) API is stable.
    //  See https://issues.redhat.com/browse/PLANNER-2542.

    static final Comparator<Task> INCREASING_DIFFICULTY_COMPARATOR = Comparator.comparing(Task::getPriority)
            .thenComparingInt(task -> task.getTaskType().getRequiredSkillList().size())
            .thenComparingInt(task -> task.getTaskType().getBaseDuration())
            .thenComparingLong(Task::getId);

    static final Comparator<Task> DECREASING_DIFFICULTY_COMPARATOR = INCREASING_DIFFICULTY_COMPARATOR.reversed();

    @Override
    public int compare(Task a, Task b) {
        return DECREASING_DIFFICULTY_COMPARATOR.compare(a, b);
    }
}
