/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.event.ProcessInstanceDataEvent;
import org.kie.kogito.services.event.UserTaskInstanceDataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.UserTaskInstanceEventBody;
import org.kie.kogito.uow.UnitOfWork;


public class PublishEventTest extends AbstractCodegenTest {

   
    @Test
    public void testBusinessRuleProcessStartToEnd() throws Exception {
        
        Application app = generateCode(Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"), Collections.singletonList("ruletask/BusinessRuleTask.drl"));        
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        Process<? extends Model> p = app.processes().processById("BusinessRuleTask");
        
        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);        
        uow.end();

        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(1);
        
        DataEvent<?> event = events.get(0);
        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent processDataEvent = (ProcessInstanceDataEvent) event;
        assertThat(processDataEvent.getKogitoProcessinstanceId()).isNotNull(); 
        assertThat(processDataEvent.getKogitoParentProcessinstanceId()).isNull(); 
        assertThat(processDataEvent.getKogitoRootProcessinstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(processDataEvent.getKogitoProcessinstanceState()).isEqualTo("2");
        assertThat(processDataEvent.getSource()).isEqualTo("http://myhost/BusinessRuleTask");
        
        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "BusinessRuleTask", "Default Process", 2);
        
        assertThat(body.getNodeInstances()).hasSize(3).extractingResultOf("getNodeType").contains("StartNode", "RuleSetNode", "EndNode");
        
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);
        
        assertThat(body.getVariables()).hasSize(1).containsKey("person");
        assertThat(body.getVariables().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true); 
    }
    
    
    @Test
    public void testBasicUserTaskProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        uow.end();     
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);        
        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(2);
        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "UserTasksProcess", "UserTasksProcess", 1);
        assertThat(body.getNodeInstances()).hasSize(2).extractingResultOf("getNodeType").contains("StartNode", "HumanTaskNode");
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").containsNull();// human task is active thus null for leave time
        
        assertUserTaskInstanceEvent(events.get(1), "First Task", null, "1", "Ready", "UserTasksProcess");
        
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("FirstTask", workItems.get(0).getName());
        
        uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        uow.end();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        events = publisher.extract();
        assertThat(events).isNotNull().hasSize(3);
        body = assertProcessInstanceEvent(events.get(0), "UserTasksProcess", "UserTasksProcess", 1);
        assertThat(body.getNodeInstances()).hasSize(2).extractingResultOf("getNodeType").contains("HumanTaskNode", "HumanTaskNode");
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").containsNull();// human task is active thus null for leave time
        
        assertUserTaskInstanceEvent(events.get(1), "Second Task", null, "1", "Ready", "UserTasksProcess");
        assertUserTaskInstanceEvent(events.get(2), "First Task", null, "1", "Completed", "UserTasksProcess");
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("SecondTask", workItems.get(0).getName());
        
        uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        uow.end();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        events = publisher.extract();
        assertThat(events).isNotNull().hasSize(2);
        body = assertProcessInstanceEvent(events.get(0), "UserTasksProcess", "UserTasksProcess", 2);
        assertThat(body.getNodeInstances()).hasSize(2).extractingResultOf("getNodeType").contains("HumanTaskNode", "EndNode");
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);
        
        assertUserTaskInstanceEvent(events.get(1), "Second Task", null, "1", "Completed", "UserTasksProcess");
    }
    
    @Test
    public void testBasicUserTaskProcessWithSecurityRoles() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcessWithSecurityRoles.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        uow.end();     
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);        
        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(2);
        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "UserTasksProcess", "UserTasksProcess", 1);
        assertThat(body.getRoles()).hasSize(2).contains("employees", "managers");
        assertThat(body.getNodeInstances()).hasSize(2).extractingResultOf("getNodeType").contains("StartNode", "HumanTaskNode");
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").containsNull();// human task is active thus null for leave time
        
        assertUserTaskInstanceEvent(events.get(1), "First Task", null, "1", "Ready", "UserTasksProcess");
    }
    
    @Test
    public void testBasicCallActivityTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("subprocess/CallActivity.bpmn2", "subprocess/CallActivitySubProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ParentProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("x", "a");
        parameters.put("y", "b");
        m.fromMap(parameters);
        
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        uow.end();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("x", "y");
        assertThat(result.toMap().get("y")).isNotNull().isEqualTo("new value");
        assertThat(result.toMap().get("x")).isNotNull().isEqualTo("a");
        
        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(2);
        
        DataEvent<?> parent = null;
        DataEvent<?> child = null;
        
        for (DataEvent<?> e : events) {
            ProcessInstanceDataEvent processDataEvent = (ProcessInstanceDataEvent) e;
            if (processDataEvent.getKogitoProcessId().equals("ParentProcess")) {
                parent = e;
                assertThat(processDataEvent.getKogitoProcessinstanceId()).isNotNull(); 
                assertThat(processDataEvent.getKogitoParentProcessinstanceId()).isNull(); 
                assertThat(processDataEvent.getKogitoRootProcessinstanceId()).isNull();
                assertThat(processDataEvent.getKogitoRootProcessId()).isNull();
                assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("ParentProcess");
                assertThat(processDataEvent.getKogitoProcessinstanceState()).isEqualTo("2");
            } else {
                child = e;
                assertThat(processDataEvent.getKogitoProcessinstanceId()).isNotNull(); 
                assertThat(processDataEvent.getKogitoParentProcessinstanceId()).isNotNull(); 
                assertThat(processDataEvent.getKogitoRootProcessinstanceId()).isNotNull();
                assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("SubProcess");
                assertThat(processDataEvent.getKogitoRootProcessId()).isEqualTo("ParentProcess");
                assertThat(processDataEvent.getKogitoProcessinstanceState()).isEqualTo("2");
            }        
        }
        ProcessInstanceEventBody parentBody = assertProcessInstanceEvent(parent, "ParentProcess", "Parent Process", 2);
        assertThat(parentBody.getNodeInstances()).hasSize(3).extractingResultOf("getNodeType").contains("StartNode", "SubProcessNode", "EndNode");
        assertThat(parentBody.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(parentBody.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);
        
        ProcessInstanceEventBody childBody = assertProcessInstanceEventWithParentId(child, "SubProcess", "Sub Process", 2);
        assertThat(childBody.getNodeInstances()).hasSize(3).extractingResultOf("getNodeType").contains("StartNode", "ActionNode", "EndNode");
        assertThat(childBody.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(childBody.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);
    }
    
    @Test
    public void testBusinessRuleProcessStartToEndAbortOfUoW() throws Exception {
        
        Application app = generateCode(Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"), Collections.singletonList("ruletask/BusinessRuleTask.drl"));        
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        Process<? extends Model> p = app.processes().processById("BusinessRuleTask");
        
        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);        
        uow.abort();

        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(0);
    }
    
    @Test
    public void testExclusiveGatewayStartToEnd() throws Exception {
        
        Application app = generateCodeProcessesOnly("gateway/ExclusiveSplit.bpmn2");        
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        
        Process<? extends Model> p = app.processes().processById("ExclusiveSplit");
        
        Map<String, Object> params = new HashMap<>();
        params.put("x", "First");
        params.put("y", "None");
        Model m = p.createModel();
        m.fromMap(params);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("x", "y");              
        uow.end();

        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(1);
        
        DataEvent<?> event = events.get(0);
        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent processDataEvent = (ProcessInstanceDataEvent) event;
        assertThat(processDataEvent.getKogitoProcessinstanceId()).isNotNull(); 
        assertThat(processDataEvent.getKogitoParentProcessinstanceId()).isNull(); 
        assertThat(processDataEvent.getKogitoRootProcessinstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("ExclusiveSplit");
        assertThat(processDataEvent.getKogitoProcessinstanceState()).isEqualTo("2");
        
        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "ExclusiveSplit", "Test", 2);
        
        assertThat(body.getNodeInstances()).hasSize(6).extractingResultOf("getNodeType").contains("StartNode", "ActionNode", "Split", "Join", "EndNode", "WorkItemNode");
        
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testServiceTaskProcessWithError() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessDifferentOperations.bpmn2");        
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
                
        Process<? extends Model> p = app.processes().processById("ServiceProcessDifferentOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance processInstance = p.createInstance(m);
        processInstance.start();
        
        uow.end();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ERROR); 
        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(1);
        
        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "ServiceProcessDifferentOperations", "Service Process", 5);
        assertThat(body.getNodeInstances()).hasSize(2).extractingResultOf("getNodeType").contains("StartNode", "WorkItemNode");
        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").containsNull();// human task is active thus null for leave time
        
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getErrorMessage()).contains("java.lang.NullPointerException - null");
        assertThat(body.getError().getNodeDefinitionId()).isEqualTo("_38E04E27-3CCA-47F9-927B-E37DC4B8CE25");
        
        parameters.put("s", "john");
        m.fromMap(parameters);
        uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        processInstance.updateVariables(m);
        uow.end();
        
        events = publisher.extract();
        assertThat(events).isNotNull().hasSize(1);
        body = assertProcessInstanceEvent(events.get(0), "ServiceProcessDifferentOperations", "Service Process", 5);
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getErrorMessage()).contains("java.lang.NullPointerException - null");
        assertThat(body.getError().getNodeDefinitionId()).isEqualTo("_38E04E27-3CCA-47F9-927B-E37DC4B8CE25");
        
        uow = app.unitOfWorkManager().newUnitOfWork();                        
        uow.start();
        if (processInstance.error().isPresent()) {
            ((ProcessError)processInstance.error().get()).retrigger();
        }
        uow.end();
        
        events = publisher.extract();
        assertThat(events).isNotNull().hasSize(1);
        
        body = assertProcessInstanceEvent(events.get(0), "ServiceProcessDifferentOperations", "Service Process", 2);
        assertThat(body.getError()).isNull();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Goodbye Hello john!!");
    }
    
    /*
     * Helper methods
     */
    
    protected ProcessInstanceEventBody assertProcessInstanceEvent(DataEvent<?> event, String processId, String processName, Integer state) {
        
        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceEventBody body = ((ProcessInstanceDataEvent)event).getData();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getStartDate()).isNotNull();
        if (state == ProcessInstance.STATE_ACTIVE || state == ProcessInstance.STATE_ERROR) {
            assertThat(body.getEndDate()).isNull();
        } else {
            assertThat(body.getEndDate()).isNotNull();
        }
        assertThat(body.getParentInstanceId()).isNull();
        assertThat(body.getRootInstanceId()).isNull();
        assertThat(body.getProcessId()).isEqualTo(processId);
        assertThat(body.getProcessName()).isEqualTo(processName);
        assertThat(body.getState()).isEqualTo(state);
        
        assertThat(event.getSource()).isEqualTo("http://myhost/" + processId);
        assertThat(event.getTime()).doesNotContain("[");
        
        return body;
    }
    
    protected UserTaskInstanceEventBody assertUserTaskInstanceEvent(DataEvent<?> event, String taskName, String taskDescription, String taskPriority, String taskState, String processId) {
        assertThat(event).isInstanceOf(UserTaskInstanceDataEvent.class);
        UserTaskInstanceEventBody body = ((UserTaskInstanceDataEvent)event).getData();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getTaskName()).isEqualTo(taskName);
        assertThat(body.getTaskDescription()).isEqualTo(taskDescription);
        assertThat(body.getTaskPriority()).isEqualTo(taskPriority);
        assertThat(body.getStartDate()).isNotNull();
        assertThat(body.getState()).isEqualTo(taskState);
        if (taskState.equals("Completed")) {
            assertThat(body.getCompleteDate()).isNotNull();
        } else {
            assertThat(body.getCompleteDate()).isNull();
        }
        
        assertThat(event.getSource()).isEqualTo("http://myhost/" + processId);
        assertThat(event.getTime()).doesNotContain("[");
        
        return body;
    }
    
    protected ProcessInstanceEventBody assertProcessInstanceEventWithParentId(DataEvent<?> event, String processId, String processName, Integer state) {
        
        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceEventBody body = ((ProcessInstanceDataEvent)event).getData();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getStartDate()).isNotNull();
        if (state == ProcessInstance.STATE_ACTIVE) {
            assertThat(body.getEndDate()).isNull();
        } else {
            assertThat(body.getEndDate()).isNotNull();
        }
        assertThat(body.getParentInstanceId()).isNotNull();
        assertThat(body.getRootInstanceId()).isNotNull();
        assertThat(body.getProcessId()).isEqualTo(processId);
        assertThat(body.getProcessName()).isEqualTo(processName);
        assertThat(body.getState()).isEqualTo(state);
        
        return body;
    }
    
    private class TestEventPublisher implements EventPublisher {

        private List<DataEvent<?>> events = new ArrayList<>();
        
        @Override
        public void publish(DataEvent<?> event) {
            this.events.add(event);
        }

        @Override
        public void publish(Collection<DataEvent<?>> events) {
            this.events.addAll(events);
        }
        
        public List<DataEvent<?>> extract() {
            List<DataEvent<?>> copied = new ArrayList<>(this.events);
            this.events.clear();
            return copied;
        }
    }
}
