/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.internals.lifecycle;

import org.jbpm.task.annotations.Mvel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.drools.core.util.StringUtils;

import org.jbpm.task.Group;
import org.jbpm.task.Operation;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;
import org.jbpm.task.annotations.Log;
import org.jbpm.task.exception.TaskException;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupCallbackManager;


/**
 *
 */
@Decorator
public abstract class UserGroupLifeCycleManagerDecorator implements LifeCycleManager{
    private @Inject @Delegate @Mvel LifeCycleManager manager;
    private @Inject EntityManager em;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();
    
    public void taskOperation(Operation operation, long taskId, String userId, String targetEntityId, Map<String, Object> data, List<String> groupIds) throws TaskException {
        OrganizationalEntity targetEntity = null;
        groupIds = doUserGroupCallbackOperation(userId, groupIds);
        doCallbackUserOperation(targetEntityId);
        if (targetEntityId != null) {
            targetEntity = em.find(OrganizationalEntity.class, targetEntityId);
        }
        manager.taskOperation(operation, taskId, userId, targetEntityId, data, groupIds);
        
    }
    
    private List<String> doUserGroupCallbackOperation(String userId, List<String> groupIds) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            doCallbackUserOperation(userId);
            doCallbackGroupsOperation(userId, groupIds);
            List<String> allGroupIds = null;
            if (UserGroupCallbackManager.getInstance().getProperty("disable.all.groups") == null) {
                // get all groups
                // (The fact that this isn't done in a query will probably become a problem at some point.. )
                Query query = em.createQuery("select g.id from Group g");
    			allGroupIds = ((List<String>) query.getResultList());
            }
            return UserGroupCallbackManager.getInstance().getCallback().getGroupsForUser(userId, groupIds, allGroupIds);
        } else {
            //logger.debug("UserGroupCallback has not been registered.");
            return groupIds;
        }
    }
    
    private boolean doCallbackUserOperation(String userId) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(userId != null && UserGroupCallbackManager.getInstance().getCallback().existsUser(userId)) {
                addUserFromCallbackOperation(userId);
                return true;
            }
            return false;
        } else {
            //logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
            // returns true for backward compatibility
            return true;
        }
    }
    private boolean doCallbackGroupOperation(String groupId) {
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(groupId != null && UserGroupCallbackManager.getInstance().getCallback().existsGroup(groupId)) {
                addGroupFromCallbackOperation(groupId);
                return true;
            }
            return false;
        } else {
            //logger.debug("UserGroupCallback has not been registered.");
            // returns true for backward compatibility
            return true;
        }
    }
     private void addUserFromCallbackOperation(String userId) { 
        try {
            boolean userExists = em.find(User.class, userId) != null;
            if( ! StringUtils.isEmpty(userId) && ! userExists ) {
                User user = new User(userId);
                em.persist(user);
            }
        } catch (Throwable t) {
            //logger.log(Level.SEVERE, "Unable to add user " + userId);
        }
    }
     private void doCallbackGroupsOperation(String userId, List<String> groupIds) { 
        if(UserGroupCallbackManager.getInstance().existsCallback()) {
            if(userId != null) {
                UserGroupCallback callback = UserGroupCallbackManager.getInstance().getCallback();
                if(groupIds != null && groupIds.size() > 0) {
                    
                	List<String> userGroups = callback.getGroupsForUser(userId, groupIds, null);
                    for(String groupId : groupIds) {
                        
                        if(callback.existsGroup(groupId) && userGroups != null && userGroups.contains(groupId)) {
                            addGroupFromCallbackOperation(groupId);
                        }
                    }
                } else {
                    if(!(userGroupsMap.containsKey(userId) && userGroupsMap.get(userId).booleanValue())) { 
                        List<String> userGroups = callback.getGroupsForUser(userId, null, null);
                        if(userGroups != null && userGroups.size() > 0) {
                            for(String group : userGroups) {
                                addGroupFromCallbackOperation(group);
                            }
                            userGroupsMap.put(userId, true);
                        }
                    }
                }
            } else {
                if(groupIds != null) {
                    for(String groupId : groupIds) {
                        addGroupFromCallbackOperation(groupId);
                    }
                }
            }
        } else {
            //logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
        }
    }
     
    private void addGroupFromCallbackOperation(String groupId) {
        try {
            boolean groupExists = em.find(Group.class, groupId) != null;
            if( ! StringUtils.isEmpty(groupId) && ! groupExists ) {
                Group group = new Group(groupId);
                em.persist(group);
            }
        } catch (Throwable t) {
            //logger.log(Level.WARNING, "UserGroupCallback has not been registered.");
        }
    } 
}
