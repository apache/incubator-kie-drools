package org.drools.compiler;

import org.drools.lang.descr.PatternDescr;

public class BuilderResult {
    private PatternDescr descr;
    private Object       object;
    private String       message;
    
    public BuilderResult(PatternDescr descr,
                         Object object,
                         String message) {
        super();
        this.descr = descr;
        this.object = object;
        this.message = message;
    }

    public PatternDescr getDescr() {
        return descr;
    }

    public Object getObject() {
        return object;
    }

    public String getMessage() {
        return message;
    }
            
}
