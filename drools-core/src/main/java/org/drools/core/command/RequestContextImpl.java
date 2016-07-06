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

import java.util.HashMap;
import java.util.Map;

public class RequestContextImpl extends ContextImpl implements Context {

    private ContextImpl         appContext;
    private ContextImpl         conversationContext;

    private ConversationManager cvnManager;

    private long                requestId;
    private long                conversationId;

    private Object              lastReturned;

    private String              lastSet;

    private Map<String, Object> out;
    private Map<String, Object> registry;


    public RequestContextImpl(long requestId, long conversationId, ContextManager ctxManager, ConversationManager cvnManager) {
        this(requestId, ctxManager, cvnManager);

    }

    public RequestContextImpl(long requestId, ContextManager ctxManager, ConversationManager cvnManager) {
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

    public Context getConversationContext() {
        return conversationContext;
    }

    public void setConversationContext(Context conversationContext) {
        this.conversationContext = (ContextImpl)conversationContext;
    }

    public ConversationManager getConversationManager() {
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

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
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

    public void setLastSet(String lastSet) {
        this.lastSet = lastSet;
    }

    public Map<String, Object> getOut() {
        return out;
    }

    public Map<String, Object> getRegistry() {
        return registry;
    }

    @Override
    public String toString() {
        return "ContextImpl{" +
               "name='" + getName() + '\'' +
               '}';
    }
}
