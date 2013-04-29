/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.identity;

import java.util.List;
import java.util.Map;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jbpm.services.task.annotations.Mvel;
import org.jbpm.services.task.exception.TaskException;
import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.kie.internal.task.api.model.Operation;

/**
 *
 */
@Decorator
public class UserGroupLifeCycleManagerDecorator extends AbstractUserGroupCallbackDecorator implements LifeCycleManager {


    @Inject
    @Delegate
    @Mvel
    private LifeCycleManager manager;

    public UserGroupLifeCycleManagerDecorator() {
    }

    public void setManager(LifeCycleManager manager) {
        this.manager = manager;
    }

   

    public LifeCycleManager getManager() {
        return manager;
    }

    public void taskOperation(Operation operation, long taskId, String userId, String targetEntityId, Map<String, Object> data, List<String> groupIds) throws TaskException {
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        doCallbackUserOperation(targetEntityId);
        manager.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds);

    }

}
