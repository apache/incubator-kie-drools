/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.command;

import java.util.Collection;
import java.util.Collections;

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.common.InternalAgenda;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.identity.IdentityProvider;

public abstract class CaseCommand<T> implements ExecutableCommand<T> {

    private static final long serialVersionUID = 4116744986913465571L;
    
    private CaseEventSupport emptyCaseEventSupport;
    private IdentityProvider identityProvider;
    
    public CaseCommand(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
        this.emptyCaseEventSupport = new CaseEventSupport(identityProvider, Collections.emptyList());
    }

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
    
    public IdentityProvider getIdentityProvider() {
        return this.identityProvider;
    }
    
    protected void triggerRules(KieSession ksession) {
        InternalAgenda agenda = ((InternalAgenda) ksession.getAgenda());
        if (agenda.focusStackSize() > 0) {
            agenda.setFocus("MAIN");
        }
        ksession.fireAllRules();
    }
    
    protected CaseFileInstance getCaseFile(KieSession ksession, String caseId) {
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() == 0) {
            return null;
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next(); 
        
        return caseFile;
    }
}
