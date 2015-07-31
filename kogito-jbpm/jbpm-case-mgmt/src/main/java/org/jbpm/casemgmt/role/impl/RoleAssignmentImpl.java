package org.jbpm.casemgmt.role.impl;

import java.io.Serializable;

import org.jbpm.casemgmt.role.RoleAssignment;

public class RoleAssignmentImpl implements RoleAssignment, Serializable {
    
    private static final long serialVersionUID = 630L;
    
    private String userId;
    
    public RoleAssignmentImpl(String userId) {
        setUserId(userId);
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

}
