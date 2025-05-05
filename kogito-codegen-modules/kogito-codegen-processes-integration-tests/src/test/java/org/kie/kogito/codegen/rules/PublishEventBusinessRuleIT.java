/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.rules;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceNodeDataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWork;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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

        List<DataEvent<?>> events = publisher.extract();
        List<ProcessInstanceStateDataEvent> processInstanceStateEvents = events.stream()
                .filter(ProcessInstanceStateDataEvent.class::isInstance)
                .map(ProcessInstanceStateDataEvent.class::cast)
                .toList();

        assertThat(processInstanceStateEvents).hasSize(2);
        assertThat(processInstanceStateEvents)
                .extracting(ProcessInstanceStateDataEvent::getData)
                .extracting(ProcessInstanceStateEventBody::getState)
                .containsExactlyInAnyOrder(KogitoProcessInstance.STATE_ACTIVE, KogitoProcessInstance.STATE_COMPLETED);

        ProcessInstanceStateDataEvent processDataEvent =
                processInstanceStateEvents.stream().filter(e -> e.getData().getState() == KogitoProcessInstance.STATE_COMPLETED).findFirst().orElseThrow();
        assertThat(processDataEvent.getKogitoParentProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(processDataEvent.getKogitoProcessInstanceState()).isEqualTo("2");
        assertThat(processDataEvent.getSource()).hasToString("http://myhost/BusinessRuleTask");

        assertProcessInstanceEvent(processDataEvent, "BusinessRuleTask", "Default Process", KogitoProcessInstance.STATE_COMPLETED);

        List<ProcessInstanceNodeDataEvent> nodeEvents = events.stream().filter(ProcessInstanceNodeDataEvent.class::isInstance).map(ProcessInstanceNodeDataEvent.class::cast).toList();
        assertThat(nodeEvents).hasSize(6).map(e -> (ProcessInstanceNodeEventBody) e.getData()).extractingResultOf("getNodeType").containsOnly("StartNode", "RuleSetNode", "EndNode");

        List<ProcessInstanceVariableDataEvent> variableEvents =
                events.stream().filter(ProcessInstanceVariableDataEvent.class::isInstance).map(ProcessInstanceVariableDataEvent.class::cast).collect(Collectors.toList());

        assertThat(variableEvents)
                .extracting(e -> e.getData().getVariableName(), e -> ((Person) e.getData().getVariableValue()).isAdult())
                .containsExactlyInAnyOrder(tuple("person", true), tuple("person", true));
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
        assertThat(events).isNotNull().hasSize(10);

        DataEvent<?> event = events.get(9);
        assertThat(event).isInstanceOf(ProcessInstanceStateDataEvent.class);
        ProcessInstanceStateDataEvent processDataEvent = (ProcessInstanceStateDataEvent) event;
        assertThat(processDataEvent.getKogitoProcessInstanceId()).isNotNull();
        assertThat(processDataEvent.getKogitoParentProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(processDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(processDataEvent.getKogitoProcessInstanceState()).isEqualTo("2");
        assertThat(processDataEvent.getSource()).hasToString("http://myhost/BusinessRuleTask");

        List<DataEvent<?>> nodeEvents = events.stream().filter(ProcessInstanceNodeDataEvent.class::isInstance).map(e -> (ProcessInstanceNodeDataEvent) e).filter(e -> e.getData().getEventType() == 1)
                .collect(Collectors.toList());
        assertThat(nodeEvents).hasSize(3).map(e -> (ProcessInstanceNodeEventBody) e.getData()).extractingResultOf("getNodeType").containsOnly("StartNode", "RuleSetNode", "EndNode");

        assertProcessInstanceEvent(event, "BusinessRuleTask", "Default Process", 2);

        List<DataEvent<?>> varaibleEvents = events.stream().filter(ProcessInstanceVariableDataEvent.class::isInstance).map(e -> (ProcessInstanceVariableDataEvent) e).collect(Collectors.toList());
        event = varaibleEvents.get(0);
        assertThat(event).isInstanceOf(ProcessInstanceVariableDataEvent.class);

        ProcessInstanceVariableDataEvent variableDataEvent = (ProcessInstanceVariableDataEvent) event;
        assertThat(variableDataEvent.getKogitoProcessInstanceId()).isNotNull();
        assertThat(variableDataEvent.getKogitoRootProcessId()).isNull();
        assertThat(variableDataEvent.getKogitoRootProcessInstanceId()).isNull();
        assertThat(variableDataEvent.getKogitoProcessId()).isEqualTo("BusinessRuleTask");
        // first is event created based on process start so no node associated
        ProcessInstanceVariableEventBody variableEventBody = variableDataEvent.getData();
        assertThat(variableEventBody).isNotNull();
        assertThat(variableEventBody.getEventDate()).isNotNull();
        assertThat(variableEventBody.getProcessInstanceId()).isEqualTo(variableDataEvent.getKogitoProcessInstanceId());
        assertThat(variableEventBody.getProcessId()).isEqualTo("BusinessRuleTask");
        assertThat(variableEventBody.getVariableName()).isEqualTo("person");
        assertThat(variableEventBody.getVariableValue()).isNotNull();
    }

    /*
     * Helper methods
     */

    protected ProcessInstanceStateEventBody assertProcessInstanceEvent(DataEvent<?> event, String processId, String processName, Integer state) {

        assertThat(event).isInstanceOf(ProcessInstanceStateDataEvent.class);
        ProcessInstanceStateEventBody body = ((ProcessInstanceStateDataEvent) event).getData();
        assertThat(body).isNotNull();
        assertThat(body.getProcessInstanceId()).isNotNull();
        assertThat(body.getEventDate()).isNotNull();
        assertThat(body.getParentInstanceId()).isNull();
        assertThat(body.getRootProcessInstanceId()).isNull();
        assertThat(body.getProcessId()).isEqualTo(processId);
        assertThat(body.getProcessName()).isEqualTo(processName);
        assertThat(body.getState()).isEqualTo(state);

        assertThat(event.getSource()).hasToString("http://myhost/" + processId);
        assertThat(event.getTime()).isBeforeOrEqualTo(ZonedDateTime.now().toOffsetDateTime());

        assertThat(((ProcessInstanceStateDataEvent) event).getKogitoAddons()).isEqualTo("test");

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
