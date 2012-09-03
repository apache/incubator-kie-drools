/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.identity;

import org.jbpm.task.annotations.Mvel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.drools.core.util.StringUtils;

import org.jbpm.task.Group;
import org.jbpm.task.Operation;
import org.jbpm.task.User;
import org.jbpm.task.annotations.TaskPersistence;
import org.jbpm.task.exception.TaskException;
import org.jbpm.task.internals.lifecycle.LifeCycleManager;

/**
 *
 */
@Decorator
public abstract class UserGroupLifeCycleManagerDecorator implements LifeCycleManager {

    @Inject
    @Delegate
    @Mvel
    private LifeCycleManager manager;
    @Inject @TaskPersistence
    private EntityManager em;
    @Inject
    private UserGroupCallback userGroupCallback;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();

    public void taskOperation(Operation operation, long taskId, String userId, String targetEntityId, Map<String, Object> data, List<String> groupIds) throws TaskException {
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        doCallbackUserOperation(targetEntityId);
        manager.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds);

    }

    private List<String> doUserGroupCallbackOperation(String userId, List<String> groupIds) {

        doCallbackUserOperation(userId);
        doCallbackGroupsOperation(userId, groupIds);

        return userGroupCallback.getGroupsForUser(userId, groupIds, null);

    }

    private boolean doCallbackUserOperation(String userId) {

        if (userId != null && userGroupCallback.existsUser(userId)) {
            addUserFromCallbackOperation(userId);
            return true;
        }
        return false;

    }

    private boolean doCallbackGroupOperation(String groupId) {

        if (groupId != null && userGroupCallback.existsGroup(groupId)) {
            addGroupFromCallbackOperation(groupId);
            return true;
        }
        return false;

    }

    private void addUserFromCallbackOperation(String userId) {
        try {
            boolean userExists = em.find(User.class, userId) != null;
            if (!StringUtils.isEmpty(userId) && !userExists) {
                User user = new User(userId);
                em.persist(user);
            }
        } catch (Throwable t) {
            //logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }

    private void doCallbackGroupsOperation(String userId, List<String> groupIds) {

        if (userId != null) {

            if (groupIds != null && groupIds.size() > 0) {

                List<String> userGroups = userGroupCallback.getGroupsForUser(userId, groupIds, null);
                for (String groupId : groupIds) {

                    if (userGroupCallback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            } else {
                if (!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) {
                    List<String> userGroups = userGroupCallback.getGroupsForUser(userId, null, null);
                    if (userGroups != null && userGroups.size() > 0) {
                        for (String group : userGroups) {
                            addGroupFromCallbackOperation(group);
                        }
                        userGroupsMap.put(userId, true);
                    }
                }
            }
        } else {
            if (groupIds != null) {
                for (String groupId : groupIds) {
                    addGroupFromCallbackOperation(groupId);
                }
            }
        }

    }

    private void addGroupFromCallbackOperation(String groupId) {
        try {
            boolean groupExists = em.find(Group.class, groupId) != null;
            if (!StringUtils.isEmpty(groupId) && !groupExists) {
                Group group = new Group(groupId);
                em.persist(group);
            }
        } catch (Throwable t) {
            //logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
        }
    }
}
