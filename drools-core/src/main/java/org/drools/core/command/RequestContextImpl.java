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
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;
import org.kie.internal.fluent.RequestContext;

import java.util.HashMap;
import java.util.Map;

public class RequestContextImpl extends ContextImpl implements Context, RequestContext {

    private ContextImpl         appContext;
    private ConversationContext conversationContext;

    private ConversationContextManager cvnManager;

    private long                requestId;

    private Object              lastReturned;

    private String              lastSet;

    private Map<String, Object> out;
    private Map<String, Object> registry;
    private Exception           exception;


    public RequestContextImpl(long requestId, ContextManager ctxManager, ConversationContextManager cvnManager) {
        super(Long.toString(requestId), ctxManager);
        this.requestId = requestId;
        out = new HashMap<String, Object>();
        registry = new HashMap<String, Object>();
        set(REGISTRY, registry);
        this.cvnManager = cvnManager;
    }

    public Context getApplicationContext() {
        return appContext;
    }

    public void setApplicationContext(Context appContext) {
        this.appContext = (ContextImpl)appContext;
    }

    public ConversationContext getConversationContext() {
        return conversationContext;
    }

    public void setConversationContext(ConversationContext conversationContext) {
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
    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public long getConversationId() {
        return conversationContext.getConversationId();
    }

    public Object getLastReturned() {
        return lastReturned;
    }

    public void setLastReturned(Object lastReturned) {
        this.lastReturned = lastReturned;
    }

    public String getLastSet() {
        return lastSet;
    }

    public void setLastSetOrGet(String lastSet) {
        this.lastSet = lastSet;
    }

    @Override
    public Map<String, Object> getOut() {
        return out;
    }

    public Map<String, Object> getRegistry() {
        return registry;
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
