package org.drools.core.command;

import org.drools.core.command.impl.ContextImpl;
import org.kie.api.runtime.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ConversationContextManager {

    private Map<String, Context> conversationContexts;

    private long counter;

    public ConversationContextManager() {
        conversationContexts = new HashMap<String, Context>();
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

    public void leaveConversation(RequestContextImpl requestContext, String conversationId) {
        throw new UnsupportedOperationException("Need to implement");
    }

    public void endConversation(RequestContextImpl requestContext, String conversationId) {
        conversationContexts.remove(conversationId);
        requestContext.setConversationContext(null);
    }

}
