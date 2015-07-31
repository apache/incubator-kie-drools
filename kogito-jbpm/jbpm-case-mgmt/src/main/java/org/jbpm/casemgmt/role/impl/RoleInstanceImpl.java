package org.jbpm.casemgmt.role.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.casemgmt.role.RoleAssignment;
import org.jbpm.casemgmt.role.RoleInstance;

public class RoleInstanceImpl implements RoleInstance, Serializable {
    
    private static final long serialVersionUID = 630L;
    
    private String roleName;
    private Map<String, RoleAssignment> roleAssignments = new HashMap<String, RoleAssignment>();

    public RoleInstanceImpl(String roleName) {
        setRoleName(roleName);
    }
    
    public String getRoleName() {
        return roleName;
    }

    private void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Collection<RoleAssignment> getRoleAssignments() {
        return roleAssignments.values();
    }
    
    public RoleAssignment getRoleAssignment(String userId) {
        return roleAssignments.get(userId);
    }

    public void addRoleAssignment(RoleAssignment assignment) {
        roleAssignments.put(assignment.getUserId(), assignment);
    }
    
    public void addRoleAssignment(String userId) {
        roleAssignments.put(userId, new RoleAssignmentImpl(userId));
    }

    public void removeRoleAssignment(String userId) {
        roleAssignments.remove(userId);
    }
    
    public String[] getRoleAssignmentNames() {
        return roleAssignments.keySet().toArray(new String[roleAssignments.size()]);
    }

}
