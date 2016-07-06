package org.drools.core.command;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;


public class LeaveConversationCommand<Void> implements GenericCommand<Void> {
    public LeaveConversationCommand() {
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.leaveConversation(reqContext, reqContext.getConversationContext().getConversationId());

        return (Void) null;
    }
}
