/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.identity;

import java.util.List;
import java.util.Map;

import org.jbpm.services.task.internals.lifecycle.LifeCycleManager;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.exception.TaskException;

/**
 *
 */
public class UserGroupLifeCycleManagerDecorator implements LifeCycleManager {

	private UserGroupCallback userGroupCallback;
	private LifeCycleManager manager;

    public UserGroupLifeCycleManagerDecorator() {
    }
    
    public UserGroupLifeCycleManagerDecorator(UserGroupCallback callback, LifeCycleManager manager) {
    	this.userGroupCallback = callback;
    	this.manager = manager;
    }

    public void setManager(LifeCycleManager manager) {
        this.manager = manager;
    }

    public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
		this.userGroupCallback = userGroupCallback;
	}

    public LifeCycleManager getManager() {
        return manager;
    }

    public void taskOperation(Operation operation, long taskId, String userId, String targetEntityId, 
    		Map<String, Object> data, List<String> groupIds, OrganizationalEntity...entities) throws TaskException {
        groupIds = userGroupCallback.getGroupsForUser(userId, groupIds, null);

        manager.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds, entities);

    }

}
