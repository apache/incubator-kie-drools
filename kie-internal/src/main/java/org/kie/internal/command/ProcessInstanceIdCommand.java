package org.kie.internal.command;

import org.kie.api.command.Command;

public interface ProcessInstanceIdCommand<T> extends Command<T> {

    public Long getProcessInstanceId();
    
}
