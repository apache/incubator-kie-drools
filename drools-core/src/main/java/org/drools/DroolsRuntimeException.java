package org.drools;

/**
 * A wrapper exception used to soften DroolsException to a RuntimeException.
 */
public class DroolsRuntimeException extends RuntimeException {

    public DroolsRuntimeException(CheckedDroolsException e) {
        super( e );
    }
    
    public DroolsRuntimeException(String message) {
    		super( message );
    }
}
