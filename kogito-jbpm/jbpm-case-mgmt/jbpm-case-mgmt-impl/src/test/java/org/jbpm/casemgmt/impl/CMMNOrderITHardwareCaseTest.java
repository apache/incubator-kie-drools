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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.impl.util.CountDownListenerFactory;
import org.jbpm.document.service.impl.DocumentImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMMNOrderITHardwareCaseTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CMMNOrderITHardwareCaseTest.class);
    
    protected static final String CMMN_CASE_ORDER_IT_ID = "itorders.orderhardware";

    @Rule
    public TestName name = new TestName();
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/cmmn/CMMN-OrderITHardwareCase.cmmn");
        processes.add("processes/BPMN2-PlaceOrder.bpmn2");
        return processes;
    }
    
    @After
    public void tearDown() { 
        super.tearDown();
        CountDownListenerFactory.clear();
    }

    @Test
    public void testStartOrderITHardwareCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));
        roleAssignments.put("supplier", new UserImpl("john"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_ORDER_IT_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_ORDER_IT_ID, caseFile);
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
            assertThat(task.getName()).isEqualTo("Prepare hardware spec");
            
            Map<String, Object> params = new HashMap<>();
            DocumentImpl doc = new DocumentImpl("hwspec.pdf", 100, new Date());
            doc.setContent("test value".getBytes());
            params.put("hwSpec_", doc);
            params.put("supplierComment_", "best offer");
            userTaskService.completeAutoProgress(task.getId(), "john", params);
            
            Collection<CaseMilestoneInstance> milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(1);
            assertThat(milestones.iterator().next().getName()).isEqualTo("Hardware spec ready");
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Manager approval");
            
            params = new HashMap<>();            
            params.put("approved_", true);
            params.put("managerComment_", "good to be ordered");
            userTaskService.completeAutoProgress(task.getId(), "mary", params);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(2);
            
            Iterator<CaseMilestoneInstance> it = milestones.iterator();
            assertThat(it.next().getName()).isEqualTo("Hardware spec ready");
            assertThat(it.next().getName()).isEqualTo("Manager decision");
            
            Collection<ProcessInstanceDesc> pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertThat(pInstances).hasSize(2);            
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Place order");
            
            params = new HashMap<>();            
            params.put("ordered_", true);
            params.put("info_", "order placed with order number XXXX-YYY-ZZZZ");
            userTaskService.completeAutoProgress(task.getId(), "john", params);
            

            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(3);
            
            it = milestones.iterator();
            assertThat(it.next().getName()).isEqualTo("Hardware spec ready");
            assertThat(it.next().getName()).isEqualTo("Manager decision");
            assertThat(it.next().getName()).isEqualTo("Milestone 1: Order placed");

            caseService.addDataToCaseFile(caseId, "shipped", true);

            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(4);
            
            it = milestones.iterator();
            assertThat(it.next().getName()).isEqualTo("Hardware spec ready");
            assertThat(it.next().getName()).isEqualTo("Manager decision");
            assertThat(it.next().getName()).isEqualTo("Milestone 1: Order placed");
            assertThat(it.next().getName()).isEqualTo("Milestone 2: Order shipped");
            
            caseService.addDataToCaseFile(caseId, "delivered", true);

            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(5);
            
            it = milestones.iterator();
            assertThat(it.next().getName()).isEqualTo("Hardware spec ready");
            assertThat(it.next().getName()).isEqualTo("Manager decision");
            assertThat(it.next().getName()).isEqualTo("Milestone 1: Order placed");
            assertThat(it.next().getName()).isEqualTo("Milestone 2: Order shipped");
            assertThat(it.next().getName()).isEqualTo("Milestone 3: Delivered to customer");
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Customer satisfaction survey");
            
            caseService.closeCase(caseId, "all work done");
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CLOSED.getId());
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
    public void testStartOrderITHardwareCaseManagerRejected() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));
        roleAssignments.put("supplier", new UserImpl("john"));

        Map<String, Object> data = new HashMap<>();        
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CMMN_CASE_ORDER_IT_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CMMN_CASE_ORDER_IT_ID, caseFile);
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
            assertThat(task.getName()).isEqualTo("Prepare hardware spec");
            
            Map<String, Object> params = new HashMap<>();
            DocumentImpl doc = new DocumentImpl("hwspec.pdf", 100, new Date());
            doc.setContent("test value".getBytes());
            params.put("hwSpec_", doc);
            params.put("supplierComment_", "best offer");
            userTaskService.completeAutoProgress(task.getId(), "john", params);
            
            Collection<CaseMilestoneInstance> milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(1);
            assertThat(milestones.iterator().next().getName()).isEqualTo("Hardware spec ready");
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("mary", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Manager approval");
            
            params = new HashMap<>();            
            params.put("approved_", false);
            params.put("managerComment_", "too expensive");
            userTaskService.completeAutoProgress(task.getId(), "mary", params);
            
            milestones = caseRuntimeDataService.getCaseInstanceMilestones(caseId, true, new QueryContext());
            assertThat(milestones).hasSize(2);
            
            Iterator<CaseMilestoneInstance> it = milestones.iterator();
            assertThat(it.next().getName()).isEqualTo("Hardware spec ready");
            assertThat(it.next().getName()).isEqualTo("Manager decision");
            
            Collection<ProcessInstanceDesc> pInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId, new QueryContext());
            assertThat(pInstances).hasSize(1);            
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertThat(tasks).hasSize(1);
            
            task = tasks.get(0);
            assertThat(task).isNotNull();
            assertThat(task.getName()).isEqualTo("Order rejected");
            
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
