package org.drools.process.instance.context.exception;

import org.drools.process.core.context.exception.ExceptionHandler;

public interface ExceptionHandlerInstance {
	
    void handleException(String exception, Object param);
    
    void setExceptionHandler(ExceptionHandler exceptionHandler);
    
}
