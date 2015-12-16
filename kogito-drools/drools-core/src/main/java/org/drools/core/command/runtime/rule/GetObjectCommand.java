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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DisconnectedFactHandle;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectCommand
    implements
    GenericCommand<Object>, IdentifiableResult {

    private DisconnectedFactHandle disconnectedFactHandle;

    private transient FactHandle factHandle;

    @XmlAttribute(name="out-identifier", required=true)
    @XmlSchemaType(name="string")
    private String     outIdentifier;

    public GetObjectCommand() { }

    public GetObjectCommand(FactHandle factHandle) {
        setFactHandle(factHandle);
    }

    public GetObjectCommand(FactHandle factHandle, String outIdentifier) {
        this(factHandle);
        this.outIdentifier = outIdentifier;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        FactHandle factHandle = DefaultFactHandle.createFromExternalFormat(factHandleId);
        setFactHandle(factHandle);
    }

    public String getFactHandleFromString() {
        return factHandle.toExternalForm();
    }

    public FactHandle getFactHandle() {
        return this.factHandle;
    }

    public void setFactHandle(FactHandle factHandle) {
        this.factHandle = factHandle;
        this.disconnectedFactHandle = DisconnectedFactHandle.newFrom(factHandle);
    }

    public Object execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();

        FactHandle factHandle = this.factHandle;
        if( factHandle == null ) {
            factHandle = this.disconnectedFactHandle;
        }
        Object object = ksession.getObject( factHandle );

        if (this.outIdentifier != null) {
            ((StatefulKnowledgeSessionImpl)ksession).getExecutionResult()
                .getResults().put( this.outIdentifier, object );
        }

        return object;
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
