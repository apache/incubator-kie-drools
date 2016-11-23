package org.drools.core.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;


public class JoinConversationCommand<Void> implements ExecutableCommand<Void> {
    private String conversationId;

    public JoinConversationCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.joinConversation(reqContext, conversationId);

        return (Void) null;
    }
}
