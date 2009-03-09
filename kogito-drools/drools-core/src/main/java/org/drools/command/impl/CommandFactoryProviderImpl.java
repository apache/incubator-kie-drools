package org.drools.command.impl;

import java.util.List;
import java.util.Map;

import org.drools.command.Command;
import org.drools.command.CommandFactoryProvider;
import org.drools.process.command.GetGlobalCommand;
import org.drools.process.command.InsertElementsCommand;
import org.drools.process.command.InsertObjectCommand;
import org.drools.process.command.QueryCommand;
import org.drools.process.command.SetGlobalCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.runtime.impl.BatchExecutionImpl;

public class CommandFactoryProviderImpl
    implements
    CommandFactoryProvider {

    public Command newGetGlobal(String identifier) {
        return new GetGlobalCommand( identifier );
    }

    public Command newGetGlobal(String identifier,
                                String outIdentifier) {
        GetGlobalCommand cmd = new GetGlobalCommand( identifier );
        cmd.setOutIdentifier( outIdentifier );
        return cmd;
    }

    public Command newInsertElements(Iterable objects) {
        return new InsertElementsCommand( objects );
    }

    public Command newInsertObject(Object object) {
        return new InsertObjectCommand( object );
    }

    public Command newInsertObject(Object object,
                                   String outIdentifier) {
        InsertObjectCommand cmd = new InsertObjectCommand( object );
        cmd.setOutIdentifier( outIdentifier );
        return cmd;
    }

    public Command newSetGlobal(String identifier,
                                Object object) {
        return new SetGlobalCommand( identifier,
                                     object );
    }

    public Command newSetGlobal(String identifier,
                                Object object,
                                boolean out) {
        SetGlobalCommand cmd = new SetGlobalCommand( identifier,
                                                     object );
        cmd.setOut( out );
        return cmd;
    }

    public Command newSetGlobal(String identifier,
                                Object object,
                                String outIdentifier) {
        SetGlobalCommand cmd = new SetGlobalCommand( identifier,
                                                     object );
        cmd.setOutIdentifier( outIdentifier );
        return cmd;
    }

    public Command newStartProcess(String processId) {
        StartProcessCommand startProcess = new StartProcessCommand();
        startProcess.setProcessId( processId );
        return startProcess;
    }

    public Command newStartProcess(String processId,
                                   Map<String, Object> parameters) {
        StartProcessCommand startProcess = new StartProcessCommand();
        startProcess.setProcessId( processId );
        startProcess.setParameters( parameters );
        return startProcess;
    }
    
    public Command newQuery(String identifier,
                                    String name) {
        return new QueryCommand(identifier, name, null );
        
    }
    
    public Command newQuery(String identifier,
                                    String name,
                                    Object[] arguments) {
        return new QueryCommand(identifier, name, arguments);
    }     
    
    public Command newBatchExecution(List<? extends Command> commands) {
        return new BatchExecutionImpl( (List<org.drools.process.command.Command>)commands );
    }

}
