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
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.NONE)
public class GetFactHandlesCommand
    implements
    GenericCommand<Collection<FactHandle>> {

    private transient ObjectFilter filter = null;

    @XmlAttribute
    private boolean disconnected = false;

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
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        Collection<FactHandle> disconnectedFactHandles = new ArrayList<FactHandle>();
        if ( filter != null ) {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( this.filter );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                return disconnectedFactHandles;
            }
            else { 
                return ksession.getFactHandles( this.filter );
            }
        } else {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
                return disconnectedFactHandles;
            }
            else { 
                return ksession.getFactHandles();
            }
        }
    }

    public String toString() {
        if ( filter != null ) {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), null, ObjectStoreWrapper.FACT_HANDLE )";
        } else {
            return "new ObjectStoreWrapper( reteooStatefulSession.getObjectStore(), filter, ObjectStoreWrapper.FACT_HANDLE )";
        }
    }
}
