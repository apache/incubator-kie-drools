package org.drools.compiler;

import java.io.PrintStream;
import java.io.StringWriter;

/**
 * This is used for reporting errors with loading a ruleflow.
 * @author Michael Neale
 *
 */
public class RuleFlowLoadError extends DroolsError {

    private String message;
    private Exception exception;

    public RuleFlowLoadError(String message, Exception nested) {
        
        this.message = message;
        this.exception = nested;
    }
    
    public String getMessage() {
        if (exception != null) {
            return message + " : Exception " + exception.getClass() + " : "+ exception.getMessage();
        } else {
            return message;
        }
    }

}
