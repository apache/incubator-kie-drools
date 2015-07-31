package org.jbpm.casemgmt.role.impl;

import java.io.Serializable;

import org.jbpm.casemgmt.role.Role;

public class RoleImpl implements Role, Serializable {
    
    private static final long serialVersionUID = 630L;
    
    private String name;
    private Integer cardinality;
    
    public RoleImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCardinality() {
        return cardinality;
    }

    public void setCardinality(Integer cardinality) {
        this.cardinality = cardinality;
    }

}
