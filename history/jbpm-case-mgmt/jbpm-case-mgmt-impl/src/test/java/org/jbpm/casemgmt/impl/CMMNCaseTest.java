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
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.impl.util.CountDownListenerFactory;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMMNCaseTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CMMNCaseTest.class);
    
    protected static final String CMMN_CASE_MILESTONE_ID = "CMMN-HumanTaskMilestoneSentries";
    protected static final String CMMN_CASE_STAGE_ID = "CMMN-StageWithHumanTaskCase";
    protected static final String CMMN_CASE_DATA_ID = "CMMN-DataInputsAndOutputsCase";
    protected static final String CMMN_CASE_RULE_SENTRY_ID = "CMMN-HumanTaskMilestoneRuleSentries";
    protected static final String CMMN_CASE_RULE_HT_ID = "CMMN-StageWithActivationByTaskCase";
    protected static final String CMMN_CASE_DMN_DECISION_ID = "CMMN-DecisionTaskWithDMNCase";
    protected static final String CMMN_CASE_DMN_DECISION_APPROVAL_ID = "CMMN-DecisionTaskWithDMNApprovalCase";

    @Rule
    public TestName name = new TestName();
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/cmmn/CMMN-HumanTaskMilestoneSentries.cmmn");
        processes.add("cases/cmmn/CMMN-StageWithHumanTaskCase.cmmn");
        processes.add("cases/cmmn/CMMN-DataInputsAndOutputsCase.cmmn");
        processes.add("cases/cmmn/CMMN-HumanTaskMilestoneRuleSentries.cmmn");
        processes.add("cases/cmmn/CMMN-StageWithActivationByTaskCase.cmmn");
        processes.add("cases/cmmn/CMMN-DecisionTaskWithDMNCase.cmmn");
        processes.add("cases/cmmn/CMMN-DecisionTaskWithDMNApprovalCase.cmmn");
        processes.add("dmn/0020-vacation-days.dmn");
        return processes;
    }
    
    @After
    public void tearDown() { 
        super.tearDown();
        CountDownListenerFactory.clear();
    }

    @Test
    public void testStartMilestoneAndHumanTaskCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_MILESTONE_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_MILESTONE_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
                       
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("First task");
            
            userTaskService.start(task.getId(), "john");
            
            Collection<CaseMilestoneInstance> milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, false, new QueryContext());
            assertThat(milestones).hasSize(1);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(0);
            
            caseService.addDataToCaseFile(caseId, "shipped", true);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(1);
            
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
    
    
    @Test
    public void testStartStageAndHumanTaskCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_STAGE_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_STAGE_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
                       
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("First task");
            
            userTaskService.start(task.getId(), "john");
            
            Collection<CaseStageInstance> stages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
            assertThat(stages).hasSize(0);
            
            caseService.addDataToCaseFile(caseId, "ordered", true);
            
            stages = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext());
            assertThat(stages).hasSize(1);
            
            caseService.triggerAdHocFragment(caseId, stages.iterator().next().getId(), "Stage task", null);
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Stage task");
            
            userTaskService.start(task.getId(), "mary");
            
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
    
    @Test
    public void testStartDataMappingHumanTaskCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();   
        data.put("invoice", "text invoice");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_DATA_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_DATA_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
                       
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("First task");
            
            Map<String, Object> taskInputs = userTaskService.getTaskInputContentByTaskId(task.getId());
            assertThat(taskInputs).isNotNull();
            assertThat(taskInputs).containsEntry("invoice", "text invoice");
            
            Map<String, Object> params = new HashMap<>();
            params.put("approval", true);
            userTaskService.completeAutoProgress(task.getId(), "john", params);
           
            Object shipped = caseService.getCaseFileInstance(caseId).getData("shipped");
            assertThat(shipped).isNotNull();
            assertThat(shipped).isEqualTo(true);
            
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
    
    @Test
    public void testStartHumanTaskRuleSentryCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_RULE_SENTRY_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_RULE_SENTRY_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
                       
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(0);
            
            caseService.addDataToCaseFile(caseId, "ordered", true);
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("First task");
            
            userTaskService.start(task.getId(), "john");
                        
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
    
    @Test
    public void testStartHumanTaskByCompletingAnotherOneCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_RULE_HT_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_RULE_HT_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
                       
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Complete to activate");
            
            Map<String, Object> params = new HashMap<>();
            params.put("ordered_", true);
            userTaskService.completeAutoProgress(task.getId(), "john", params);
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Activated");
            
            userTaskService.start(task.getId(), "mary");
                        
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
    
    @Test
    public void testStartDMNDecisionTaskCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();        
        
        Map<String, Object> data = new HashMap<>();        
        data.put("age", 16);
        data.put("yearsOfService", 1);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            Object vacationDays = cInstance.getCaseFile().getData("vacationDays");
            assertThat(vacationDays).isNotNull();
            assertThat(vacationDays).isInstanceOf(BigDecimal.class);
            assertThat(vacationDays).isEqualTo(BigDecimal.valueOf(5));
            
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
   
    @Test
    public void testStartDMNDecisionTaskCaseApprovalRequired() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();        
        roleAssignments.put("owner", new UserImpl("john"));
        
        Map<String, Object> data = new HashMap<>();        
        data.put("age", 16);
        data.put("yearsOfService", 1);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_APPROVAL_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_APPROVAL_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            Object vacationDays = cInstance.getCaseFile().getData("vacationDays");
            assertThat(vacationDays).isNotNull();
            assertThat(vacationDays).isInstanceOf(BigDecimal.class);
            assertThat(vacationDays).isEqualTo(BigDecimal.valueOf(27));
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("More than 25 days requires approval");
            
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
    
    @Test
    public void testStartDMNDecisionTaskCaseNoApprovalRequired() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();        
        roleAssignments.put("owner", new UserImpl("john"));
        
        Map<String, Object> data = new HashMap<>();        
        data.put("age", 44);
        data.put("yearsOfService", 20);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_APPROVAL_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_DMN_DECISION_APPROVAL_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            Object vacationDays = cInstance.getCaseFile().getData("vacationDays");
            assertThat(vacationDays).isNotNull();
            assertThat(vacationDays).isInstanceOf(BigDecimal.class);
            assertThat(vacationDays).isEqualTo(BigDecimal.valueOf(24));
            
            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            TaskSummary task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Less than 25 days you're good to go");
            
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
}
