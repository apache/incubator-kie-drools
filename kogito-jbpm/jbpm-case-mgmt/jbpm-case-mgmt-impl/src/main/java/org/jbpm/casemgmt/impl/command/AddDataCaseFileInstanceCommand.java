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
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.api.auth.AuthorizationManager;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.identity.IdentityProvider;

/**
 * Updates case file with new data
 */
public class AddDataCaseFileInstanceCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335953L;

    private Map<String, Object> parameters;
    private AuthorizationManager authorizationManager;
    
    private List<String> accessRestriction;
    
    public AddDataCaseFileInstanceCommand(IdentityProvider identityProvider, Map<String, Object> parameters, List<String> accessRestriction, AuthorizationManager authorizationManager) {
        super(identityProvider);
        this.parameters = parameters;   
        this.authorizationManager = authorizationManager;
        this.accessRestriction = accessRestriction;
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
        authorizationManager.checkDataAuthorization(caseFile.getCaseId(), caseFile, parameters.keySet());
        
        FactHandle factHandle = ksession.getFactHandle(caseFile);
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        caseEventSupport.fireBeforeCaseDataAdded(caseFile.getCaseId(), caseFile, caseFile.getDefinitionId(), parameters);
        caseFile.addAll(parameters);
        
        // setup data restriction if any are given
        for (String name : parameters.keySet()) {
            if (accessRestriction != null) {
                ((CaseFileInstanceImpl) caseFile).addDataAccessRestriction(name, accessRestriction);
            } else {
                ((CaseFileInstanceImpl) caseFile).removeDataAccessRestriction(name);
            }
        }
        
        
        ksession.update(factHandle, caseFile);
        triggerRules(ksession);
        caseEventSupport.fireAfterCaseDataAdded(caseFile.getCaseId(), caseFile, caseFile.getDefinitionId(), parameters);
        return null;
    }
}
