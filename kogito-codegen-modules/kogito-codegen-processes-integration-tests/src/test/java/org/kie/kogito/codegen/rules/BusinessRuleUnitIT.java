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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.ruleunits.impl.UndefinedGeneratedRuleUnitVariableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.uow.UnitOfWork;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class BusinessRuleUnitIT extends AbstractRulesCodegenIT {

    static Stream<String> processes() {
        return Stream.of(
                "org/kie/kogito/codegen/tests/BusinessRuleUnit.bpmn2",
                "org/kie/kogito/codegen/tests/BusinessRuleUnitAlternateSyntax.bpmn2");
    }

    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnit(String bpmnPath) throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList(bpmnPath));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
    }

    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnitWithAgendaListener(String bpmnPath) throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList(bpmnPath));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        final AtomicInteger counter = new AtomicInteger();
        app.config().get(RuleConfig.class).ruleEventListeners().agendaListeners().add(new DefaultAgendaEventListener() {

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                counter.incrementAndGet();
            }

        });
        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);

        assertThat(counter.get()).isOne();
    }

    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnitControlledByUnitOfWork(String bpmnPath) throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList(bpmnPath));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        final List<String> startedProcesses = new ArrayList<>();
        // add custom event listener that collects data
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                startedProcesses.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }

        });
        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        uow.end();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);

        assertThat(startedProcesses).hasSize(1);
    }

    @Test
    public void ioMapping() throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleP.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Example.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleP");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", asList(new Person("Paul", 77), new Person("Ringo", 79)));
        map.put("emptyList", new ArrayList<>());

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();
        assertThat((Collection) result.get("emptyList")).isEmpty();

        instance.start();

        result = instance.variables().toMap();
        assertThat(result).containsEntry("emptyString", "hello");

        Person yoko = new Person("Yoko", 86);
        yoko.setAdult(true);
        assertThat(result).containsEntry("emptyPerson", yoko);

        Person paul = new Person("Paul", 77);
        paul.setAdult(true);
        Person ringo = new Person("Ringo", 79);
        ringo.setAdult(true);
        assertThat(result).containsEntry("emptyList", asList(paul, ringo));

    }

    @Test
    public void ioMappingAutoGeneratedRuleUnit() throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleGenerated.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Generated.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleGenerated");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", asList(new Person("Paul", 77), new Person("Ringo", 79)));
        map.put("emptyList", new ArrayList<>());

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();

        instance.start();

        result = instance.variables().toMap();

        Person yoko = new Person("Yoko", 86);
        yoko.setAdult(true);
        assertThat(result).containsEntry("singlePerson", yoko);

    }

    @Test
    public void testSettingOtherVariableFromAutoGeneratedRuleUnit() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleGenerated.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Generated.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleGenerated");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("John", 50));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();

        instance.start();

        result = instance.variables().toMap();

        Person john = new Person("John", 50);
        john.setAdult(true);
        assertThat(result).containsEntry("singlePerson", john)
                .containsEntry("singleString", "Now the life starts again");
    }

    @Test
    public void testRemovingOtherVariableFromAutoGeneratedRuleUnit() throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleGenerated.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Generated.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleGenerated");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("John", 60));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();

        instance.start();

        result = instance.variables().toMap();

        Person john = new Person("John", 60);
        john.setAdult(true);
        assertThat(result).containsEntry("singlePerson", john);
        assertThat(result.get("singleString")).isNull();

    }

    @Test
    public void wrongVariableNameInGeneratedRuleUnit() {
        assertThatExceptionOfType(ProcessCodegenException.class).isThrownBy(() -> {
            Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
            resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleGeneratedWrong.bpmn"));
            resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Generated.drl"));
            Application app = generateCode(resourcesTypeMap);
        }).withCauseInstanceOf(UndefinedGeneratedRuleUnitVariableException.class);
    }

    @Test
    @DisplayName("Should throw an exception when a null collection variable is mapped as input of a datasource")
    public void inputMappingNullCollection() throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleP.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Example.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleP");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", null);

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();
        assertThat(result.get("emptyList")).isNull();

        instance.start();

        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_ERROR);
        assertThat(instance.error().get().errorMessage()).contains("The input collection variable of a data source cannot be null");
    }

    @Test
    @DisplayName("Should throw an exception when a null collection variable is mapped as output of a datasource")
    public void outputMappingNullCollection() throws Exception {
        Map<AbstractCodegenIT.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/ExampleP.bpmn"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/Example.drl"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> process = app.get(Processes.class).processById("ruletask.ExampleP");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", asList(new Person("Paul", 77), new Person("Ringo", 79)));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertThat(result.get("emptyString")).isNull();
        assertThat(result.get("emptyPerson")).isNull();
        assertThat(result.get("emptyList")).isNull();

        instance.start();

        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_ERROR);
        assertThat(instance.error().get().errorMessage()).contains("Null collection variable used as an output variable");
    }

    @Test
    public void malformedShouldThrowException() {
        assertThatExceptionOfType(ProcessCodegenException.class).isThrownBy(() -> {
            generateCodeProcessesOnly("ruletask/BusinessRuleTaskMalformed.bpmn2");
        });
    }
}
