/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.internals.lifecycle;


import java.util.List;
import java.util.Map;

import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.exception.TaskException;

/**
 *
 */
public interface LifeCycleManager {
    public void taskOperation(final Operation operation, final long taskId, final String userId,
                              final String targetEntityId, final Map<String, Object> data,
                              List<String> groupIds) throws TaskException;
}
