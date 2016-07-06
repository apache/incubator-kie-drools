package org.drools.core.command;

import org.drools.core.command.RequestContextImpl;
import org.kie.internal.command.Context;

import org.drools.core.command.impl.ContextImpl;

import java.util.HashMap;
import java.util.Map;


public class ConversationContextManager {

    private Map<Long, Context> conversationContexts;

    private long counter;

    public ConversationContextManager() {
        conversationContexts = new HashMap<Long, Context>();
    }

    public void startConversation(RequestContextImpl requestContext) {
        long conversationId = counter++;
        ConversationContext ctx = new ConversationContext(conversationId, null);
        conversationContexts.put(conversationId, ctx);
        requestContext.setConversationContext(ctx);
    }

    public void joinConversation(RequestContextImpl requestContext, long conversationId) {
        ConversationContext ctx = (ConversationContext) conversationContexts.get(conversationId);
        if ( ctx == null ) {
            throw new RuntimeException("Conversation cannot be found");
        }
        requestContext.setConversationContext(ctx);
    }

    public void leaveConversation(RequestContextImpl requestContext, long conversationId) {
        throw new UnsupportedOperationException("Need to implement");
    }

    public void endConversation(RequestContextImpl requestContext, long conversationId) {
        conversationContexts.remove(conversationId);
        requestContext.setConversationContext(null);
    }

}
