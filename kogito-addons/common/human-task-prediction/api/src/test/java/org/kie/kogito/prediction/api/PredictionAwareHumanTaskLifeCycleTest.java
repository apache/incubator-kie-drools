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
package org.kie.kogito.prediction.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.internal.process.workitem.Policy;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.impl.CachedWorkItemHandlerConfig;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.impl.StaticProcessConfig;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;

public class PredictionAwareHumanTaskLifeCycleTest {

    private Policy securityPolicy = SecurityPolicy.of(new StaticIdentityProvider("john"));

    private AtomicBoolean predictNow;
    private List<String> trainedTasks;

    private PredictionService predictionService;

    private ProcessConfig config;

    @BeforeEach
    public void configure() {

        predictNow = new AtomicBoolean(false);
        trainedTasks = new ArrayList<>();

        predictionService = new PredictionService() {

            @Override
            public void train(org.kie.api.runtime.process.WorkItem task, Map<String, Object> inputData, Map<String, Object> outputData) {
                trainedTasks.add(((InternalKogitoWorkItem) task).getStringId());
            }

            @Override
            public PredictionOutcome predict(org.kie.api.runtime.process.WorkItem task, Map<String, Object> inputData) {
                if (predictNow.get()) {
                    return new PredictionOutcome(95, 75, Collections.singletonMap("output", "predicted value"));
                }

                return new PredictionOutcome();
            }

            @Override
            public String getIdentifier() {
                return "test";
            }
        };

        CachedWorkItemHandlerConfig wiConfig = new CachedWorkItemHandlerConfig();
        wiConfig.register("Human Task", new PredictionAwareHumanTaskWorkItemHandler(predictionService));
        config = new StaticProcessConfig(wiConfig, new DefaultProcessEventListenerConfig(), new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
    }

    @Test
    public void testUserTaskWithPredictionService() {
        predictNow.set(true);

        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2)
                .containsEntry("s", "predicted value");

        assertThat(trainedTasks).isEmpty();
    }

    @Test
    public void testUserTaskWithoutPredictionService() {

        BpmnProcess process = BpmnProcess.from(config, new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        process.configure();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters()).containsEntry("ActorId", "john");
        processInstance.completeWorkItem(workItem.getId(), Collections.singletonMap("output", "given value"), securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2)
                .containsEntry("s", "given value");

        assertThat(trainedTasks).hasSize(1);
    }
}
