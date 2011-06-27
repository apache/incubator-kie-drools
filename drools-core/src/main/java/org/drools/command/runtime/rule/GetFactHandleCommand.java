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

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalFactHandle;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class GetFactHandleCommand
    implements
    GenericCommand<FactHandle> {

    private Object object;
    private boolean disconnected;

    public GetFactHandleCommand(Object object) {
        this.object = object;
        this.disconnected = false;
    }
    
    public GetFactHandleCommand(Object object, boolean disconnected) {
        this.object = object;
        this.disconnected = disconnected;
    }

    public FactHandle execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        InternalFactHandle factHandle = (InternalFactHandle) ksession.getFactHandle( object );
        if ( factHandle != null && disconnected){
            InternalFactHandle handle = factHandle.clone();
            handle.disconnect();
            return handle;
        }
        return null;
    }

    public String toString() {
        return "ksession.getFactHandle( " + object + " );";
    }
}
