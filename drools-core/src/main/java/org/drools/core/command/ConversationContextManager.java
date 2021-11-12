package org.drools.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.drools.core.command.impl.ContextImpl;
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
