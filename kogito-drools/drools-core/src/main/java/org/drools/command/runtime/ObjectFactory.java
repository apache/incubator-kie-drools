package org.drools.command.runtime;

import javax.xml.bind.annotation.XmlRegistry;

import org.drools.command.runtime.BatchExecutionCommand;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;


@XmlRegistry
public class ObjectFactory {
    public BatchExecutionCommand createBatchExecutionCommand() {
        return new BatchExecutionCommand();
    }
    
    public GetGlobalCommand createGetGlobalCommand() {
        return new GetGlobalCommand();
    }
    
    public SetGlobalCommand createSetGlobalCommand() {
        return new SetGlobalCommand();
    }
    
}
