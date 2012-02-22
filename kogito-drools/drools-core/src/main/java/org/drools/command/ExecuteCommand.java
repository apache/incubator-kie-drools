/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.command;

import java.util.HashMap;

import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DefaultFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.FactHandle;

public class ExecuteCommand
    implements
    GenericCommand<ExecutionResults> {

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
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        ExecutionResults kresults = null;
        if( ksession instanceof StatefulKnowledgeSessionImpl ) { 
            kresults = ((StatefulKnowledgeSessionImpl)ksession).execute(context, this.command );
        }
        else { 
            // Graceful failure
            kresults = ksession.execute(this.command);
        }
        
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context ).getExecutionResults()).getResults().put( this.outIdentifier, kresults );
        }
        if (disconnected) {
            ExecutionResultImpl disconnectedResults = new ExecutionResultImpl();
            HashMap<String, Object> disconnectedHandles = new HashMap<String, Object>();
            for (String key : kresults.getIdentifiers()) {
                FactHandle handle = (FactHandle) kresults.getFactHandle(key);
                if (handle != null) {
                    DefaultFactHandle disconnectedHandle = ((DefaultFactHandle) handle).clone();
                    disconnectedHandle.disconnect();
                    disconnectedHandles.put(key, disconnectedHandle);
                }
            }
            disconnectedResults.setFactHandles(disconnectedHandles);
            disconnectedResults.setResults((HashMap)((ExecutionResultImpl)kresults).getResults());
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
