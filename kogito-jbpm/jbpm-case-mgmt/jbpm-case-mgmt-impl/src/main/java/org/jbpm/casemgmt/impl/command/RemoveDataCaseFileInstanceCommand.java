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

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.identity.IdentityProvider;
import org.kie.api.runtime.Context;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Updates case file with new data
 */
public class RemoveDataCaseFileInstanceCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335953L;

    private List<String> variableNames;
    private AuthorizationManager authorizationManager;
    
    public RemoveDataCaseFileInstanceCommand(IdentityProvider identityProvider, List<String> variableNames, AuthorizationManager authorizationManager) {                
        super(identityProvider);
        this.variableNames = variableNames;        
        this.authorizationManager = authorizationManager;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() != 1) {
            throw new IllegalStateException("Not able to find distinct case file - found case files " + caseFiles.size());
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();
        
        // apply authorization
        authorizationManager.checkDataAuthorization(caseFile.getCaseId(), caseFile, variableNames);
        
        FactHandle factHandle = ksession.getFactHandle(caseFile);
        
        Map<String, Object> remove = new HashMap<>();        
        variableNames.forEach(p -> remove.put(p, caseFile.getData(p)));
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeCaseDataRemoved(caseFile.getCaseId(), caseFile, caseFile.getDefinitionId(), remove);
        
        variableNames.forEach(p -> caseFile.remove(p));
        
        ksession.update(factHandle, caseFile);
        triggerRules(ksession);
        
        caseEventSupport.fireAfterCaseDataRemoved(caseFile.getCaseId(), caseFile, caseFile.getDefinitionId(), remove);
        return null;
    }

}
