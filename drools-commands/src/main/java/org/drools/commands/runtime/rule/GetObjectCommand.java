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
package org.drools.commands.runtime.rule;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;

import org.drools.commands.IdentifiableResult;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GetObjectCommand
    implements
    ExecutableCommand<Object>, IdentifiableResult {

    @XmlElement(name="fact-handle", required=true)
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

    public DisconnectedFactHandle getDisconnectedFactHandle() {
        return disconnectedFactHandle;
    }

    public Object execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        FactHandle factHandle = this.factHandle;
        if( factHandle == null ) {
            factHandle = this.disconnectedFactHandle;
        }
        Object object = ksession.getObject( factHandle );

        if (this.outIdentifier != null) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult( this.outIdentifier, object );
        }

        return object;
    }

    public String toString() {
        return "session.getObject( " + factHandle + " );";
    }

}
