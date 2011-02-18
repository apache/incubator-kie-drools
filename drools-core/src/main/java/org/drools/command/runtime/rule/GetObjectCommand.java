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

package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DefaultFactHandle;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectCommand
    implements
    GenericCommand<Object> {

    private FactHandle factHandle;
    private String     outIdentifier;
    
    public GetObjectCommand() { }

    public GetObjectCommand(FactHandle factHandle) {
        this.factHandle = factHandle;
    }
    
    public GetObjectCommand(FactHandle factHandle, String outIdentifier) {
        this.factHandle = factHandle;
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
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Object object = ksession.getObject( factHandle );
        
        if (this.outIdentifier != null) {
            ((StatefulKnowledgeSessionImpl)ksession).session.getExecutionResult()
                .getResults().put( this.outIdentifier, object );
        }
        
        return object;
    }
    
    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
