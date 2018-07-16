package org.drools.core.command;

import org.drools.core.command.impl.TransactionalCommand;
import org.kie.api.runtime.Context;


public class LeaveConversationCommand<Void> implements TransactionalCommand<Void> {
    public LeaveConversationCommand() {
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.leaveConversation(reqContext, reqContext.getConversationContext().getName());

        return (Void) null;
    }
}
