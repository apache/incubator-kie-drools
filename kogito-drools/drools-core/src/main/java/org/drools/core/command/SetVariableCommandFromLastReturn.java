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

package org.drools.core.command;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.common.InternalFactHandle;
import org.kie.internal.command.Context;

public class SetVariableCommandFromLastReturn
    implements
    GenericCommand<Object> {
    private String identifier;
    private String contextName;

    public SetVariableCommandFromLastReturn(String identifier) {
        this.identifier = identifier;
    }

    public SetVariableCommandFromLastReturn(String contextName,
                                            String identifier) {
        this.identifier = identifier;
        this.contextName = contextName;
    }

    public Object execute(Context context) {
        Context targetCtx;
        if ( this.contextName == null ) {
            targetCtx = context;
        } else {
            targetCtx = context.getContextManager().getContext( this.contextName );
        }

        GetDefaultValue sim = (GetDefaultValue) context.get( "simulator" );

        Object o = sim.getObject();
        // for FactHandle's we store the handle on a map and the actual object as
        if ( o instanceof FactHandle ) {
            Map<String, FactHandle> handles = (Map<String, FactHandle>) targetCtx.get( "h" );
            if ( handles == null ) {
                handles = new HashMap<String, FactHandle>();
                targetCtx.set( "h",
                               handles );
            }
            handles.put( identifier,
                         (FactHandle) o );

            o = ((InternalFactHandle) o).getObject();

        }

        targetCtx.set( identifier,
                       o );

        return o;
    }

}
