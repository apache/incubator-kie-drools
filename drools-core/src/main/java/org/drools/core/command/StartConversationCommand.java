package org.drools.core.command;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;


public class StartConversationCommand<Void> implements GenericCommand<Void> {
    public StartConversationCommand() {
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.startConversation(reqContext);

        return (Void) null;
    }
}
