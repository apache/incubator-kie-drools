/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.error;

import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.command.impl.AbstractInterceptor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.runtime.error.ExecutionErrorManager;


public class ExecutionErrorHandlerInterceptor extends AbstractInterceptor {

    private static final ThreadLocal<AtomicInteger> invocationsCounter = new ThreadLocal<>();
    
    private ExecutionErrorManager executionErrorManager;
    
    
    public ExecutionErrorHandlerInterceptor(Environment env) {
        this.executionErrorManager = (ExecutionErrorManager) env.get(EnvironmentName.EXEC_ERROR_MANAGER);
    }

    @Override
    public final RequestContext execute( Executable executable, RequestContext ctx ) {
        AtomicInteger counter = invocationsCounter.get();
        if (counter == null) {
            counter = new AtomicInteger( 0 );
            invocationsCounter.set( counter );
            if (executionErrorManager != null) {
                ((ExecutionErrorManagerImpl)executionErrorManager).createHandler();
            }
        }
        counter.incrementAndGet();
        try {
            return internalExecute( executable, ctx );
        } finally {
            if (counter.decrementAndGet() == 0) {
                invocationsCounter.remove();
                if (executionErrorManager != null) {
                    ((ExecutionErrorManagerImpl)executionErrorManager).closeHandler();
                }
            }
        }
    }
    
    protected RequestContext internalExecute(Executable executable, RequestContext ctx) {

        try {
            executeNext(executable, ctx);
            return ctx;
        } catch (Throwable ex) {
            // in case there is another interceptor of this type in the stack don't handle it here
            if (hasInterceptorInStack() || executionErrorManager == null) {
                throw ex;
            }
            executionErrorManager.getHandler().handle(ex);
            throw ex;
        }
    }
    
    protected boolean hasInterceptorInStack() {
        return invocationsCounter.get().get() > 1;
    }
}
