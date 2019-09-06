package org.optaplanner.examples.taskassigning.solver.score;

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.examples.taskassigning.domain.Priority;
import org.optaplanner.examples.taskassigning.domain.Task;

public final class TaskAssigningConstraintProvider implements ConstraintProvider {

    private static final int BENDABLE_SCORE_HARD_LEVELS_SIZE = 1;
    private static final int BENDABLE_SCORE_SOFT_LEVELS_SIZE = 4;

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                noMissingSkills(constraintFactory),
                minimizeMakespan(constraintFactory),
                /*
                 * TODO potential for performance improvements through API enhancements,
                 *  see https://issues.jboss.org/browse/PLANNER-1604.
                 */
                criticalPriorityBasedTaskEndTime(constraintFactory),
                majorPriorityTaskEndTime(constraintFactory),
                minorPriorityTaskEndTime(constraintFactory)
        };
    }

    private UniConstraintStream<Task> getTaskWithPriority(ConstraintFactory constraintFactory, Priority priority) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getPriority() == priority);
    }

    private Constraint noMissingSkills(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getMissingSkillCount() > 0)
                .penalize("No missing skills",
                        BendableScore.ofHard(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 0, 1),
                        Task::getMissingSkillCount);
    }

    private Constraint criticalPriorityBasedTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.CRITICAL)
                .penalize("Critical priority task end time",
                        BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 0, 1),
                        Task::getEndTime);
    }

    private Constraint minimizeMakespan(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Task.class)
                .filter(task -> task.getNextTask() == null)
                .penalize("Minimize makespan, latest ending employee first",
                        BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 1, 1),
                        task -> task.getEndTime() * task.getEndTime());
    }

    private Constraint majorPriorityTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.MAJOR)
                .penalize("Major priority task end time",
                        BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 2, 1),
                        Task::getEndTime);
    }

    private Constraint minorPriorityTaskEndTime(ConstraintFactory constraintFactory) {
        return getTaskWithPriority(constraintFactory, Priority.MINOR)
                .penalize("Minor priority task end time",
                        BendableScore.ofSoft(BENDABLE_SCORE_HARD_LEVELS_SIZE, BENDABLE_SCORE_SOFT_LEVELS_SIZE, 3, 1),
                        Task::getEndTime);
    }
}
