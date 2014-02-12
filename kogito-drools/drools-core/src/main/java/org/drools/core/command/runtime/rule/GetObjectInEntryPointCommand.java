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

package org.drools.core.command.runtime.rule;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectInEntryPointCommand
    implements
    GenericCommand<Object>, IdentifiableResult {

    private FactHandle factHandle;
    private String     outIdentifier;

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    public GetObjectInEntryPointCommand() { }

    public GetObjectInEntryPointCommand(FactHandle factHandle, String entryPoint) {
        this.factHandle = factHandle;
        this.entryPoint = entryPoint;
    }

    public GetObjectInEntryPointCommand(FactHandle factHandle, String entryPoint, String outIdentifier) {
        this.factHandle = factHandle;
        this.entryPoint = entryPoint;
        this.outIdentifier = outIdentifier;
    }

    @XmlAttribute(name="out-identifier", required=true)
    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @XmlAttribute(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        factHandle = new DefaultFactHandle(factHandleId);
    }
    
    public String getFactHandleFromString() {
        return factHandle.toExternalForm();
    }

    public Object execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Object object = ep.getObject( factHandle );
        
        if (this.outIdentifier != null) {
            ((StatefulKnowledgeSessionImpl)ksession).getExecutionResult()
                .getResults().put( this.outIdentifier, object );
        }
        
        return object;
    }
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).getObject( " + factHandle + " );";
    }

}
