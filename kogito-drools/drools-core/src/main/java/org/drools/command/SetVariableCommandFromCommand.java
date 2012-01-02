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
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.command.Command;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.common.InternalFactHandle;

public class SetVariableCommandFromCommand
    implements
    GenericCommand<Object> {
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
            context.getContextManager().getContext( World.ROOT ).set( this.identifier,
                         ((GenericCommand) this.cmd).execute( context ) );
        } else {
            context.getContextManager().getContext( this.contextName ).set( this.identifier,
                                                                            ((GenericCommand) this.cmd).execute( context ) );
        }
        return null;
    }

}
