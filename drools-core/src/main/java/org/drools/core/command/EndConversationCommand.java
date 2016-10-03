package org.drools.core.command;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;


public class EndConversationCommand<Void> implements GenericCommand<Void> {
    private long conversationId;

    public EndConversationCommand() {
    }

    public EndConversationCommand(long conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.endConversation(reqContext, conversationId);

        return (Void) null;
    }
}
