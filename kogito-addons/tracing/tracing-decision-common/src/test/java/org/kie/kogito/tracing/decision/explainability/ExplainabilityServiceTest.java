/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.tracing.decision.explainability;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.DecisionTracingListener;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.explainability.ModelIdentifier;
import org.kie.kogito.tracing.decision.event.explainability.PredictInput;
import org.kie.kogito.tracing.decision.event.explainability.PredictOutput;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.kie.kogito.tracing.decision.event.explainability.ModelIdentifier.RESOURCE_ID_SEPARATOR;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.MODEL_NAME;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.MODEL_NAMESPACE;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.MODEL_RESOURCE;

public class ExplainabilityServiceTest {

    final static String TEST_EXECUTION_ID = "test";
    final static DMNRuntime genericDMNRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(
            ExplainabilityServiceTest.class.getResourceAsStream(MODEL_RESOURCE)
    ));
    final static DmnDecisionModel decisionModel = new DmnDecisionModel(genericDMNRuntime, MODEL_NAMESPACE, MODEL_NAME, () -> TEST_EXECUTION_ID);

    @Test
    public void testPerturbedExecution() {
        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        genericDMNRuntime.addListener(listener);

        DecisionModels decisionModels = new DecisionModels() {
            @Override
            public DecisionModel getDecisionModel(String namespace, String name) {
                if (MODEL_NAMESPACE.equals(namespace) && MODEL_NAME.equals(name)) {
                    return decisionModel;
                }
                throw new RuntimeException("Model not found.");
            }
        };

        Map<String, Object> perturbedRequest = createPerturbedRequest();
        PredictInput predictInput = new PredictInput(
                new ModelIdentifier("dmn", String.format("%s%s%s", MODEL_NAMESPACE, RESOURCE_ID_SEPARATOR, MODEL_NAME)),
                perturbedRequest);
        StaticApplication application = new StaticApplication(null, null, null, decisionModels, null);

        ExplainabilityService explainabilityService = ExplainabilityService.INSTANCE;
        PredictOutput predictOutput = explainabilityService.processRequest(application, predictInput);

        Assertions.assertNotNull(predictOutput);
        Assertions.assertNotNull(predictOutput.getResult());

        Map<String, Object> perturbedResult = predictOutput.getResult();
        Assertions.assertTrue(perturbedResult.containsKey("Should the driver be suspended?"));
        Assertions.assertTrue(perturbedResult.get("Should the driver be suspended?").equals("No"));
        Assertions.assertTrue(perturbedResult.containsKey("Fine"));
        Assertions.assertNull(perturbedResult.get("Fine"));

        verify(eventConsumer, times(0)).accept(any());
    }

    private Map<String, Object> createPerturbedRequest() {
        Map<String, Object> driver = new HashMap<>();
        driver.put("Age", 25);
        driver.put("Points", 10);

        Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 105);
        violation.put("Speed Limit", 100);

        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Driver", driver);
        contextVariables.put("Violation", violation);

        return contextVariables;
    }
}
