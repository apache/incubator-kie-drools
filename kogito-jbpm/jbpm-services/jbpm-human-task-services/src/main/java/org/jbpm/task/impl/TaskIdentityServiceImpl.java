/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.jbpm.task.impl.model.GroupImpl;
import org.jbpm.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.task.impl.model.UserImpl;
import org.kie.internal.task.api.TaskIdentityService;
import org.kie.internal.task.api.model.Group;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.User;

/**
 *
 */

@Transactional
@ApplicationScoped
public class TaskIdentityServiceImpl implements TaskIdentityService {

    @Inject 
    private JbpmServicesPersistenceManager pm;

    public TaskIdentityServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    public void addUser(User user) {
        pm.persist(user);
 
    }

    public void addGroup(Group group) {
        pm.persist(group);
    }

    public void removeGroup(String groupId) {
        GroupImpl group = pm.find(GroupImpl.class, groupId);
        pm.remove(group);
    }
    
    public void removeUser(String userId) {
        UserImpl user = pm.find(UserImpl.class, userId);
        pm.remove(user);
    }

    public List<User> getUsers() {
        return (List<User>) pm.queryStringInTransaction("from User");
    }

    public List<Group> getGroups() {
        return (List<Group>) pm.queryStringInTransaction("from Group");
    }

    public User getUserById(String userId) {
        return pm.find(UserImpl.class, userId);
    }

    public Group getGroupById(String groupId) {
        return pm.find(GroupImpl.class, groupId);
    }

    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return pm.find(OrganizationalEntityImpl.class, entityId);
    }
}
