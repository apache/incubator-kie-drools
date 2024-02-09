/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.drools.commands.impl.ContextImpl;
import org.kie.api.runtime.Context;


public class ConversationContextManager {

    private Map<String, Context> conversationContexts;

    public ConversationContextManager() {
        conversationContexts = new HashMap<>();
    }

    public void startConversation(RequestContextImpl requestContext) {
        String conversationId = UUID.randomUUID().toString();
        ContextImpl ctx = new ContextImpl( conversationId, null);
        conversationContexts.put(conversationId, ctx);
        requestContext.setConversationContext(ctx);
    }

    public void joinConversation(RequestContextImpl requestContext, String conversationId) {
        Context ctx = conversationContexts.get( conversationId );
        if ( ctx == null ) {
            throw new RuntimeException("Conversation cannot be found");
        }
        requestContext.setConversationContext(ctx);
    }

    public void endConversation(RequestContextImpl requestContext, String conversationId) {
        conversationContexts.remove(conversationId);
        requestContext.setConversationContext(null);
    }

}
