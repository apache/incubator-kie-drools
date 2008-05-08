package org.drools.process.instance.context.exception;

import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.instance.context.AbstractContextInstance;

public abstract class ExceptionScopeInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 400L;

    public String getContextType() {
        return ExceptionScope.EXCEPTION_SCOPE;
    }
    
    public ExceptionScope getExceptionScope() {
        return (ExceptionScope) getContext();
    }
    
    public void handleException(String exception, Object params) {
        ExceptionHandler handler = getExceptionScope().getExceptionHandler(exception);
        if (handler == null) {
            throw new IllegalArgumentException(
                "Could not find ExceptionHandler for " + exception);
        }
        handleException(handler, exception, params);
    }
    
    public abstract void handleException(ExceptionHandler handler, String exception, Object params);

}
