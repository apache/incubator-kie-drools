package org.jbpm.casemgmt.role;

import java.util.Collection;

public interface RoleInstance {
    
    String getRoleName();
    
    Collection<RoleAssignment> getRoleAssignments();
    
    void addRoleAssignment(String userId);
    
    void removeRoleAssignment(String userId);
    
    String[] getRoleAssignmentNames();
}
