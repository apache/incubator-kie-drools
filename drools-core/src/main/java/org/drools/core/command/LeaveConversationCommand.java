package org.drools.core.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;


public class LeaveConversationCommand<Void> implements ExecutableCommand<Void> {
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
