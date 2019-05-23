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

package org.kie.submarine.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.submarine.Application;
import org.kie.submarine.Model;
import org.kie.submarine.codegen.AbstractCodegenTest;
import org.kie.submarine.codegen.data.Person;
import org.kie.submarine.process.Process;
import org.kie.submarine.process.ProcessInstance;


public class BusinessRuleTaskTest extends AbstractCodegenTest {

    
    @Test
    public void testBasicBusinessRuleTask() throws Exception {
        
        Application app = generateCode(Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"), Collections.singletonList("ruletask/BusinessRuleTask.drl"));        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("BusinessRuleTask");
        
        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
    }
    
    @Test
    public void testBasicBusinessRuleTaskWithAgendaListener() throws Exception {
        
        Application app = generateCode(Collections.singletonList("ruletask/BusinessRuleTask.bpmn2"), Collections.singletonList("ruletask/BusinessRuleTask.drl"));        
        assertThat(app).isNotNull();
        final AtomicInteger counter = new AtomicInteger();
        ((DefaultRuleEventListenerConfig)app.config().rule().ruleEventListeners()).register(new DefaultAgendaEventListener() {

            @Override
            public void afterMatchFired(AfterMatchFiredEvent event) {
                counter.incrementAndGet();
            }
            
        });                
        Process<? extends Model> p = app.processes().processById("BusinessRuleTask");
        
        Model m = p.createModel();
        m.fromMap(Collections.singletonMap("person", new Person("john", 25)));
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED);
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKey("person");
        assertThat(result.toMap().get("person")).isNotNull().hasFieldOrPropertyWithValue("adult", true);
        
        assertThat(counter.get()).isEqualTo(1);
    }
}
