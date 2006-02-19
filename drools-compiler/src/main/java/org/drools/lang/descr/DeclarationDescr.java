package org.drools.lang.descr;

public class DeclarationDescr {
    private String identifier;
    private Class type;
            
    public DeclarationDescr(String identifier,
                            Class type) {
        super();
        this.identifier = identifier;
        this.type = type;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public Class getType() {
        return type;
    }
    
    
    
}
