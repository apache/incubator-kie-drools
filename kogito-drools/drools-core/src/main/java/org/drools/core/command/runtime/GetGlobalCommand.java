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

package org.drools.core.command.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.internal.command.Context;
import org.kie.api.runtime.KieSession;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetGlobalCommand
    implements
    GenericCommand<Object>, IdentifiableResult {

    private static final long serialVersionUID = 510l;

    @XmlAttribute (required=true)
    private String identifier;
    @XmlAttribute(name="out-identifier")
    private String outIdentifier;
    
    public GetGlobalCommand() {
    }

    public GetGlobalCommand(String identifier) {
        this.identifier = identifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Object execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();

        Object object = ksession.getGlobal( identifier );
        ExecutionResultImpl results = ((StatefulKnowledgeSessionImpl) ksession).getExecutionResult();
        if ( results != null ) {
            results.getResults().put( (this.outIdentifier != null) ? this.outIdentifier : this.identifier,
                                      object );
        }
        return object;
    }

    public String toString() {
        return "session.getGlobal( " + identifier + " );";
    }
}
