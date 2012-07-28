/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.internals.lifecycle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.drools.core.util.StringUtils;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;
import org.jbpm.task.annotations.CommandBased;
import org.jbpm.task.api.TaskCommandExecutor;
import org.jbpm.task.commands.TaskCommand;
import org.jbpm.task.identity.UserGroupCallback;
import org.jbpm.task.identity.UserGroupCallbackManager;

/**
 *
 */
@Decorator
public class UserGroupTaskCommandExecutorDecorator implements TaskCommandExecutor{
    private @Inject @Delegate @CommandBased TaskCommandExecutor executor;
    private @Inject EntityManager em;
    private Map<String, Boolean> userGroupsMap = new HashMap<String, Boolean>();
    
    public <T> T executeTaskCommand(TaskCommand<T> command) {
        OrganizationalEntity targetEntity = null;
        command.setGroupsIds( doUserGroupCallbackOperation(command.getUserId(), command.getGroupsIds()));
        doCallbackUserOperation(command.getTargetEntityId());
//        if (command.getTargetEntityId() != null) {
//            targetEntity = em.find(OrganizationalEntity.class, command.getTargetEntityId());
//        }
        return executor.executeTaskCommand(command);
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
