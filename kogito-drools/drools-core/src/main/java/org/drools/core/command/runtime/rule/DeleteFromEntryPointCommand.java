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

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.DisconnectedFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class DeleteFromEntryPointCommand
        implements GenericCommand<Void> {

    @XmlAttribute(name="entry-point")
    private String entryPoint;

    private DisconnectedFactHandle handle;

    private FactHandle.State fhState = FactHandle.State.ALL;

    public DeleteFromEntryPointCommand() {
    }

    public DeleteFromEntryPointCommand(FactHandle handle, String entryPoint) {
        this.handle = DisconnectedFactHandle.newFrom( handle );
        this.entryPoint = entryPoint;
    }

    public DeleteFromEntryPointCommand(FactHandle handle, String entryPoint, FactHandle.State fhState) {
        this(handle, entryPoint);
        this.fhState = fhState;
    }

    public Void execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        ksession.getEntryPoint( entryPoint ).delete( handle, fhState );
        return null;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }

    @XmlElement(name="fact-handle", required=true)
    public void setFactHandleFromString(String factHandleId) {
        handle = new DisconnectedFactHandle(factHandleId);
    }

    public String getFactHandleFromString() {
        return handle.toExternalForm();
    }

    public String toString() {
        return "session.getEntryPoint( " + entryPoint + " ).retract( " + handle + " );";
    }

}
