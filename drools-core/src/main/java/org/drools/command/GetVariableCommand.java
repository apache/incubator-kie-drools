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
import java.util.Map;

import org.drools.FactHandle;
import org.drools.command.impl.GenericCommand;
import org.drools.common.InternalFactHandle;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceConfiguration;
import org.kie.builder.ResourceType;
import org.kie.command.Command;
import org.kie.command.Context;
import org.kie.io.Resource;

public class GetVariableCommand
    implements
    GenericCommand<Object> {
    private String identifier;
    private String contextName;
    
    public GetVariableCommand(String identifier) {
        this.identifier = identifier;
    }    

    public GetVariableCommand(String identifier,
                              String contextName) {
        this.identifier = identifier;
        this.contextName = contextName;
    }

    public Object execute(Context ctx) {        
        Context targetCtx;
        if ( this.contextName == null ) {
            targetCtx = ctx;
        } else {
            targetCtx = ctx.getContextManager().getContext( this.contextName );
        }
        
        return targetCtx.get( identifier);        
    }

}
