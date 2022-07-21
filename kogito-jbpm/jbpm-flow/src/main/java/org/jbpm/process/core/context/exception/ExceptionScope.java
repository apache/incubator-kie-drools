/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.context.exception;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.context.AbstractContext;
import org.kie.kogito.process.workitem.WorkItemExecutionException;

public class ExceptionScope extends AbstractContext {

    private static final long serialVersionUID = 510l;

    public static final String EXCEPTION_SCOPE = "ExceptionScope";

    protected Map<String, ExceptionHandler> exceptionHandlers = new HashMap<String, ExceptionHandler>();
    private transient Collection<ExceptionHandlerPolicy> policies = ExceptionHandlerPolicyFactory.getHandlerPolicies();

    @Override
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

    public ExceptionHandler getExceptionHandler(Throwable exception) {
        ExceptionHandler handler = exceptionHandlers.entrySet().stream().filter(e -> test(policies, e.getKey(), exception)).findFirst().map(Entry::getValue).orElse(null);
        if (handler == null && exception instanceof WorkItemExecutionException) {
            handler = exceptionHandlers.get(((WorkItemExecutionException) exception).getErrorCode());
        }
        if (handler == null) {
            handler = exceptionHandlers.get(null);
        }
        return handler;
    }

    private boolean test(Collection<ExceptionHandlerPolicy> policies, String className, Throwable exception) {
        if (className == null)
            return false;
        Iterator<ExceptionHandlerPolicy> iter = policies.iterator();
        boolean found = false;
        while (!found && iter.hasNext()) {
            found = iter.next().test(className, exception);
        }
        return found;
    }

    public void removeExceptionHandler(String exception) {
        this.exceptionHandlers.remove(exception);
    }

    public Map<String, ExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    public void setExceptionHandlers(Map<String, ExceptionHandler> exceptionHandlers) {
        if (exceptionHandlers == null) {
            throw new IllegalArgumentException("Exception handlers are null");
        }
        this.exceptionHandlers = exceptionHandlers;
    }

    @Override
    public Context resolveContext(Object param) {
        if (param instanceof String) {
            return getExceptionHandler((String) param) == null ? null : this;
        } else if (param instanceof Throwable) {
            return getExceptionHandler((Throwable) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
                "ExceptionScopes can only resolve exception names: " + param);
    }

}
