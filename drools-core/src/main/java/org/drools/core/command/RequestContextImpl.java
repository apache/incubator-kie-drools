/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command;

import org.drools.core.command.impl.ContextImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.KieBase;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.command.ContextManager;

public class RequestContextImpl extends ContextImpl implements RequestContext {

    private Context appContext;
    private Context conversationContext;

    private ConversationContextManager cvnManager;

    private Object result;

    private String lastSet;

    private Exception exception;

    public RequestContextImpl() {
        register( ExecutionResultImpl.class, new ExecutionResultImpl() );
    }

    public RequestContextImpl(long requestId, ContextManager ctxManager, ConversationContextManager cvnManager) {
        super(Long.toString(requestId), ctxManager);
        this.cvnManager = cvnManager;
        register( ExecutionResultImpl.class, new ExecutionResultImpl() );
    }

    public Context getApplicationContext() {
        return appContext;
    }

    public void setApplicationContext(Context appContext) {
        this.appContext = (ContextImpl)appContext;
    }

    public Context getConversationContext() {
        return conversationContext;
    }

    public void setConversationContext(Context conversationContext ) {
        this.conversationContext = conversationContext;
    }

    public ConversationContextManager getConversationManager() {
        return cvnManager;
    }

    public Object get(String identifier) {
        if(identifier == null || identifier.equals("")){
            return null;
        }

        Object object = null;

        if ( has(identifier)) {
            object = super.get( identifier );
        } else if (conversationContext != null && conversationContext.has(identifier)) {
            object = conversationContext.get( identifier );
        } else if (appContext != null && appContext.has(identifier)) {
            object = appContext.get( identifier );
        }

        return object;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult( Object result ) {
        this.result = result;
    }

    @Override
    public RequestContext with( KieBase kieBase ) {
        register( KieBase.class, kieBase );
        return this;
    }

    @Override
    public RequestContext with( KieSession kieSession ) {
        register( KieSession.class, kieSession );
        return this;
    }

    public String getLastSet() {
        return lastSet;
    }

    public void setLastSetOrGet(String lastSet) {
        this.lastSet = lastSet;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ContextImpl{" +
               "name='" + getName() + '\'' +
               '}';
    }
}
