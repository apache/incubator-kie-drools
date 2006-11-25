package org.drools.analytics;

public class Pattern {

    public String id;
    public String objectType;
    public Constraint[] constraints;
    
    
    
    public Constraint[] getConstraints() {
        return constraints;
    }
    public void setConstraints(Constraint[] constraints) {
        this.constraints = constraints;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
}
