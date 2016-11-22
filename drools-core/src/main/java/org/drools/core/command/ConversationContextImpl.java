package org.drools.core.command;

import org.drools.core.command.impl.ContextImpl;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ConversationContext;
import org.kie.internal.command.ContextManager;

public class ConversationContextImpl extends ContextImpl implements ConversationContext {

    private long conversationId;

    public ConversationContextImpl( long conversationId, ContextManager manager, Context delegate ) {
        super(Long.toString(conversationId), manager, delegate);
    }

    public ConversationContextImpl( long conversationId, ContextManager manager ) {
        super(Long.toString(conversationId), manager);
        this.conversationId = conversationId;

    }

    public long getConversationId() {
        return conversationId;
    }
}
