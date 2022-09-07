package org.optaplanner.examples.taskassigning.score;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Task;

public final class TaskAssigningConstraintProvider implements ConstraintProvider {

    private static final int BENDABLE_SCORE_HARD_LEVELS_SIZE = 1;
    private static final int BENDABLE_SCORE_SOFT_LEVELS_SIZE = 4;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                noMissingSkills(constraintFactory),
                minimizeMakespan(constraintFactory),
                /*
                 * TODO potential for performance improvements through API enhancements,
                 * see https://issues.redhat.com/browse/PLANNER-1604.
                 */
                criticalPriorityBasedTaskEndTime(constraintFactory),
                majorPriorityTaskEndTime(constraintFactory),
                minorPriorityTaskEndTime(constraintFactory)
        };
    }

    private UniConstraintStream<Task> getTaskWithPriority(ConstraintFactory constraintFactory, Priority priority) {
        return constraintFactory.forEach(Task.class)
                .filter(task -> task.getEmployee() != null && task.getPriority() == priority);
    }

    private Constraint noMissingSkills(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Task.class)
                .filter(task -> task.getMissingSkillCount() > 0)
                .penalize(BendableScore.ofHard(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 0, 1),
                        Task::getMissingSkillCount)
                .asConstraint("No missing skills");
    }

    private Constraint criticalPriorityBasedTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.CRITICAL)
                .penalize(BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 0, 1),
                        Task::getEndTime)
                .asConstraint("Critical priority task end time");
    }

    private Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Employee.class)
                .penalize(BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 1, 1),
                        employee -> employee.getEndTime() * employee.getEndTime())
                .asConstraint("Minimize makespan, latest ending employee first");
    }

    private Constraint majorPriorityTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.MAJOR)
                .penalize(BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 2, 1),
                        Task::getEndTime)
                .asConstraint("Major priority task end time");
    }

    private Constraint minorPriorityTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.MINOR)
                .penalize(BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 3, 1),
                        Task::getEndTime)
                .asConstraint("Minor priority task end time");
    }
}
