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
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.BaseExplainabilityRequestDto;
import org.kie.kogito.explainability.api.BaseExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualDomainRangeDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequestDto;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResultDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitDto;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.ModelIdentifierDto;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.explainability.models.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CounterfactualExplainerServiceHandlerTest {

    private static final String EXECUTION_ID = UUID.randomUUID().toString();

    private static final String COUNTERFACTUAL_ID = UUID.randomUUID().toString();

    private static final String SOLUTION_ID = UUID.randomUUID().toString();

    private static final String SERVICE_URL = "serviceURL";

    private static final ModelIdentifier MODEL_IDENTIFIER = new ModelIdentifier("resourceType", "resourceId");

    private static final ModelIdentifierDto MODEL_IDENTIFIER_DTO = new ModelIdentifierDto("resourceType", "resourceId");

    private CounterfactualExplainer explainer;

    private CounterfactualExplainerServiceHandler handler;

    @BeforeEach
    public void setup() {
        this.explainer = mock(CounterfactualExplainer.class);
        this.handler = new CounterfactualExplainerServiceHandler(explainer);
    }

    @Test
    public void testSupports() {
        assertTrue(handler.supports(CounterfactualExplainabilityRequest.class));
        assertFalse(handler.supports(BaseExplainabilityRequest.class));
    }

    @Test
    public void testSupportsDo() {
        assertTrue(handler.supportsDto(CounterfactualExplainabilityRequestDto.class));
        assertFalse(handler.supportsDto(BaseExplainabilityRequestDto.class));
    }

    @Test
    public void testExplainabilityRequestFrom() {
        CounterfactualExplainabilityRequestDto requestDto = new CounterfactualExplainabilityRequestDto(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER_DTO,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualExplainabilityRequest request = handler.explainabilityRequestFrom(requestDto);

        assertEquals(requestDto.getExecutionId(), request.getExecutionId());
        assertEquals(requestDto.getCounterfactualId(), request.getCounterfactualId());
        assertEquals(requestDto.getServiceUrl(), request.getServiceUrl());
        assertEquals(requestDto.getModelIdentifier().getResourceId(), request.getModelIdentifier().getResourceId());
        assertEquals(requestDto.getModelIdentifier().getResourceType(), request.getModelIdentifier().getResourceType());
        assertEquals(requestDto.getInputs(), request.getInputs());
        assertEquals(requestDto.getOutputs(), request.getOutputs());
        assertEquals(requestDto.getSearchDomains(), request.getSearchDomains());
    }

    @Test
    public void testGetPredictionWithEmptyDefinition() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualPrediction prediction = (CounterfactualPrediction) handler.getPrediction(request);

        assertTrue(prediction.getInput().getFeatures().isEmpty());
        assertTrue(prediction.getOutput().getOutputs().isEmpty());
        assertTrue(prediction.getDomain().getFeatureDomains().isEmpty());
    }

    @Test
    public void testGetPredictionWithFlatInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Map.of("input1",
                        new UnitValue("number", new IntNode(20))),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualPrediction prediction = (CounterfactualPrediction) handler.getPrediction(request);

        assertEquals(1, prediction.getInput().getFeatures().size());
        Optional<Feature> oInput1 = prediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input1")).findFirst();
        assertTrue(oInput1.isPresent());
        Feature input1 = oInput1.get();
        assertEquals(Type.NUMBER, input1.getType());
        assertEquals(20, input1.getValue().asNumber());

        assertTrue(prediction.getOutput().getOutputs().isEmpty());
        assertTrue(prediction.getDomain().getFeatureDomains().isEmpty());
    }

    @Test
    public void testGetPredictionWithStructuredInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Map.of("input1",
                        new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55))))),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Map.of("input1",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100))))),
                Collections.emptyMap(),
                Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithFlatOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Map.of("output1",
                        new UnitValue("number", new IntNode(20))),
                Collections.emptyMap());

        CounterfactualPrediction prediction = (CounterfactualPrediction) handler.getPrediction(request);

        assertEquals(1, prediction.getOutput().getOutputs().size());
        Optional<Output> oOutput1 = prediction.getOutput().getOutputs().stream().filter(f -> f.getName().equals("output1")).findFirst();
        assertTrue(oOutput1.isPresent());
        Output output1 = oOutput1.get();
        assertEquals(Type.NUMBER, output1.getType());
        assertEquals(20, output1.getValue().asNumber());

        assertTrue(prediction.getInput().getFeatures().isEmpty());
        assertTrue(prediction.getDomain().getFeatureDomains().isEmpty());
        assertTrue(prediction.getConstraints().isEmpty());
    }

    @Test
    public void testGetPredictionWithStructuredOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Map.of("input1",
                        new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55))))),
                Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Map.of("input1",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100))))),
                Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithFlatSearchDomains() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Map.of("output1",
                        new CounterfactualSearchDomainUnitDto("number",
                                true,
                                new CounterfactualDomainRangeDto(new IntNode(10), new IntNode(20)))));

        CounterfactualPrediction prediction = (CounterfactualPrediction) handler.getPrediction(request);

        assertEquals(1, prediction.getDomain().getFeatureDomains().size());
        FeatureDomain featureDomain1 = prediction.getDomain().getFeatureDomains().get(0);
        assertEquals(10, featureDomain1.getLowerBound());
        assertEquals(20, featureDomain1.getUpperBound());

        assertTrue(prediction.getInput().getFeatures().isEmpty());
        assertTrue(prediction.getOutput().getOutputs().isEmpty());
        assertEquals(1, prediction.getConstraints().size());
        assertTrue(prediction.getConstraints().get(0));
    }

    @Test
    public void testGetPredictionWithStructuredSearchDomains() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Map.of("input1",
                        new CounterfactualSearchDomainStructureDto("number",
                                Map.of("input2b",
                                        new CounterfactualSearchDomainUnitDto("number",
                                                true,
                                                new CounterfactualDomainRangeDto(new IntNode(10), new IntNode(20)))))));

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionSearchDomains() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Map.of("input1",
                        new CounterfactualSearchDomainCollectionDto("number",
                                List.of(new CounterfactualSearchDomainUnitDto("number",
                                        true,
                                        new CounterfactualDomainRangeDto(new IntNode(10), new IntNode(20)))))));

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    @Disabled("See https://issues.redhat.com/browse/FAI-439")
    //TODO When the results are passed back to TrustyService this will be completed.
    public void testCreateSucceededResultDto() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(),
                Collections.emptyList(),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID));

        BaseExplainabilityResultDto base = handler.createSucceededResultDto(request, counterfactuals);
        assertTrue(base instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto result = (CounterfactualExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertTrue(result.getInputs().isEmpty());
        assertTrue(result.getOutputs().isEmpty());
    }

    @Test
    public void testCreateFailedResultDto() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        BaseExplainabilityResultDto base = handler.createFailedResultDto(request, new NullPointerException("Something went wrong"));
        assertTrue(base instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto result = (CounterfactualExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.FAILED, result.getStatus());
        assertEquals("Something went wrong", result.getStatusDetails());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
    }

    @Test
    public void testExplainAsyncDelegation() {
        Prediction prediction = mock(Prediction.class);
        PredictionProvider model = mock(PredictionProvider.class);

        handler.explainAsync(prediction, model);

        verify(explainer).explainAsync(eq(prediction), eq(model));
    }
}
