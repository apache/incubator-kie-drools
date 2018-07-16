package org.drools.core.command;

import org.drools.core.command.impl.TransactionalCommand;
import org.kie.api.runtime.Context;


public class StartConversationCommand<Void> implements TransactionalCommand<Void> {
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
