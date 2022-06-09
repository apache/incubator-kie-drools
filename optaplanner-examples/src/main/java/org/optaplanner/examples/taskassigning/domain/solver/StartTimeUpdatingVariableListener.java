package org.optaplanner.examples.taskassigning.domain.solver;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

public class StartTimeUpdatingVariableListener implements VariableListener<TaskAssigningSolution, Task> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        updateStartTime(scoreDirector, task);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        updateStartTime(scoreDirector, task);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        // Do nothing
    }

    protected void updateStartTime(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        Employee employee = task.getEmployee();
        if (employee == null) {
            return;
        }
        Integer index = task.getIndex();
        List<Task> tasks = employee.getTasks();
        Integer previousEndTime = index == 0 ? Integer.valueOf(0) : tasks.get(index - 1).getEndTime();

        for (int i = index; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            Integer startTime = calculateStartTime(t, previousEndTime);
            if (!Objects.equals(t.getStartTime(), startTime)) {
                scoreDirector.beforeVariableChanged(t, "startTime");
                t.setStartTime(startTime);
                scoreDirector.afterVariableChanged(t, "startTime");
            }
            previousEndTime = t.getEndTime();
        }
    }

    private Integer calculateStartTime(Task task, Integer previousEndTime) {
        if (previousEndTime == null) {
            return null;
        }
        return Math.max(task.getReadyTime(), previousEndTime);
    }

}
