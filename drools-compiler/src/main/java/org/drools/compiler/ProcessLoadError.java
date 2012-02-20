package org.drools.compiler;


import org.drools.io.Resource;

/**
 * This is used for reporting errors with loading a ruleflow.
 */
public class ProcessLoadError extends DroolsError {

    private String message;
    private Exception exception;
    private static final int[] lines = new int[0];

    public ProcessLoadError(Resource resource, String message, Exception nested) {
        super(resource);
        this.message = message;
        this.exception = nested;
    }
    
    public int[] getLines() {
        return this.lines;
    }
    
    public String getMessage() {
        if (exception != null) {
            return message + " : Exception " + exception.getClass() + " : "+ exception.getMessage();
        } else {
            return message;
        }
    }

}
