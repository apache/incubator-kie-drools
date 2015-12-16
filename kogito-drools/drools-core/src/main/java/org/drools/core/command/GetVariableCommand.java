/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.impl.GenericCommand;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-variable-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetVariableCommand implements GenericCommand<Object> {
   
    @XmlElement
    @XmlSchemaType(name="string")
    private String identifier;
   
    @XmlElement
    @XmlSchemaType(name="string")
    private String contextName;
    
    public GetVariableCommand() {
        // no-arg constructor neccessary for serialization
    }    

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
