package org.drools.core.command;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;


public class StartConversationCommand<Void> implements ExecutableCommand<Void> {
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
