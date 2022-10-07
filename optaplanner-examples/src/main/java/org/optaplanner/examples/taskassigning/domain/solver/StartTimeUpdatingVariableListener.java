package org.optaplanner.examples.taskassigning.domain.solver;

import java.util.List;
import java.util.Objects;

import org.optaplanner.core.api.domain.variable.ListVariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

public class StartTimeUpdatingVariableListener implements ListVariableListener<TaskAssigningSolution, Employee, Task> {

    @Override
    public void beforeEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee) {
        throw new UnsupportedOperationException("This example does not support adding employees.");
    }

    @Override
    public void afterEntityAdded(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee) {
        throw new UnsupportedOperationException("This example does not support adding employees.");
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee) {
        throw new UnsupportedOperationException("This example does not support removing employees.");
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee) {
        throw new UnsupportedOperationException("This example does not support removing employees.");
    }

    @Override
    public void afterListVariableElementUnassigned(ScoreDirector<TaskAssigningSolution> scoreDirector, Task task) {
        scoreDirector.beforeVariableChanged(task, "startTime");
        task.setStartTime(null);
        scoreDirector.afterVariableChanged(task, "startTime");
    }

    @Override
    public void beforeListVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee, int startIndex,
            int endIndex) {
        // Do nothing
    }

    @Override
    public void afterListVariableChanged(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee, int startIndex,
            int endIndex) {
        updateStartTime(scoreDirector, employee, startIndex);
    }

    protected void updateStartTime(ScoreDirector<TaskAssigningSolution> scoreDirector, Employee employee, int index) {
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
