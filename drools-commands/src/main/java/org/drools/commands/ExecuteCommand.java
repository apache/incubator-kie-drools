/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
