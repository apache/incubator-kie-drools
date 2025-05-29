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
package org.kie.kogito.predictions.smile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.prediction.api.PredictionAwareHumanTaskWorkItemHandler;
import org.kie.kogito.prediction.api.PredictionService;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.process.bpmn2.StaticApplicationAssembler;
import org.kie.kogito.process.impl.StaticProcessConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;

public class SmileRandomForestPredictionTest {

    private PredictionService predictionService;

    @BeforeEach
    public void configure() {

        final RandomForestConfiguration configuration = new RandomForestConfiguration();

        final Map<String, AttributeType> inputFeatures = new HashMap<>();
        inputFeatures.put("ActorId", AttributeType.NOMINAL);
        configuration.setInputFeatures(inputFeatures);

        configuration.setOutcomeName("output");
        configuration.setOutcomeType(AttributeType.NOMINAL);
        configuration.setConfidenceThreshold(0.7);
        configuration.setNumTrees(1);

        predictionService = new SmileRandomForest(configuration);

        for (int i = 0; i < 10; i++) {
            predictionService.train(null, Collections.singletonMap("ActorId", "john"), Collections.singletonMap("output", "predicted value"));
        }
        for (int i = 0; i < 8; i++) {
            predictionService.train(null, Collections.singletonMap("ActorId", "mary"), Collections.singletonMap("output", "value"));
        }
    }

    private BpmnProcess createProcess(String fileName) {
        StaticProcessConfig processConfig = StaticProcessConfig.newStaticProcessConfigBuilder()
                .withWorkItemHandler("Human Task", new PredictionAwareHumanTaskWorkItemHandler(predictionService))
                .build();

        Application application = StaticApplicationAssembler.instance().newStaticApplication(null, processConfig, fileName);

        org.kie.kogito.process.Processes container = application.get(org.kie.kogito.process.Processes.class);
        String processId = container.processIds().stream().findFirst().get();
        org.kie.kogito.process.Process<? extends Model> process = container.processById(processId);

        return (BpmnProcess) process;
    }

    @Test
    public void testUserTaskWithPredictionService() {
        BpmnProcess process = createProcess("BPMN2-UserTask.bpmn2");

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));

        processInstance.start();
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        Model result = (Model) processInstance.variables();
        assertThat(result.toMap()).hasSize(2)
                .containsEntry("s", "predicted value");
    }
}
