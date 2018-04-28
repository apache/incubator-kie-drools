/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.demo.enrichment.DocumentType;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.document.service.impl.DocumentImpl;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;


public class AdHocConditionsTest extends AbstractCaseServicesBaseTest {
    
    private static final String ENRICHMENT_PROC_ID = "src.enrichment-case";

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("org/jbpm/casemgmt/demo/enrichment/enrichment-case.bpmn2");
        processes.add("org/jbpm/casemgmt/demo/enrichment/init.drl");
        return processes;
    }

    @Test
    public void testEnrichmentFlowNotValidManuallyApproved() throws Exception {
        
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);
        
        Collection<CaseStageInstance> activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);

        identityProvider.setRoles(Arrays.asList("HR"));
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
        
        TaskSummary task1 = tasks.stream().filter(task -> task.getName().equals("Upload Document test")).findAny().get();
        
        Map<String, Object> results = new HashMap<>();
        DocumentImpl document = new DocumentImpl("test", 10l, new Date());
        document.setContent("test".getBytes());
        results.put("uploadedDoc", document);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Manual Approval for test");     
        
        task1 = tasks.stream().filter(task -> task.getName().equals("Manual Approval for test")).findAny().get();
        
        Map<String, Object> inputs = userTaskService.getTaskInputContentByTaskId(task1.getId());
        assertThat(inputs).containsKeys("documentFile", "documentType");
        assertThat(inputs.get("documentFile")).isNotNull();
        assertThat(inputs.get("documentType")).isNotNull();
        
        assertThat(inputs.get("documentFile")).isInstanceOf(DocumentImpl.class);
        assertThat(inputs.get("documentType")).isInstanceOf(DocumentType.class);
        
        assertThat(inputs.get("documentFile")).hasFieldOrPropertyWithValue("name", "test");
        assertThat(inputs.get("documentType")).hasFieldOrPropertyWithValue("name", "test");
        
        results = new HashMap<>();        
        DocumentType documentType = new DocumentType("test", true, true, "txt", false);        
        results.put("decided", documentType);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(2);
        
        caseService.destroyCase(caseId);
    }
    
    @Test
    public void testEnrichmentFlowNotValidManuallyRejected() throws Exception {
        
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);
        
        Collection<CaseStageInstance> activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);

        identityProvider.setRoles(Arrays.asList("HR"));
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
        
        TaskSummary task1 = tasks.stream().filter(task -> task.getName().equals("Upload Document test")).findAny().get();
        
        Map<String, Object> results = new HashMap<>();
        DocumentImpl document = new DocumentImpl("test", 10l, new Date());
        document.setContent("test".getBytes());
        results.put("uploadedDoc", document);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Manual Approval for test");
     
        task1 = tasks.stream().filter(task -> task.getName().equals("Manual Approval for test")).findAny().get();
        results = new HashMap<>();
        
        DocumentType documentType = new DocumentType("test", true, false, "txt", true);        
        results.put("decided", documentType);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
     
        mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
        
        activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);
                
        caseService.destroyCase(caseId);
    }
    
    @Test
    public void testEnrichmentFlowNotValidDirectlyRejected() throws Exception {
        
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);
        
        Collection<CaseStageInstance> activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
        
        TaskSummary task1 = tasks.stream().filter(task -> task.getName().equals("Upload Document enablement")).findAny().get();
        
        Map<String, Object> results = new HashMap<>();
        DocumentImpl document = new DocumentImpl("enablement", 10l, new Date());
        document.setContent("enablement".getBytes());
        results.put("uploadedDoc", document);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);        
        
        mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
        
        activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);
                
        caseService.destroyCase(caseId);
    }

    @Test
    public void testEnrichmentFlowNotValidDirectlyApproved() throws Exception {
        
        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), ENRICHMENT_PROC_ID, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);
        
        Collection<CaseStageInstance> activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(3);
        
        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(4);
        
        Map<String, TaskSummary> mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document jbpm", 
                                             "Upload Document enablement", 
                                             "Upload Document test");
   
        
        TaskSummary task1 = tasks.stream().filter(task -> task.getName().equals("Upload Document jbpm")).findAny().get();
        
        Map<String, Object> results = new HashMap<>();
        DocumentImpl document = new DocumentImpl("jbpm", 10l, new Date());
        document.setContent("jbpm".getBytes());
        results.put("uploadedDoc", document);
        
        userTaskService.completeAutoProgress(task1.getId(), "john", results);
        
        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
        assertThat(tasks).hasSize(3);
        
        mappedTasks = mapTaskSummaries(tasks);
        assertThat(mappedTasks).containsKeys("Additional Client Details", 
                                             "Upload Document enablement", 
                                             "Upload Document test");

        activeStages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
        assertThat(activeStages).hasSize(2);
                
        caseService.destroyCase(caseId);
    }  

}
