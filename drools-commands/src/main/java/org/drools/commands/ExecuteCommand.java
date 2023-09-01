package org.drools.commands;

import java.util.HashMap;

import org.drools.core.common.DefaultFactHandle;
import org.drools.commands.runtime.ExecutionResultImpl;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

public class ExecuteCommand
    implements
    ExecutableCommand<ExecutionResults> {

    private String   outIdentifier;
    private Command<ExecutionResults>  command;
    private boolean disconnected = false;
    
    public ExecuteCommand(Command  command) {
        this.command = command;
    }
    
    public ExecuteCommand(String identifier, Command  command) {
        this.command = command;
        this.outIdentifier = identifier;
    }
    
    public ExecuteCommand(String identifier, Command  command, boolean disconnected) {
        this.command = command;
        this.outIdentifier = identifier;
        this.disconnected = disconnected;
    }
    
    public ExecuteCommand(Command  command, boolean disconnected) {
        this.command = command;
        this.disconnected = disconnected;
    }

    public ExecutionResults execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        ExecutionResults kresults = ksession.execute(this.command);
        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, kresults );
        }
        if (disconnected) {
            ExecutionResultImpl disconnectedResults = new ExecutionResultImpl();
            HashMap<String, Object> disconnectedHandles = new HashMap<>();
            for (String key : kresults.getIdentifiers()) {
                FactHandle handle = (FactHandle) kresults.getFactHandle(key);
                if (handle != null) {
                    DefaultFactHandle disconnectedHandle = ((DefaultFactHandle) handle).clone();
                    disconnectedHandle.disconnect();
                    disconnectedHandles.put(key, disconnectedHandle);
                }
            }
            disconnectedResults.setFactHandles(disconnectedHandles);
            disconnectedResults.setResults(kresults.getResults());
            return disconnectedResults;
        }
        
        return kresults;
    }

    public Command getCommand() {
        return this.command;
    }
       
    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }
    
    public String toString() {
        return "session.execute(" + this.command + ");";
    }

}
