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

import java.util.ArrayList;
import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandlesCommand
    implements
    GenericCommand<Collection<FactHandle>> {

    private ObjectFilter filter = null;
    private boolean      disconnected = false;

    public GetFactHandlesCommand() {
    }

    public GetFactHandlesCommand(ObjectFilter filter) {
        this.filter = filter;
    }
    public GetFactHandlesCommand(ObjectFilter filter, boolean disconnected) {
        this.filter = filter;
        this.disconnected = disconnected;
    }

    public Collection<FactHandle> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        Collection<FactHandle> disconnectedFactHandles = new ArrayList<FactHandle>();
        if ( filter != null ) {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( this.filter );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
            }
            return disconnectedFactHandles;
        } else {
            Collection<InternalFactHandle> factHandles = ksession.getFactHandles( );
            if(factHandles != null && disconnected){
                for(InternalFactHandle factHandle: factHandles){
                    InternalFactHandle handle = factHandle.clone();
                    handle.disconnect();
                    disconnectedFactHandles.add(handle);
                }
            }
            return disconnectedFactHandles;
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
