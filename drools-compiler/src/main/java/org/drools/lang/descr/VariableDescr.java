package org.drools.lang.descr;

public class VariableDescr extends BaseDescr {
    private String identifier;

    public VariableDescr(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    
}
