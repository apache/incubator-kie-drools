package org.kie.internal.task.api;

import java.util.List;

import org.kie.internal.task.api.model.TaskDef;

/**
 * Experimental:
 *  The Task Definition Service is intended to keep
 *   meta information about a Task. This meta information
 *   can be used as a Task Template, to reuse the same
 *   template in different places without redefining the
 *   Task Structure
 */
public interface TaskDefService {

    public void deployTaskDef(TaskDef def);

    public List<TaskDef> getAllTaskDef(String filter);

    public TaskDef getTaskDefById(String id);

    public void undeployTaskDef(String id);
}
