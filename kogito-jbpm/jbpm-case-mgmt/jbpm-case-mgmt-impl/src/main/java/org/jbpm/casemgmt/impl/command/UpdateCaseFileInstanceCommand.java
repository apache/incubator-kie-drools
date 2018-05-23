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

import org.drools.core.ClassObjectFilter;
import org.drools.core.command.impl.RegistryContext;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Updates case file meta data
 */
public class UpdateCaseFileInstanceCommand extends CaseCommand<Void> {

    private static final long serialVersionUID = 6345222909719335953L;

    private String caseDefinitionId;
    
    public UpdateCaseFileInstanceCommand(String caseDefinitionId) {
        super(null);
        this.caseDefinitionId = caseDefinitionId;
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
        ((CaseFileInstanceImpl)caseFile).setDefinitionId(caseDefinitionId);
        ksession.update(factHandle, caseFile);
        return null;
    }
}
