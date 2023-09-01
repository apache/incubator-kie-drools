package org.drools.commands.runtime;

import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {

    public BatchExecutionCommandImpl createBatchExecutionCommand() {
        return new BatchExecutionCommandImpl();
    }
    
    public GetGlobalCommand createGetGlobalCommand() {
        return new GetGlobalCommand();
    }

    public GetIdCommand createGetIdCommand() {
        return new GetIdCommand();
    }
    
    public SetGlobalCommand createSetGlobalCommand() {
        return new SetGlobalCommand();
    }

    public ExecutionResultImpl createExecutionResultImpl() {
        return new ExecutionResultImpl();
    }

    public FlatQueryResults createFlatQueryResults() {
        return new FlatQueryResults();
    }
}
