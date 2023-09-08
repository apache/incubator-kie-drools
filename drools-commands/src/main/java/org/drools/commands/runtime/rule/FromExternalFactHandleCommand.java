/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.runtime.rule;

import java.util.Collection;

import org.drools.core.common.InternalFactHandle;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;

public class FromExternalFactHandleCommand implements ExecutableCommand<FactHandle> {

    private String factHandleExternalForm;
    private boolean disconnected;
    
    public FromExternalFactHandleCommand() {
    }

    public FromExternalFactHandleCommand(String factHandleExternalForm) {
        this(factHandleExternalForm, false);
    }

    public FromExternalFactHandleCommand(String factHandleExternalForm, boolean disconnected) {
        this.factHandleExternalForm = factHandleExternalForm;
        this.disconnected = disconnected;
    }

    public FactHandle execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        Collection<FactHandle> factHandles = ksession.getFactHandles();
        long fhId = Long.parseLong(factHandleExternalForm.split(":")[1]);
        for (FactHandle factHandle : factHandles) {
            if (factHandle instanceof InternalFactHandle
                    && ((InternalFactHandle) factHandle).getId() == fhId) {
                InternalFactHandle fhClone = ((InternalFactHandle) factHandle).clone();
                if (disconnected) {
                    fhClone.disconnect();
                }
                return fhClone;
            }
        }
        return null;
    }

    public String toString() {
        return "ksession.getFactHandle( " + factHandleExternalForm + " );";
    }
}
