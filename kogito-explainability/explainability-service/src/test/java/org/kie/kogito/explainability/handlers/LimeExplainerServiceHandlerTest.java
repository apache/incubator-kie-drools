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
package org.kie.kogito.explainability.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.api.SaliencyModel;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.SimplePrediction;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LimeExplainerServiceHandlerTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String SERVICE_URL = "serviceURL";

    private static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("resourceType", "resourceId");

    private LimeExplainer explainer;

    private LimeExplainerServiceHandler handler;

    @BeforeEach
    public void setup() {
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);

        this.explainer = mock(LimeExplainer.class);
        this.handler = new LimeExplainerServiceHandler(explainer, predictionProviderFactory);
    }

    @Test
    public void testSupports() {
        assertTrue(handler.supports(LIMEExplainabilityRequest.class));
        assertFalse(handler.supports(BaseExplainabilityRequest.class));
    }

    @Test
    public void testGetPredictionWithEmptyDefinition() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyList(),
                Collections.emptyList());

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof SimplePrediction);
        SimplePrediction simplePrediction = (SimplePrediction) prediction;

        assertTrue(simplePrediction.getInput().getFeatures().isEmpty());
        assertTrue(simplePrediction.getOutput().getOutputs().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetPredictionWithNonEmptyDefinition() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                List.of(new NamedTypedValue("input1",
                        new UnitValue("number", "number", new IntNode(20))),
                        new NamedTypedValue("input2",
                                new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55))))),
                        new NamedTypedValue("input3",
                                new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100)))))),
                List.of(new NamedTypedValue("output1",
                        new UnitValue("number", "number", new IntNode(20))),
                        new NamedTypedValue("output2",
                                new StructureValue("number", Map.of("output2b", new UnitValue("number", new IntNode(55))))),
                        new NamedTypedValue("output3",
                                new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100)))))));

        Prediction prediction = handler.getPrediction(request);

        //Inputs
        assertEquals(3, prediction.getInput().getFeatures().size());
        Optional<Feature> oInput1 = prediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input1")).findFirst();
        assertTrue(oInput1.isPresent());
        Feature input1 = oInput1.get();
        assertEquals(Type.NUMBER, input1.getType());
        assertEquals(20, input1.getValue().asNumber());

        Optional<Feature> oInput2 = prediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input2")).findFirst();
        assertTrue(oInput2.isPresent());
        Feature input2 = oInput2.get();
        assertEquals(Type.COMPOSITE, input2.getType());
        assertTrue(input2.getValue().getUnderlyingObject() instanceof List);
        List<Feature> input2Object = (List<Feature>) input2.getValue().getUnderlyingObject();
        assertEquals(1, input2Object.size());

        Optional<Feature> oInput2Child = input2Object.stream().filter(f -> f.getName().equals("input2b")).findFirst();
        assertTrue(oInput2Child.isPresent());
        Feature input2Child = oInput2Child.get();
        assertEquals(Type.NUMBER, input2Child.getType());
        assertEquals(55, input2Child.getValue().asNumber());

        Optional<Feature> oInput3 = prediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input3")).findFirst();
        assertTrue(oInput3.isPresent());
        Feature input3 = oInput3.get();
        assertEquals(Type.COMPOSITE, input3.getType());
        assertTrue(input3.getValue().getUnderlyingObject() instanceof List);
        List<Feature> input3Object = (List<Feature>) input3.getValue().getUnderlyingObject();
        assertEquals(1, input3Object.size());

        Feature input3Child = input3Object.get(0);
        assertEquals(Type.NUMBER, input3Child.getType());
        assertEquals(100, input3Child.getValue().asNumber());

        //Outputs
        assertEquals(3, prediction.getOutput().getOutputs().size());
        Optional<Output> oOutput1 = prediction.getOutput().getOutputs().stream().filter(o -> o.getName().equals("output1")).findFirst();
        assertTrue(oOutput1.isPresent());
        Output output1 = oOutput1.get();
        assertEquals(Type.NUMBER, output1.getType());
        assertEquals(20, output1.getValue().asNumber());

        Optional<Output> oOutput2 = prediction.getOutput().getOutputs().stream().filter(o -> o.getName().equals("output2")).findFirst();
        assertTrue(oOutput2.isPresent());
        Output output2 = oOutput2.get();
        assertEquals(Type.COMPOSITE, input2.getType());
        assertTrue(output2.getValue().getUnderlyingObject() instanceof List);
        List<Output> output2Object = (List<Output>) output2.getValue().getUnderlyingObject();
        assertEquals(1, output2Object.size());

        Optional<Output> oOutput2Child = output2Object.stream().filter(f -> f.getName().equals("output2b")).findFirst();
        assertTrue(oOutput2Child.isPresent());
        Output output2Child = oOutput2Child.get();
        assertEquals(Type.NUMBER, output2Child.getType());
        assertEquals(55, output2Child.getValue().asNumber());

        Optional<Output> oOutput3 = prediction.getOutput().getOutputs().stream().filter(o -> o.getName().equals("output3")).findFirst();
        assertTrue(oOutput3.isPresent());
        Output output3 = oOutput3.get();
        assertEquals(Type.COMPOSITE, output3.getType());
        assertTrue(output3.getValue().getUnderlyingObject() instanceof List);
        List<Output> output3Object = (List<Output>) output3.getValue().getUnderlyingObject();
        assertEquals(1, output3Object.size());

        Output output3Child = output3Object.get(0);
        assertEquals(Type.NUMBER, output3Child.getType());
        assertEquals(100, output3Child.getValue().asNumber());
    }

    @Test
    public void testCreateSucceededResult() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyList(),
                Collections.emptyList());

        Map<String, Saliency> saliencies = Map.of("s1",
                new Saliency(new Output("salary", Type.NUMBER),
                        List.of(new FeatureImportance(new Feature("age", Type.NUMBER, new Value(25.0)), 5.0),
                                new FeatureImportance(new Feature("dependents", Type.NUMBER, new Value(2)), -11.0))));

        BaseExplainabilityResult base = handler.createSucceededResult(request, saliencies);
        assertTrue(base instanceof LIMEExplainabilityResult);
        LIMEExplainabilityResult result = (LIMEExplainabilityResult) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(1, result.getSaliencies().size());

        SaliencyModel saliencyModel = result.getSaliencies().iterator().next();
        assertEquals(2, saliencyModel.getFeatureImportance().size());
        assertEquals("age", saliencyModel.getFeatureImportance().get(0).getFeatureName());
        assertEquals(5.0, saliencyModel.getFeatureImportance().get(0).getFeatureScore());
        assertEquals("dependents", saliencyModel.getFeatureImportance().get(1).getFeatureName());
        assertEquals(-11.0, saliencyModel.getFeatureImportance().get(1).getFeatureScore());
    }

    @Test
    public void testCreateIntermediateResult() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyList(),
                Collections.emptyList());

        assertThrows(UnsupportedOperationException.class, () -> handler.createIntermediateResult(request, null));
    }

    @Test
    public void testCreateFailedResult() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyList(),
                Collections.emptyList());

        BaseExplainabilityResult base = handler.createFailedResult(request, new NullPointerException("Something went wrong"));
        assertTrue(base instanceof LIMEExplainabilityResult);
        LIMEExplainabilityResult result = (LIMEExplainabilityResult) base;

        assertEquals(ExplainabilityStatus.FAILED, result.getStatus());
        assertEquals("Something went wrong", result.getStatusDetails());
        assertEquals(EXECUTION_ID, result.getExecutionId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExplainAsyncDelegation() {
        Prediction prediction = mock(Prediction.class);
        PredictionProvider predictionProvider = mock(PredictionProvider.class);

        handler.explainAsync(prediction, predictionProvider);

        verify(explainer).explainAsync(eq(prediction), eq(predictionProvider), any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExplainAsyncWithConsumerDelegation() {
        Prediction prediction = mock(Prediction.class);
        PredictionProvider predictionProvider = mock(PredictionProvider.class);
        Consumer<Map<String, Saliency>> callback = mock(Consumer.class);

        handler.explainAsync(prediction, predictionProvider, callback);

        verify(explainer).explainAsync(eq(prediction), eq(predictionProvider), eq(callback));
    }

}
