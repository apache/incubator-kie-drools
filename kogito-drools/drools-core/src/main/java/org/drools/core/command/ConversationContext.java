package org.drools.core.command;

import org.drools.core.command.impl.ContextImpl;
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;

public class ConversationContext extends ContextImpl {

    private long conversationId;

    public ConversationContext(long conversationId, ContextManager manager, Context delegate) {
        super(Long.toString(conversationId), manager, delegate);
    }

    public ConversationContext(long conversationId, ContextManager manager) {
        super(Long.toString(conversationId), manager);
        this.conversationId = conversationId;

    }

    public long getConversationId() {
        return conversationId;
    }
}
