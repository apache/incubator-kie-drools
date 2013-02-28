/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.identity;

import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jbpm.task.Operation;
import org.jbpm.task.annotations.Mvel;
import org.jbpm.task.exception.TaskException;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;

/**
 *
 */
@Decorator
public abstract class UserGroupLifeCycleManagerDecorator extends AbstractUserGroupCallbackDecorator implements LifeCycleManager {

    @Inject
    @Delegate
    @Mvel
    private LifeCycleManager manager;


    public void taskOperation(Operation operation, long taskId, String userId, String targetEntityId, Map<String, Object> data, List<String> groupIds) throws TaskException {
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        doCallbackUserOperation(targetEntityId);
        manager.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds);

    }
}
