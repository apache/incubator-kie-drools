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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.codegen.data.Account;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Processes;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.uow.UnitOfWork;

import static org.assertj.core.api.Assertions.assertThat;


public class BusinessRuleTaskTest extends AbstractCodegenTest {

    @Test
    public void testBasicBusinessRuleTask() throws Exception {
        Map<AbstractCodegenTest.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
    }

    @Test
    public void testBasicBusinessRuleTaskWithAgendaListener() throws Exception {
        Map<AbstractCodegenTest.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        final AtomicInteger counter = new AtomicInteger();
        app.config().get(RuleConfig.class).ruleEventListeners().agendaListeners().add(new DefaultAgendaEventListener() {

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                counter.incrementAndGet();
            }

        });
        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

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

    @Test
    public void testBasicBusinessRuleTaskControlledByUnitOfWork() throws Exception {
        Map<AbstractCodegenTest.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
        final List<String> startedProcesses = new ArrayList<>();
        // add custom event listener that collects data
        app.config().get(ProcessConfig.class).processEventListeners().listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                startedProcesses.add(event.getProcessInstance().getId());
            }

        });
        UnitOfWork uow = app.unitOfWorkManager().newUnitOfWork();
        uow.start();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

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
    public void testDecision() throws Exception {
        Map<AbstractCodegenTest.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("decision/models/dmnprocess.bpmn2"));
        resourcesTypeMap.put(TYPE.DECISION, Collections.singletonList("decision/models/vacationDaysAlt/vacationDaysAlt.dmn"));
        Application app = generateCode(resourcesTypeMap);
        Process<? extends Model> p =
                app.get(Processes.class)
                        .processById("DmnProcess");

        // first run 16, 1 and expected days is 27
        {
            Model m = p.createModel();
            HashMap<String, Object> vars = new HashMap<>();
            vars.put("age", 16);
            vars.put("yearsOfService", 1);
            m.fromMap(vars);

            ProcessInstance<? extends Model> processInstance = p.createInstance(m);
            processInstance.start();

            assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
            Model result = processInstance.variables();

            assertThat(result.toMap().get("vacationDays"))
                    .isNotNull()
                    .isEqualTo(BigDecimal.valueOf(27));
        }

        // second run 44, 20 and expected days is 24
        {
            Model m = p.createModel();
            HashMap<String, Object> vars = new HashMap<>();
            vars.put("age", 44);
            vars.put("yearsOfService", 20);
            m.fromMap(vars);

            ProcessInstance<? extends Model> processInstance = p.createInstance(m);
            processInstance.start();

            assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
            Model result = processInstance.variables();

            assertThat(result.toMap().get("vacationDays"))
                    .isNotNull()
                    .isEqualTo(BigDecimal.valueOf(24));
        }

        // second run 50, 30 and expected days is 30
        {
            Model m = p.createModel();
            HashMap<String, Object> vars = new HashMap<>();
            vars.put("age", 50);
            vars.put("yearsOfService", 30);
            m.fromMap(vars);

            ProcessInstance<? extends Model> processInstance = p.createInstance(m);
            processInstance.start();

            assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
            Model result = processInstance.variables();

            assertThat(result.toMap().get("vacationDays"))
                    .isNotNull()
                    .isEqualTo(BigDecimal.valueOf(30));
        }
    }
    
    @Test
    public void testBusinessRuleTaskWithIOExpression() throws Exception {
        Map<AbstractCodegenTest.TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.PROCESS, Collections.singletonList("ruletask/BusinessRuleTaskWithIOExpression.bpmn2"));
        resourcesTypeMap.put(TYPE.RULES, Collections.singletonList("ruletask/BusinessRuleTask.drl"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();

        Process<? extends Model> p = app.get(Processes.class).processById("BusinessRuleTask");

        Model m = p.createModel();
        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person("john", 25));
        params.put("account", new Account());
        m.fromMap(params);

        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(2).containsKeys("person", "account");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        assertThat(result.toMap().get("account")).isNotNull();
        assertThat(((Account)result.toMap().get("account")).getPerson()).isNotNull();
    }
}
