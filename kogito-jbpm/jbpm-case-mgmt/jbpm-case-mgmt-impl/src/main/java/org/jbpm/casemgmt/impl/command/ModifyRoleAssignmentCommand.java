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
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.event.CaseEventSupport;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.identity.IdentityProvider;
import org.kie.api.runtime.Context;

import java.util.Collection;

/**
 * Modifies case role assignments
 */
public class ModifyRoleAssignmentCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335923L;

    private String roleName;
    private OrganizationalEntity entity;
    private boolean add;
    
    public ModifyRoleAssignmentCommand(IdentityProvider identityProvider, String roleName, OrganizationalEntity entity, boolean add) {
        super(identityProvider);
        this.roleName = roleName;
        this.entity = entity;
        this.add = add;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        
        Collection<? extends Object> caseFiles = ksession.getObjects(new ClassObjectFilter(CaseFileInstance.class));
        if (caseFiles.size() != 1) {
            throw new IllegalStateException("Not able to find distinct case file - found case files " + caseFiles.size());
        }
        CaseFileInstance caseFile = (CaseFileInstance) caseFiles.iterator().next();
        FactHandle factHandle = ksession.getFactHandle(caseFile);
        
        CaseEventSupport caseEventSupport = getCaseEventSupport(context);
        
        if (add) {
            caseEventSupport.fireBeforeCaseRoleAssignmentAdded(caseFile.getCaseId(), caseFile, roleName, entity);
            ((CaseAssignment) caseFile).assign(roleName, entity);
            caseEventSupport.fireAfterCaseRoleAssignmentAdded(caseFile.getCaseId(), caseFile, roleName, entity);
        } else {
            caseEventSupport.fireBeforeCaseRoleAssignmentRemoved(caseFile.getCaseId(), caseFile, roleName, entity);
            ((CaseAssignment) caseFile).remove(roleName, entity);            
            caseEventSupport.fireAfterCaseRoleAssignmentRemoved(caseFile.getCaseId(), caseFile, roleName, entity);
        }
        
        ksession.update(factHandle, caseFile);
        triggerRules(ksession);
        return null;
    }

}
