/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl.command;

import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.Context;

import java.util.Collections;

public abstract class CaseCommand<T> implements ExecutableCommand<T> {

    private static final long serialVersionUID = 4116744986913465571L;
    
    private CaseEventSupport emptyCaseEventSupport = new CaseEventSupport(Collections.emptyList());

    protected CaseEventSupport getCaseEventSupport(Context context) {        
        RuntimeManager runtimeManager = getRuntimeManager(context);
        if (runtimeManager instanceof PerCaseRuntimeManager) {
            CaseEventSupport caseEventSupport = (CaseEventSupport) ((PerCaseRuntimeManager) runtimeManager).getCaseEventSupport();
            if (caseEventSupport != null) {
                return caseEventSupport;
            }
        }
        
        return emptyCaseEventSupport;
    }
    
    protected RuntimeManager getRuntimeManager(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        RuntimeManager runtimeManager = (RuntimeManager) ksession.getEnvironment().get(EnvironmentName.RUNTIME_MANAGER);
        
        return runtimeManager;
    }
}
