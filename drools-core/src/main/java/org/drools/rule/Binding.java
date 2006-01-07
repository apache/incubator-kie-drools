package org.drools.rule;

import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;

public abstract class Binding {
    private final String     identifier;

    private final ObjectType objectType;       
    
    private final Extractor extractor;

    public Binding(String identifier,
                   ObjectType objectType,
                   Extractor extractor) {
        this.identifier = identifier;
        this.objectType = objectType;
        this.extractor = extractor;
    }

    /**
     * @return Returns the identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }

    /**
     * @return Returns the objectType.
     */
    public ObjectType getObjectType() {
        return this.objectType;
    }
    
    public Extractor getExtractor() {
        return this.extractor;
    }
    
    

}
