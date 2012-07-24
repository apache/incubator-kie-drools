/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals.lifecycle;


import java.util.List;
import java.util.Map;
import org.jbpm.task.Operation;
import org.jbpm.task.exception.TaskException;

/**
 *
 * @author salaboy
 */
public interface LifeCycleManager {
    public void taskOperation(final Operation operation, final long taskId, final String userId,
                              final String targetEntityId, final Map<String, Object> data,
                              List<String> groupIds) throws TaskException;
}
