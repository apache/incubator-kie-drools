/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.TaskDef;

/**
 *
 * @author salaboy
 */
public interface TaskDefService {

    public void deployTaskDef(TaskDef def);

    public List<TaskDef> getAllTaskDef(String filter);

    public TaskDef getTaskDefById(String id);

    public void undeployTaskDef(String id);
}
