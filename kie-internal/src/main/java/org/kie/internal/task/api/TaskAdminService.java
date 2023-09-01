package org.kie.internal.task.api;

import java.util.Date;
import java.util.List;

import org.kie.api.task.model.TaskSummary;


/**
 * The Task Admintration Service is intended to provide
 *  administrative functions such as:
 *    - Remove and Archive Tasks

 */
public interface TaskAdminService {

    public List<TaskSummary> getActiveTasks();

    public List<TaskSummary> getActiveTasks(Date since);

    public List<TaskSummary> getCompletedTasks();

    public List<TaskSummary> getCompletedTasks(Date since);

    public List<TaskSummary> getCompletedTasksByProcessId(Long processId);

    public int archiveTasks(List<TaskSummary> tasks);

    public List<TaskSummary> getArchivedTasks();

    public int removeTasks(List<TaskSummary> tasks);

    public int removeAllTasks();
}
