/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.process.core.context.exception;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.Context;
import org.drools.process.core.context.AbstractContext;

public class ExceptionScope extends AbstractContext {

    private static final long serialVersionUID = 510l;

    public static final String EXCEPTION_SCOPE = "ExceptionScope";
    
    private Map<String, ExceptionHandler> exceptionHandlers = new HashMap<String, ExceptionHandler>();
    
    public String getType() {
        return EXCEPTION_SCOPE;
    }

    public void setExceptionHandler(String exception, ExceptionHandler exceptionHandler) {
        this.exceptionHandlers.put(exception, exceptionHandler);
    }
    
    public ExceptionHandler getExceptionHandler(String exception) {
        ExceptionHandler result = exceptionHandlers.get(exception);
        if (result == null) {
        	result = exceptionHandlers.get(null);
        }
        return result;
    }

    public void removeExceptionHandler(String exception) {
        this.exceptionHandlers.remove(exception);
    }
    
    public Map<String, ExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }
    
    public void setExceptionHandlers(Map<String, ExceptionHandler> exceptionHandlers) {
    	if (exceptionHandlers == null) {
    		throw new IllegalArgumentException( "Exception handlers are null" );
    	}
    	this.exceptionHandlers = exceptionHandlers;
    }

    public Context resolveContext(Object param) {
        if (param instanceof String) {
            return getExceptionHandler((String) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
            "ExceptionScopes can only resolve exception names: " + param);
    }

}
