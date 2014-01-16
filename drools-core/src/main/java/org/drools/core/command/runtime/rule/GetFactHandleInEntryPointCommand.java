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
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;

public class GetFactHandleInEntryPointCommand
    implements
    GenericCommand<FactHandle> {

    private Object object;
    private boolean disconnected;
    private String entryPoint;

    public GetFactHandleInEntryPointCommand() {
    }

    public GetFactHandleInEntryPointCommand(Object object, String entryPoint) {
        this.object = object;
        this.entryPoint = entryPoint;
        this.disconnected = false;
    }

    public GetFactHandleInEntryPointCommand(Object object, String entryPoint, boolean disconnected) {
        this.object = object;
        this.entryPoint = entryPoint;
        this.disconnected = disconnected;
    }

    public FactHandle execute(Context context) {
        KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
        EntryPoint ep = ksession.getEntryPoint(entryPoint);
        InternalFactHandle factHandle = (InternalFactHandle) ep.getFactHandle( object );
        if ( factHandle != null ){
            InternalFactHandle handle = factHandle.clone();
            if ( disconnected ) {
                handle.disconnect();
            }
            return handle;
        }
        return null;
    }

    public String toString() {
        return "ksession.getEntryPoint( " + entryPoint + " ).getFactHandle( " + object + " );";
    }
}
