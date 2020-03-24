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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.rules.units.UndefinedGeneratedRuleUnitVariable;
import org.kie.kogito.uow.UnitOfWork;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BusinessRuleUnitTest extends AbstractCodegenTest {

    static Stream<String> processes() {
        return Stream.of(
                "org/kie/kogito/codegen/tests/BusinessRuleUnit.bpmn2",
                "org/kie/kogito/codegen/tests/BusinessRuleUnitAlternateSyntax.bpmn2");
    }


    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnit(String bpmnPath) throws Exception {

        Application app = generateCode(Collections.singletonList(bpmnPath), Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.processes().processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
    }

    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnitWithAgendaListener(String bpmnPath) throws Exception {

        Application app = generateCode(Collections.singletonList(bpmnPath), Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        assertThat(app).isNotNull();
        final AtomicInteger counter = new AtomicInteger();
        ((DefaultRuleEventListenerConfig)app.config().rule().ruleEventListeners()).register(new DefaultAgendaEventListener() {

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                counter.incrementAndGet();
            }

        });
        Process<? extends Model> p = app.processes().processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);

        assertThat(counter.get()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("processes")
    public void testBasicBusinessRuleUnitControlledByUnitOfWork(String bpmnPath) throws Exception {

        Application app = generateCode(Collections.singletonList(bpmnPath), Collections.singletonList("org/kie/kogito/codegen/tests/BusinessRuleUnit.drl"));
        assertThat(app).isNotNull();
        final List<String> startedProcesses = new ArrayList<>();
        // add custom event listener that collects data
        ((DefaultProcessEventListenerConfig)app.config().process().processEventListeners()).listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                startedProcesses.add(event.getProcessInstance().getId());
            }

        });
        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.processes().processById("BusinessRuleUnit");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);

        // since the unit of work has not been finished yet not listeners where invoked
        assertThat(startedProcesses).hasSize(0);
        uow.end();
        // after unit of work has been ended listeners are invoked
        assertThat(startedProcesses).hasSize(1);
    }

    @Test
    public void ioMapping() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleP.bpmn"),
                                       Collections.singletonList("ruletask/Example.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleP");

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

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));
        assertThat((Collection) result.get("emptyList")).isEmpty();

        instance.start();

        result = instance.variables().toMap();
        assertEquals("hello", result.get("emptyString"));

        Person yoko = new Person("Yoko", 86);
        yoko.setAdult(true);
        assertEquals(yoko, result.get("emptyPerson"));

        Person paul = new Person("Paul", 77);
        paul.setAdult(true);
        Person ringo = new Person("Ringo", 79);
        ringo.setAdult(true);
        assertEquals(asList(paul, ringo), result.get("emptyList"));

    }


    @Test
    public void ioMappingAutoGeneratedRuleUnit() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleGenerated.bpmn"),
                                       Collections.singletonList("ruletask/Generated.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleGenerated");

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

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));

        instance.start();

        result = instance.variables().toMap();

        Person yoko = new Person("Yoko", 86);
        yoko.setAdult(true);
        assertEquals(yoko, result.get("singlePerson"));
        

    }
    
    @Test
    public void testSettingOtherVariableFromAutoGeneratedRuleUnit() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleGenerated.bpmn"),
                                       Collections.singletonList("ruletask/Generated.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleGenerated");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("John", 50));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));

        instance.start();

        result = instance.variables().toMap();

        Person john = new Person("John", 50);
        john.setAdult(true);
        assertEquals(john, result.get("singlePerson"));
        assertEquals("Now the life starts again", result.get("singleString"));

    }
    
    @Test
    public void testRemovingOtherVariableFromAutoGeneratedRuleUnit() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleGenerated.bpmn"),
                                       Collections.singletonList("ruletask/Generated.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleGenerated");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("John", 60));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));

        instance.start();

        result = instance.variables().toMap();

        Person john = new Person("John", 60);
        john.setAdult(true);
        assertEquals(john, result.get("singlePerson"));
        assertNull(result.get("singleString"));

    }


    @Test
    public void wrongVariableNameInGeneratedRuleUnit() {
        assertThatExceptionOfType(ProcessCodegenException.class).isThrownBy(() -> {
            Application app = generateCode(Collections.singletonList("ruletask/ExampleGeneratedWrong.bpmn"),
                                           Collections.singletonList("ruletask/Generated.drl"));
        }).withCauseInstanceOf(UndefinedGeneratedRuleUnitVariable.class);
    }

    @Test
    @DisplayName("Should throw an exception when a null collection variable is mapped as input of a datasource")
    public void inputMappingNullCollection() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleP.bpmn"),
                                       Collections.singletonList("ruletask/Example.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleP");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", null);

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));
        assertNull(result.get("emptyList"));

        instance.start();

        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_ERROR);
        assertThat(instance.error().get().errorMessage()).contains("The input collection variable of a data source cannot be null");
    }

    @Test
    @DisplayName("Should throw an exception when a null collection variable is mapped as output of a datasource")
    public void outputMappingNullCollection() throws Exception {
        Application app = generateCode(Collections.singletonList("ruletask/ExampleP.bpmn"),
                                       Collections.singletonList("ruletask/Example.drl"));
        Process<? extends Model> process = app.processes().processById("ruletask.ExampleP");

        HashMap<String, Object> map = new HashMap<>();
        map.put("singleString", "hello");
        map.put("singlePerson", new Person("Yoko", 86));
        map.put("manyPersons", asList(new Person("Paul", 77), new Person("Ringo", 79)));

        Model model = process.createModel();
        model.fromMap(map);
        ProcessInstance<? extends Model> instance = process.createInstance(model);
        Model variables = instance.variables();
        Map<String, Object> result = variables.toMap();

        assertNull(result.get("emptyString"));
        assertNull(result.get("emptyPerson"));
        assertNull(result.get("emptyList"));

        instance.start();

        assertThat(instance.status()).isEqualTo(ProcessInstance.STATE_ERROR);
        assertThat(instance.error().get().errorMessage()).contains("Null collection variable used as an output variable");
    }

    @Test
    public void malformedShouldThrowException() {
        assertThrows(ProcessCodegenException.class, () -> {
            generateCodeProcessesOnly("ruletask/BusinessRuleTaskMalformed.bpmn2");
        });
    }
}
