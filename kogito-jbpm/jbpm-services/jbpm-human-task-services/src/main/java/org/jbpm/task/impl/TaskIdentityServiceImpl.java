/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
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
    private EntityManager em;

    public TaskIdentityServiceImpl() {
    }
    
    public void addUser(User user) {
        em.persist(user);
 
    }

    public void addGroup(Group group) {
        em.persist(group);
    }

    public void removeGroup(String groupId) {
        Group group = em.find(Group.class, groupId);
        em.remove(group);
    }
    
    public void removeUser(String userId) {
        User user = em.find(User.class, userId);
        em.remove(user);
    }

    public List<User> getUsers() {
        return em.createQuery("from User").getResultList();
    }

    public List<Group> getGroups() {
        return em.createQuery("from Group").getResultList();
    }

    public User getUserById(String userId) {
        return em.find(User.class, userId);
    }

    public Group getGroupById(String groupId) {
        return em.find(Group.class, groupId);
    }

    public OrganizationalEntity getOrganizationalEntityById(String entityId) {
        return em.find(OrganizationalEntity.class, entityId);
    }
}
