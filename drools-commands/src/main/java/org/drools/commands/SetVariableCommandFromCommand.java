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

import org.drools.commands.impl.ContextManagerImpl;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;

public class SetVariableCommandFromCommand
    implements
    ExecutableCommand<Void> {
    private String identifier;
    private String contextName;
    private Command cmd;

    public SetVariableCommandFromCommand(String contextName,
                                         String identifier,
                                         Command cmd) {
        this.identifier = identifier;
        this.contextName = contextName;
        this.cmd = cmd;
    }

    public Void execute(Context context) {
        if ( this.contextName == null ) {
            ( (RegistryContext) context ).getContextManager().getContext( ContextManagerImpl.ROOT ).set( this.identifier,
                                                                                                     ((ExecutableCommand) this.cmd).execute( context ) );
        } else {
            ( (RegistryContext) context ).getContextManager().getContext( this.contextName ).set( this.identifier,
                                                                            ((ExecutableCommand) this.cmd).execute( context ) );
        }
        return null;
    }

}
