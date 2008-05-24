package org.drools.process.core.context.exception;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Context;
import org.drools.process.core.context.AbstractContext;

public class ExceptionScope extends AbstractContext {

    private static final long serialVersionUID = 400L;

    public static final String EXCEPTION_SCOPE = "ExceptionScope";
    
    private Map<String, ExceptionHandler> exceptionHandlers = new HashMap<String, ExceptionHandler>();
    
    public String getType() {
        return EXCEPTION_SCOPE;
    }

    public void setExceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        this.exceptionHandlers.put(exception, exceptionHandler);
    }
    
    public ExceptionHandler getExceptionHandler(String exception) {
        return this.exceptionHandlers.get(exception);
    }

    public void removeExceptionHandler(String exception) {
        this.exceptionHandlers.remove(exception);
    }
    
    public Map<String, ExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    public Context resolveContext(Object param) {
        if (param instanceof String) {
            return getExceptionHandler((String) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
            "ExceptionScopes can only resolve exception names: " + param);
    }

}
