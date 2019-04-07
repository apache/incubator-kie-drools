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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.impl.util.CountDownListenerFactory;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseSLAComplianceTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseSLAComplianceTest.class);
    
    protected static final String USER_TASK_SLA_CASE_P_ID = "UserTaskCaseSLA";
    protected static final String USER_TASK_SLA_EXPR_CASE_P_ID = "UserTaskCaseSLAExpr";

    @Rule
    public TestName name = new TestName();
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/UserTaskCaseWithSLA.bpmn2");
        processes.add("cases/UserTaskCaseWithSLAExpr.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        // rules for SLA calculation
        processes.add("rules/sla-rules.drl");
        return processes;
    }
    
    @After
    public void tearDown() { 
        super.tearDown();
        CountDownListenerFactory.clear();
    }

    @Test
    public void testStartCaseWithSLAEscalation() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_PENDING, cInstance.getSlaCompliance().intValue());
            
            List<TaskSummary> escalationTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(escalationTasks).hasSize(0);
            
            CountDownListenerFactory.getExisting("slaCompliance").waitTillCompleted();
            
            cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_VIOLATED, cInstance.getSlaCompliance().intValue());
            
            escalationTasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(escalationTasks).hasSize(1);
            TaskSummary task = escalationTasks.get(0);
            assertThat(task.getName()).isEqualTo("SLA violation for case " + caseId);
            assertThat(task.getDescription()).isEqualTo("Service Level Agreement has been violated for case " + caseId);

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
    public void testStartCaseWithSLANotification() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_PENDING, cInstance.getSlaCompliance().intValue());
            
            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext());                        
            assertThat(activeNodes).hasSize(1);
            Iterator<NodeInstanceDesc> it = activeNodes.iterator();
            NodeInstanceDesc active = it.next();
            assertThat(active.getName()).isEqualTo("Hello1");            
            
            CountDownListenerFactory.getExisting("slaCompliance").waitTillCompleted();
            
            cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_VIOLATED, cInstance.getSlaCompliance().intValue());
            
            activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext());                        
            assertThat(activeNodes).hasSize(2);
            it = activeNodes.iterator();
            active = it.next();
            assertThat(active.getName()).isEqualTo("Hello1");
            active = it.next();
            assertThat(active.getName()).isEqualTo("[Dynamic] SLA Violation for case " + caseId);

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
    public void testStartCaseWithSLASubprocess() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_PENDING, cInstance.getSlaCompliance().intValue());
            
            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(1, caseProcessInstances.size());
            
            CountDownListenerFactory.getExisting("slaCompliance").waitTillCompleted();
            
            cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_VIOLATED, cInstance.getSlaCompliance().intValue());
            
            caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(2, caseProcessInstances.size());
            for (ProcessInstanceDesc pi : caseProcessInstances) {
                assertThat(pi.getCorrelationKey()).startsWith(HR_CASE_ID);
            }

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
    public void testStartCaseWithSLAForGoldCustomerByRules() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        data.put("CustomerType", "Gold");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_PENDING, cInstance.getSlaCompliance().intValue());
            
            Object calcualtedSlaDueDate = cInstance.getCaseFile().getData("slaDueDate");
            assertNotNull(calcualtedSlaDueDate);
            assertThat(calcualtedSlaDueDate).isEqualTo("1s");
            
            CountDownListenerFactory.getExisting("slaCompliance").waitTillCompleted();
            
            cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_VIOLATED, cInstance.getSlaCompliance().intValue());
            
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
    public void testStartCaseWithSLAForSilverCustomerByRules() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        data.put("CustomerType", "Silver");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_PENDING, cInstance.getSlaCompliance().intValue());
            
            Object calcualtedSlaDueDate = cInstance.getCaseFile().getData("slaDueDate");
            assertNotNull(calcualtedSlaDueDate);
            assertThat(calcualtedSlaDueDate).isEqualTo("2s");
            
            CountDownListenerFactory.getExisting("slaCompliance").waitTillCompleted();
            
            cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_VIOLATED, cInstance.getSlaCompliance().intValue());
            
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
    public void testStartCaseWithSLAForNewCustomerByRules() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("admin", new UserImpl("mary"));

        Map<String, Object> data = new HashMap<>();
        data.put("s", "Case with SLA");
        data.put("CustomerType", "New");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_SLA_EXPR_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(HR_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            assertEquals(ProcessInstance.SLA_NA, cInstance.getSlaCompliance().intValue());
            
            Object calcualtedSlaDueDate = cInstance.getCaseFile().getData("slaDueDate");
            assertNotNull(calcualtedSlaDueDate);
            assertThat(calcualtedSlaDueDate).isEqualTo("");
                 
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

    @Override
    protected List<ObjectModel> getProcessListeners() {
        List<ObjectModel> listeners = super.getProcessListeners();
        
        listeners.add(new ObjectModel("mvel", "org.jbpm.casemgmt.impl.util.CountDownListenerFactory.getSLA(\"slaCompliance\", 1)"));
        if (name.getMethodName().equals("testStartCaseWithSLAEscalation")) {
            listeners.add(new ObjectModel("mvel", "new org.jbpm.casemgmt.impl.wih.EscalateToAdminSLAViolationListener()"));
        } else if (name.getMethodName().equals("testStartCaseWithSLANotification")) {
            listeners.add(new ObjectModel("mvel", "new org.jbpm.casemgmt.impl.wih.NotifyOwnerSLAViolationListener()"));
        } else if (name.getMethodName().equals("testStartCaseWithSLASubprocess")) {
            listeners.add(new ObjectModel("mvel", "new org.jbpm.casemgmt.impl.wih.StartProcessSLAViolationListener(\"DataVerification\")"));
        }

        return listeners;
    }
    
    @Override
    protected List<NamedObjectModel> getWorkItemHandlers() {
        List<NamedObjectModel> handlers = super.getWorkItemHandlers();

        handlers.add(new NamedObjectModel("mvel", "Email",
                "new org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler()"));

        return handlers;
    }
}
