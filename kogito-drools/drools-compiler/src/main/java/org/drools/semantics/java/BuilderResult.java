package org.drools.semantics.java;

import org.drools.lang.descr.PatternDescr;

public class BuilderResult {
    private PatternDescr descr;
    private Exception    exception;
    private String       message;
    
    public BuilderResult(PatternDescr descr,
                         Exception exception,
                         String message) {
        super();
        this.descr = descr;
        this.exception = exception;
        this.message = message;
    }

    public PatternDescr getDescr() {
        return descr;
    }

    public Exception getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
            
}
