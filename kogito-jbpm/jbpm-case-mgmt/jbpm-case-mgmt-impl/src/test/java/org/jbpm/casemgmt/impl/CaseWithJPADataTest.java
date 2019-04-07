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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.model.CaseFileItem;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.marshalling.CaseMarshallerFactory;
import org.jbpm.casemgmt.impl.objects.Patient;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CaseWithJPADataTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseWithJPADataTest.class);

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        return processes;
    }

    @Test
    public void testStartEmptyCaseWithJPAData() {
        Patient patient = new Patient("john");
        Map<String, Object> data = new HashMap<>();
        data.put("patient", patient);
        data.put("name", "Patient case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            
            Patient patientFromCase = (Patient) cInstance.getCaseFile().getData("patient");
            Assertions.assertThat(patientFromCase).isNotNull();
            Assertions.assertThat(patient.getName()).isEqualTo(patientFromCase.getName());
            
            Collection<CaseFileItem> logs = caseRuntimeDataService.getCaseInstanceDataItems(caseId, new QueryContext());
            Assertions.assertThat(logs).hasSize(3);
            
            Map<String, CaseFileItem> mappedLogs = logs.stream().collect(toMap(CaseFileItem::getName, t -> t));
            // there is special org.jbpm.casemgmt.impl.audit.PatientCaseVariableIndexer so it produces multiple entries for single variable
            Assertions.assertThat(mappedLogs).containsKey("patient");
            Assertions.assertThat(mappedLogs).containsKey("patient_name");
            
            caseService.removeDataFromCaseFile(caseId, "patient");
            
            logs = caseRuntimeDataService.getCaseInstanceDataItems(caseId, new QueryContext());
            Assertions.assertThat(logs).hasSize(1);
            
            mappedLogs = logs.stream().collect(toMap(CaseFileItem::getName, t -> t));
            Assertions.assertThat(mappedLogs).doesNotContainKeys("patient", "patient_name");

            caseService.cancelCase(caseId);
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CANCELLED.getId());
            caseId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    protected DeploymentDescriptor createDeploymentDescriptor() {
        //add this listener by default
        listenerMvelDefinitions.add("new org.jbpm.casemgmt.impl.util.TrackingCaseEventListener()");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        DeploymentDescriptorBuilder ddBuilder = customDescriptor.getBuilder()
                .runtimeStrategy(RuntimeStrategy.PER_CASE)
                .addMarshalingStrategy(new ObjectModel("mvel", CaseMarshallerFactory.builder().withDoc().withJpa("org.jbpm.persistence.patient.example").toString()))
                .addWorkItemHandler(new NamedObjectModel("mvel", "StartCaseInstance", "new org.jbpm.casemgmt.impl.wih.StartCaseWorkItemHandler(ksession)"));

        listenerMvelDefinitions.forEach(
                listenerDefinition -> ddBuilder.addEventListener(new ObjectModel("mvel", listenerDefinition))
        );

        getProcessListeners().forEach(
                listener -> ddBuilder.addEventListener(listener)
        );
        
        getWorkItemHandlers().forEach(
               listener -> ddBuilder.addWorkItemHandler(listener)
        );


        return customDescriptor;
    }
}
