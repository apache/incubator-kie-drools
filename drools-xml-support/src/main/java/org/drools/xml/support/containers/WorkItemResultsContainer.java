package org.drools.xml.support.containers;

public class WorkItemResultsContainer {
    
    private String identifier;
    private Object object;
    
    public WorkItemResultsContainer() {
        
    }
    
    public WorkItemResultsContainer(String identifier,
                            Object object) {
        this.identifier = identifier;
        this.object = object;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
