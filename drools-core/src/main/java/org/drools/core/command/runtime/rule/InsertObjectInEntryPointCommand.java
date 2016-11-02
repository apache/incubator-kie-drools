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

import org.drools.core.command.IdentifiableResult;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class InsertObjectInEntryPointCommand
        implements
        ExecutableCommand<FactHandle>, IdentifiableResult {

    private static final long serialVersionUID = 510l;
    @XmlElement
    private Object object;
    @XmlAttribute(name = "out-identifier", required = true)
    private String outIdentifier;
    private boolean returnObject = true;

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    public InsertObjectInEntryPointCommand() {
    }

    public InsertObjectInEntryPointCommand(Object object, String entryPoint) {
        this.object = object;
        this.entryPoint = entryPoint;
    }

    public InsertObjectInEntryPointCommand(Object object, String entryPoint, String outIdentifier) {
        super();
        this.object = object;
        this.entryPoint = entryPoint;
        this.outIdentifier = outIdentifier;
    }

    public FactHandle execute(Context context) {

        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        EntryPoint ep = ksession.getEntryPoint(entryPoint);
        FactHandle factHandle = ep.insert(object);

        DefaultFactHandle disconnectedHandle = ((DefaultFactHandle) factHandle).clone();
        disconnectedHandle.disconnect();

        if (outIdentifier != null) {
            if (this.returnObject) {
                ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier, object);
            }
            ((RegistryContext) context).lookup( ExecutionResultImpl.class ).getFactHandles().put(this.outIdentifier, disconnectedHandle);
        }

        return disconnectedHandle;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return this.object;
    }

    public String getOutIdentifier() {
        return this.outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public boolean isReturnObject() {
        return returnObject;
    }

    public void setReturnObject(boolean returnObject) {
        this.returnObject = returnObject;
    }

    public String toString() {
        return "session.getEntryPoint(" + entryPoint + ").insert(" + object + ");";
    }
}
