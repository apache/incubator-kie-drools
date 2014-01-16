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

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

import java.util.ArrayList;
import java.util.Collection;

public class GetFactHandlesInEntryPointCommand
    implements
    GenericCommand<Collection<FactHandle>> {

    private ObjectFilter filter = null;
    private boolean      disconnected = false;
    private String       entryPoint;

    public GetFactHandlesInEntryPointCommand(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, ObjectFilter filter) {
        this.entryPoint = entryPoint;
        this.filter = filter;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, ObjectFilter filter, boolean disconnected) {
        this.entryPoint = entryPoint;
        this.filter = filter;
        this.disconnected = disconnected;
    }

    public GetFactHandlesInEntryPointCommand(String entryPoint, boolean disconnected) {
        this.entryPoint = entryPoint;
        this.disconnected = disconnected;
    }

    public Collection<FactHandle> execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        EntryPoint ep = ksession.getEntryPoint(entryPoint);

        Collection<FactHandle> disconnectedFactHandles = new ArrayList<FactHandle>();
        if ( filter != null ) {
            Collection<InternalFactHandle> factHandles = ep.getFactHandles( this.filter );
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
            Collection<InternalFactHandle> factHandles = ep.getFactHandles( );
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
