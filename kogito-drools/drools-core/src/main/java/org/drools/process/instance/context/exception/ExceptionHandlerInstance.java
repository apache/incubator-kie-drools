package org.drools.process.instance.context.exception;

public interface ExceptionHandlerInstance {

    void handleException(String exception, Object param);
    
}
