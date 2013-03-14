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
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;
import org.jbpm.task.api.TaskIdentityService;

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
        Group group = pm.find(Group.class, groupId);
        pm.remove(group);
    }
    
    public void removeUser(String userId) {
        User user = pm.find(User.class, userId);
        pm.remove(user);
    }

    public List<User> getUsers() {
        return (List<User>) pm.queryStringInTransaction("from User");
    }

    public List<Group> getGroups() {
        return (List<Group>) pm.queryStringInTransaction("from Group");
    }

    public User getUserById(String userId) {
        return pm.find(User.class, userId);
    }

    public Group getGroupById(String groupId) {
        return pm.find(Group.class, groupId);
    }

    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return pm.find(OrganizationalEntity.class, entityId);
    }
}
