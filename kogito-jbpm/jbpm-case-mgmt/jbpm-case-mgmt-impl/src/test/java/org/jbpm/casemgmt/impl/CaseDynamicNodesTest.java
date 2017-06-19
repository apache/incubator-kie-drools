/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.casemgmt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.services.api.ProcessDefinitionNotFoundException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CaseDynamicNodesTest extends AbstractCaseServicesBaseTest {

    private static final String EMPTY_CASE_STAGE = "EmptyCaseStage";
    private static final String NOT_EXISTING_SUBPROCESS = "NotExistingSubprocess";

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/EmptyCaseStage.bpmn2");
        processes.add("cases/CaseWithTwoStages.bpmn2");
        processes.add("processes/UserTaskProcess.bpmn2");
        return processes;
    }

    @Test
    public void testAddDynamicSubprocessWithNotExistingProcessId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(),
                                              EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId,
                                                                false,
                                                                false,
                                                                false,
                                                                true);

        assertThat(caseInstance).isNotNull();
        CaseStatus initialStatus = CaseStatus.fromId(caseInstance.getStatus());

        assertThatThrownBy(() -> caseService.addDynamicSubprocess(caseId,
                                                                  NOT_EXISTING_SUBPROCESS,
                                                                  Collections.emptyMap())).isInstanceOf(ProcessDefinitionNotFoundException.class);

        // case instance status should not have changed
        CaseStatus afterStatus = CaseStatus.fromId(caseInstance.getStatus());
        assertThat(initialStatus.getId() == afterStatus.getId()).isTrue();
    }

    @Test
    public void testAddDynamicSubprocessToStageWithNotExistingProcessId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(),
                                              EMPTY_CASE_STAGE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId,
                                                                false,
                                                                false,
                                                                false,
                                                                true);

        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        CaseStatus initialStatus = CaseStatus.fromId(caseInstance.getStatus());

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        assertThat(stageId).isNotNull();

        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(caseId,
                                                                         stageId,
                                                                         NOT_EXISTING_SUBPROCESS,
                                                                         Collections.emptyMap())).isInstanceOf(ProcessDefinitionNotFoundException.class);
        // case instance status should not have changed
        CaseStatus afterStatus = CaseStatus.fromId(caseInstance.getStatus());
        assertThat(initialStatus.getId() == afterStatus.getId()).isTrue();

    }
}