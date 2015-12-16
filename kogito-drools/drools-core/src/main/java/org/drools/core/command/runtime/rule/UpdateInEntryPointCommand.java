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

package org.drools.core.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.DisconnectedFactHandle;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class UpdateInEntryPointCommand
        implements GenericCommand<Void> {

    private static final long serialVersionUID = 3255044102543531497L;

    private DisconnectedFactHandle handle;
    private Object object;
    private String entryPoint;

    public UpdateInEntryPointCommand() {
    }

    public UpdateInEntryPointCommand(FactHandle handle,
                                     Object object,
                                     String entryPoint) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.object = object;
        this.entryPoint = entryPoint;
    }
    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        
        ksession.getEntryPoint( entryPoint ).update( handle, object );
        return null;
    }
    

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
    	if (entryPoint == null) {
    		entryPoint = "DEFAULT";
    	}
        this.entryPoint = entryPoint;
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).update( " + handle + ", " + object + " );";
    }

    public Object getObject() { 
        return object;
    }
}
