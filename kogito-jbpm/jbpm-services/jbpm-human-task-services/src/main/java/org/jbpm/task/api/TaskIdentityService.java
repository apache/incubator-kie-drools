/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task.api;

import java.util.List;
import org.jbpm.task.Group;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.User;

/**
 * The Task Identity Service provides all the 
 *  functionality related with the Organizational Entities
 *  that will be handled internally by jBPM. This methods
 *  will allow us to create the Mappings against external 
 *  identity directories to the internal inforamtion required
 *  by jBPM.
 */
public interface TaskIdentityService {

    public void addUser(User user);

    public void addGroup(Group group);

    public void removeGroup(String groupId);

    public void removeUser(String userId);

    public List<User> getUsers();

    public List<Group> getGroups();

    public User getUserById(String userId);

    public Group getGroupById(String groupId);
    
    public OrganizationalEntity getOrganizationalEntityById(String entityId);

    
}
