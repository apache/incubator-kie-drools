package org.drools.core.command;

import org.drools.core.command.RequestContextImpl;
import org.kie.internal.command.Context;

import org.drools.core.command.impl.ContextImpl;

import java.util.HashMap;
import java.util.Map;


public class ConversationManager {

    private Map<Long, Context> conversationContexts;

    private long counter;

    public ConversationManager() {
        conversationContexts = new HashMap<Long, Context>();
    }

    public void startConversation(RequestContextImpl requestContext) {
        long conversationId = counter++;
        ContextImpl ctx = new ContextImpl(Long.toString(conversationId), null);
        conversationContexts.put(conversationId, ctx);
        requestContext.setConversationContext(ctx);
    }

    public void joinConversation(RequestContextImpl requestContext, long conversationId) {
        ContextImpl ctx = (ContextImpl) conversationContexts.get(conversationId);
        requestContext.setConversationContext(ctx);
    }

    public void endConversation(RequestContextImpl requestContext, long conversationId) {
        conversationContexts.remove(conversationId);
        requestContext.setConversationContext(null);
    }

}
