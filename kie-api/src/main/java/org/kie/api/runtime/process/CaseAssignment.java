package org.kie.api.runtime.process;

import java.util.Collection;

import org.kie.api.task.model.OrganizationalEntity;

/**
 * Represents case assignment which usually means named role to individuals or groups.
 *
 */
public interface CaseAssignment {

    /**
     * Assigns given entity (either user or group) to given role
     * @param roleName name of the role entity should be assigned to
     * @param entity user or group to be assigned
     */
    void assign(String roleName, OrganizationalEntity entity);
    
    /**
     * Assigns given user to given role
     * @param roleName name of the role user should be assigned to
     * @param userId user to be assigned
     */
    void assignUser(String roleName, String userId);
    
    /**
     * Assigns given group to given role
     * @param roleName name of the role group should be assigned to
     * @param groupId group to be assigned
     */
    void assignGroup(String roleName, String groupId);
    
    /**
     * Removes given entity from the role
     * @param roleName name of the role that given entity should be removed from
     * @param entity use or group to be removed
     */
    void remove(String roleName, OrganizationalEntity entity);
    
    /**
     * Returns assigned entities for given role
     * @param roleName name of the role assignment should be returned for
     * @return returns all assignments for the given role
     */
    Collection<OrganizationalEntity> getAssignments(String roleName);
    
    /**
     * Returns defined roles
     * @return returns all roles
     */
    Collection<String> getRoles();
}
