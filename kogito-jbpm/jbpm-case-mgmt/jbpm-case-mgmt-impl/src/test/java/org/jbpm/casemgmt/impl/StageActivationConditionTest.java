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
package org.jbpm.casemgmt.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;

public class StageActivationConditionTest extends AbstractCaseServicesBaseTest {

    private static final String STAGE_WITH_ACTIVATION_COND = "NoStartNodeAdhocCaseWithActivationCondition";


    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<>();
        processes.add("cases/stage/activation/NoStartNodeAdhocCaseWithActivationCondition.bpmn2");
        return processes;
    }

    @Test
    public void testAutoComplete() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_ACTIVATION_COND);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);
        
        Collection<CaseStageInstance> activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(0);

        caseService.addDataToCaseFile(caseId, "readyToActivate", true);
        
        activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(1);
        
        caseService.addDataToCaseFile(caseId, "readyToComplete", true);
             
        assertCaseInstanceNotActive(caseId);
    }

}
