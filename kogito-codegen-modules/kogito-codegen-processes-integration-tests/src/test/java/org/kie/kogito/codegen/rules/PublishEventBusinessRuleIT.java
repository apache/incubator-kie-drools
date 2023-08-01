/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.rules;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceEventBody;
import org.kie.kogito.event.process.VariableInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceEventBody;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWork;

import static org.assertj.core.api.Assertions.assertThat;

public class PublishEventBusinessRuleIT extends AbstractRulesCodegenIT {

    @Test
    public void testBusinessRuleProcessStartToEnd() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        uow.end();

        ProcessInstanceDataEvent processDataEvent = publisher.extract().stream().filter(ProcessInstanceDataEvent.class::isInstance).map(ProcessInstanceDataEvent.class::cast).findFirst().orElseThrow();
        assertThat(processDataEvent.getKogitoProcessInstanceId()).isNotNull();
        assertThat(processDataEvent.getKogitoParentProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(processDataEvent.getKogitoProcessInstanceState()).isEqualTo("2");
        assertThat(processDataEvent.getSource()).hasToString("http://myhost/BusinessRuleTask");

        ProcessInstanceEventBody body = assertProcessInstanceEvent(processDataEvent, "BusinessRuleTask", "Default Process", 2);

        assertThat(body.getNodeInstances()).hasSize(3).extractingResultOf("getNodeType").contains("StartNode", "RuleSetNode", "EndNode");

        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);

        assertThat(body.getVariables()).hasSize(1).containsKey("person");
        assertThat(body.getVariables().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
    }

    @Test
    public void testBusinessRuleProcessStartToEndAbortOfUoW() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        uow.abort();

        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().isEmpty();
    }

    @Test
    public void testBusinessRuleProcessStartToEndWithVariableTracked() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTaskVariableTags.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        TestEventPublisher publisher = new TestEventPublisher();
        app.unitOfWorkManager().eventManager().setService("http://myhost");
        app.unitOfWorkManager().eventManager().addPublisher(publisher);

        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        uow.end();

        List<DataEvent<?>> events = publisher.extract();
        assertThat(events).isNotNull().hasSize(2);

        DataEvent<?> event = events.get(0);
        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceDataEvent processDataEvent = (ProcessInstanceDataEvent) event;
        assertThat(processDataEvent.getKogitoProcessInstanceId()).isNotNull();
        assertThat(processDataEvent.getKogitoParentProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(processDataEvent.getKogitoProcessInstanceState()).isEqualTo("2");
        assertThat(processDataEvent.getSource()).hasToString("http://myhost/BusinessRuleTask");

        ProcessInstanceEventBody body = assertProcessInstanceEvent(events.get(0), "BusinessRuleTask", "Default Process", 2);

        assertThat(body.getNodeInstances()).hasSize(3).extractingResultOf("getNodeType").contains("StartNode", "RuleSetNode", "EndNode");

        assertThat(body.getNodeInstances()).extractingResultOf("getTriggerTime").allMatch(v -> v != null);
        assertThat(body.getNodeInstances()).extractingResultOf("getLeaveTime").allMatch(v -> v != null);

        assertThat(body.getVariables()).hasSize(1).containsKey("person");
        assertThat(body.getVariables().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);

        event = events.get(1);
        assertThat(event).isInstanceOf(VariableInstanceDataEvent.class);

        VariableInstanceDataEvent variableDataEvent = (VariableInstanceDataEvent) event;
        assertThat(variableDataEvent.getKogitoProcessInstanceId()).isNotNull();
        assertThat(variableDataEvent.getKogitoRootProcessId()).isNull();
        assertThat(variableDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(variableDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        // first is event created based on process start so no node associated
        VariableInstanceEventBody variableEventBody = variableDataEvent.getData();
        assertThat(variableEventBody).isNotNull();
        assertThat(variableEventBody.getChangeDate()).isNotNull();
        assertThat(variableEventBody.getProcessInstanceId()).isEqualTo(variableDataEvent.getKogitoProcessInstanceId());
        assertThat(variableEventBody.getProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(variableEventBody.getRootProcessId()).isNull();
        assertThat(variableEventBody.getRootProcessInstanceId()).isNull();
        assertThat(variableEventBody.getVariableName()).isEqualTo("person");
        assertThat(variableEventBody.getVariableValue()).isNotNull();
        assertThat(variableEventBody.getVariablePreviousValue()).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        assertThat(variableEventBody.getChangedByNodeId()).isEqualTo("BusinessRuleTask_2");
        assertThat(variableEventBody.getChangedByNodeName()).isEqualTo("Business Rule Task");
        assertThat(variableEventBody.getChangedByNodeType()).isEqualTo("RuleSetNode");
        assertThat(variableEventBody.getIdentity()).isNull();
    }

    /*
     * Helper methods
     */

    protected ProcessInstanceEventBody assertProcessInstanceEvent(DataEvent<?> event, String processId, String processName, Integer state) {

        assertThat(event).isInstanceOf(ProcessInstanceDataEvent.class);
        ProcessInstanceEventBody body = ((ProcessInstanceDataEvent) event).getData();
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

        assertThat(event.getSource()).hasToString("http://myhost/" + processId);
        assertThat(event.getTime()).isBeforeOrEqualTo(ZonedDateTime.now().toOffsetDateTime());

        assertThat(((ProcessInstanceDataEvent) event).getKogitoAddons()).isEqualTo("test");

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
