/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability.handlers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.LIMEExplainabilityRequestDto;
import org.kie.kogito.explainability.api.LIMEExplainabilityResultDto;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.explainability.api.SaliencyDto;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.explainability.models.LIMEExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LimeExplainerServiceHandlerTest {

    private static final String EXECUTION_ID = "executionId";

    private static final String SERVICE_URL = "serviceURL";

    private static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("resourceType", "resourceId");

    private static final ModelIdentifierDto MODEL_IDENTIFIER_DTO = new ModelIdentifierDto("resourceType", "resourceId");

    private LimeExplainer explainer;

    private LimeExplainerServiceHandler handler;

    @BeforeEach
    public void setup() {
        this.explainer = mock(LimeExplainer.class);
        this.handler = new LimeExplainerServiceHandler(explainer);
    }

    @Test
    public void testSupports() {
        assertTrue(handler.supports(LIMEExplainabilityRequest.class));
        assertFalse(handler.supports(BaseExplainabilityRequest.class));
    }

    @Test
    public void testSupportsDo() {
        assertTrue(handler.supportsDto(LIMEExplainabilityRequestDto.class));
        assertFalse(handler.supportsDto(BaseExplainabilityRequestDto.class));
    }

    @Test
    public void testExplainabilityRequestFrom() {
        LIMEExplainabilityRequestDto requestDto = new LIMEExplainabilityRequestDto(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER_DTO,
                Collections.emptyMap(),
                Collections.emptyMap());

        LIMEExplainabilityRequest request = handler.explainabilityRequestFrom(requestDto);

        assertEquals(requestDto.getExecutionId(), request.getExecutionId());
        assertEquals(requestDto.getServiceUrl(), request.getServiceUrl());
        assertEquals(requestDto.getModelIdentifier().getResourceId(), request.getModelIdentifier().getResourceId());
        assertEquals(requestDto.getModelIdentifier().getResourceType(), request.getModelIdentifier().getResourceType());
        assertEquals(requestDto.getInputs(), request.getInputs());
        assertEquals(requestDto.getOutputs(), request.getOutputs());
    }

    @Test
    public void testGetPredictionWithEmptyDefinition() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap());

        Prediction prediction = handler.getPrediction(request);

        assertTrue(prediction.getInput().getFeatures().isEmpty());
        assertTrue(prediction.getOutput().getOutputs().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetPredictionWithNonEmptyDefinition() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Map.of("input1",
                        new UnitValue("number", new IntNode(20)),
                        "input2",
                        new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55)))),
                        "input3",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100))))),
                Map.of("output1",
                        new UnitValue("number", new IntNode(20)),
                        "output2",
                        new StructureValue("number", Map.of("output2b", new UnitValue("number", new IntNode(55)))),
                        "output3",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100))))));

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
    public void testCreateSucceededResultDto() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap());

        Map<String, Saliency> saliencies = Map.of("s1",
                new Saliency(new Output("salary", Type.NUMBER),
                        List.of(new FeatureImportance(new Feature("age", Type.NUMBER, new Value(25.0)), 5.0),
                                new FeatureImportance(new Feature("dependents", Type.NUMBER, new Value(2)), -11.0))));

        BaseExplainabilityResultDto base = handler.createSucceededResultDto(request, saliencies);
        assertTrue(base instanceof LIMEExplainabilityResultDto);
        LIMEExplainabilityResultDto result = (LIMEExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(1, result.getSaliencies().size());
        assertTrue(result.getSaliencies().containsKey("s1"));

        SaliencyDto dto = result.getSaliencies().get("s1");
        assertEquals(2, dto.getFeatureImportance().size());
        assertEquals("age", dto.getFeatureImportance().get(0).getFeatureName());
        assertEquals(5.0, dto.getFeatureImportance().get(0).getScore());
        assertEquals("dependents", dto.getFeatureImportance().get(1).getFeatureName());
        assertEquals(-11.0, dto.getFeatureImportance().get(1).getScore());
    }

    @Test
    public void testCreateFailedResultDto() {
        LIMEExplainabilityRequest request = new LIMEExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap());

        BaseExplainabilityResultDto base = handler.createFailedResultDto(request, new NullPointerException("Something went wrong"));
        assertTrue(base instanceof LIMEExplainabilityResultDto);
        LIMEExplainabilityResultDto result = (LIMEExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.FAILED, result.getStatus());
        assertEquals("Something went wrong", result.getStatusDetails());
        assertEquals(EXECUTION_ID, result.getExecutionId());
    }

    @Test
    public void testExplainAsyncDelegation() {
        Prediction prediction = mock(Prediction.class);
        PredictionProvider model = mock(PredictionProvider.class);

        handler.explainAsync(prediction, model);

        verify(explainer).explainAsync(eq(prediction), eq(model));
    }

}
