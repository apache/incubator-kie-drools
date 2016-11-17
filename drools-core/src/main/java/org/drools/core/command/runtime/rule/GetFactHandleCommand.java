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

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.Context;

public class GetFactHandleCommand
    implements
    ExecutableCommand<FactHandle> {

    private Object object;
    private boolean disconnected;
    
    public GetFactHandleCommand() {
    }

    public GetFactHandleCommand(Object object) {
        this.object = object;
        this.disconnected = false;
    }
    
    public GetFactHandleCommand(Object object, boolean disconnected) {
        this.object = object;
        this.disconnected = disconnected;
    }

    public FactHandle execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        InternalFactHandle factHandle = (InternalFactHandle) ksession.getFactHandle( object );
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
        return "ksession.getFactHandle( " + object + " );";
    }
}
