package org.drools.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;


public class EndConversationCommand<Void> implements ExecutableCommand<Void> {
    private String conversationId;

    public EndConversationCommand() {
    }

    public EndConversationCommand(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.endConversation(reqContext, conversationId);

        return null;
    }
}
