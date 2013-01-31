package org.drools.exception;

public class HandlerRuntimeException extends RuntimeException {

    /** Generated serial version UID **/
    private static final long serialVersionUID = -6144303249955625507L;
    
    private final JavaExceptionHandler exceptionHandler;
    
    public HandlerRuntimeException(Throwable cause, JavaExceptionHandler exceptionHandler) {
        super(cause);
        this.exceptionHandler = exceptionHandler;
    }
    
    public JavaExceptionHandler getExceptionHandler() { 
        return exceptionHandler;
    }
    
}
