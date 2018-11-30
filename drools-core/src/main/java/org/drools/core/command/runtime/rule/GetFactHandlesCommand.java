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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

@XmlAccessorType(XmlAccessType.NONE)
public class GetFactHandlesCommand
    implements
    ExecutableCommand<Collection<FactHandle>> {

    private transient ObjectFilter filter = null;

    @XmlAttribute
    private boolean disconnected = false;

    @XmlAttribute(name="out-identifier")
    private String  outIdentifier;

    public GetFactHandlesCommand() {
    }

    public GetFactHandlesCommand(ObjectFilter filter) {
        this.filter = filter;
    }
    public GetFactHandlesCommand(ObjectFilter filter, boolean disconnected) {
        this.filter = filter;
        this.disconnected = disconnected;
    }
    public GetFactHandlesCommand(boolean disconnected) {
        this.disconnected = disconnected;
    }

    public boolean isDisconnected() {
        return disconnected;
    }

    public void setDisconnected( boolean disconnected ) {
        this.disconnected = disconnected;
    }

    public Collection<FactHandle> execute( Context context ) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        Collection<FactHandle> disconnectedFactHandles = new ArrayList<FactHandle>();
        if ( filter != null ) {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( this.filter );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                if (outIdentifier != null) {
                    ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult( this.outIdentifier, disconnectedFactHandles );
                }
                return disconnectedFactHandles;
            }
            else {

                Collection<FactHandle> ksessionFactHandles = ksession.getFactHandles( this.filter );
                if (outIdentifier != null) {
                    ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier, new ArrayList<FactHandle>(ksessionFactHandles));
                }
                return ksessionFactHandles;
            }
        } else {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                if (outIdentifier != null) {
                    ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier, disconnectedFactHandles);
                }
                return disconnectedFactHandles;
            }
            else {
                Collection<FactHandle> ksessionFactHandles =  ksession.getFactHandles();
                if (outIdentifier != null) {
                    ((RegistryContext) context).lookup( ExecutionResultImpl.class ).setResult(this.outIdentifier, new ArrayList<FactHandle>(ksessionFactHandles));
                }
                return ksessionFactHandles;
            }
        }
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public String toString() {
        if ( filter != null ) {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), null, ObjectStoreWrapper.FACT_HANDLE )";
        } else {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), filter, ObjectStoreWrapper.FACT_HANDLE )";
        }
    }
}
