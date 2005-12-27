package org.drools.rule;

import org.drools.spi.ObjectType;

public abstract class Binding {
    private final String     identifier;

    private final ObjectType objectType;

    public Binding(String identifier,
                   ObjectType objectType){
        this.identifier = identifier;
        this.objectType = objectType;
    }

    /**
     * @return Returns the identifier.
     */
    public String getIdentifier(){
        return this.identifier;
    }

    /**
     * @return Returns the objectType.
     */
    public ObjectType getObjectType(){
        return this.objectType;
    }

}
