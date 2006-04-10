package org.drools.lang;

import org.antlr.runtime.RecognitionException;

public class ExpanderException extends RecognitionException {

    private String message;
    
    public ExpanderException(String message, int line) {
        this.message = message; 
        this.line = line;
    }
    
    public String getMessage() {
        return message;
    }
    
}
