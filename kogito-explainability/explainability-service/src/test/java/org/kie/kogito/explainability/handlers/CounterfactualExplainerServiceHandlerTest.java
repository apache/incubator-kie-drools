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
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.PredictionProviderFactory;
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
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.FeatureDomain;
import org.kie.kogito.explainability.models.BaseExplainabilityRequest;
import org.kie.kogito.explainability.models.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
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
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);

        this.explainer = mock(CounterfactualExplainer.class);
        this.handler = new CounterfactualExplainerServiceHandler(explainer, predictionProviderFactory);
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
        assertEquals(requestDto.getOriginalInputs(), request.getOriginalInputs());
        assertEquals(requestDto.getGoals(), request.getGoals());
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

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertTrue(counterfactualPrediction.getInput().getFeatures().isEmpty());
        assertTrue(counterfactualPrediction.getOutput().getOutputs().isEmpty());
        assertTrue(counterfactualPrediction.getDomain().getFeatureDomains().isEmpty());
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

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getInput().getFeatures().size());
        Optional<Feature> oInput1 = counterfactualPrediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input1")).findFirst();
        assertTrue(oInput1.isPresent());
        Feature input1 = oInput1.get();
        assertEquals(Type.NUMBER, input1.getType());
        assertEquals(20, input1.getValue().asNumber());

        assertTrue(counterfactualPrediction.getOutput().getOutputs().isEmpty());
        assertTrue(counterfactualPrediction.getDomain().getFeatureDomains().isEmpty());
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

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getOutput().getOutputs().size());
        Optional<Output> oOutput1 = counterfactualPrediction.getOutput().getOutputs().stream().filter(f -> f.getName().equals("output1")).findFirst();
        assertTrue(oOutput1.isPresent());
        Output output1 = oOutput1.get();
        assertEquals(Type.NUMBER, output1.getType());
        assertEquals(20, output1.getValue().asNumber());

        assertTrue(counterfactualPrediction.getInput().getFeatures().isEmpty());
        assertTrue(counterfactualPrediction.getDomain().getFeatureDomains().isEmpty());
        assertTrue(counterfactualPrediction.getConstraints().isEmpty());
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

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getDomain().getFeatureDomains().size());
        FeatureDomain featureDomain1 = counterfactualPrediction.getDomain().getFeatureDomains().get(0);
        assertEquals(10, featureDomain1.getLowerBound());
        assertEquals(20, featureDomain1.getUpperBound());

        assertTrue(counterfactualPrediction.getInput().getFeatures().isEmpty());
        assertTrue(counterfactualPrediction.getOutput().getOutputs().isEmpty());
        assertEquals(1, counterfactualPrediction.getConstraints().size());
        assertTrue(counterfactualPrediction.getConstraints().get(0));
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
    public void testCreateSucceededResultDto() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualResult counterfactuals = new CounterfactualResult(List.of(DoubleEntity.from(new Feature("input1", Type.NUMBER, new Value(123.0d)), 0, 1000)),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID));

        BaseExplainabilityResultDto base = handler.createSucceededResultDto(request, counterfactuals);
        assertTrue(base instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto result = (CounterfactualExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(CounterfactualExplainabilityResultDto.Stage.FINAL, result.getStage());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(1, result.getInputs().size());
        assertTrue(result.getInputs().containsKey("input1"));
        TypedValue input1 = result.getInputs().get("input1");
        assertEquals(Double.class.getSimpleName(), input1.getType());
        assertEquals(TypedValue.Kind.UNIT, input1.getKind());
        assertEquals(123.0, input1.toUnit().getValue().asDouble());

        assertEquals(1, result.getOutputs().size());
        assertTrue(result.getOutputs().containsKey("output1"));
        TypedValue output1 = result.getOutputs().get("output1");
        assertEquals(Double.class.getSimpleName(), output1.getType());
        assertEquals(TypedValue.Kind.UNIT, output1.getKind());
        assertEquals(555.0, output1.toUnit().getValue().asDouble());
    }

    @Test
    public void testCreateSucceededResultDtoWithNullPredictions() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(),
                null,
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID));

        assertThrows(NullPointerException.class, () -> handler.createSucceededResultDto(request, counterfactuals));
    }

    @Test
    public void testCreateSucceededResultDtoWithEmptyPredictions() {
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

        assertThrows(IllegalStateException.class, () -> handler.createSucceededResultDto(request, counterfactuals));
    }

    @Test
    public void testCreateSucceededResultDtoWithMoreThanOnePrediction() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0))),
                        new PredictionOutput(List.of(new Output("output2", Type.NUMBER, new Value(777.0d), 2.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID));

        assertThrows(IllegalStateException.class, () -> handler.createSucceededResultDto(request, counterfactuals));
    }

    @Test
    public void testCreateIntermediateResultDto() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                COUNTERFACTUAL_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyMap());

        CounterfactualResult counterfactuals = new CounterfactualResult(List.of(DoubleEntity.from(new Feature("input1", Type.NUMBER, new Value(123.0d)), 0, 1000)),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID));

        BaseExplainabilityResultDto base = handler.createIntermediateResultDto(request, counterfactuals);
        assertTrue(base instanceof CounterfactualExplainabilityResultDto);
        CounterfactualExplainabilityResultDto result = (CounterfactualExplainabilityResultDto) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(CounterfactualExplainabilityResultDto.Stage.INTERMEDIATE, result.getStage());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(1, result.getInputs().size());
        assertTrue(result.getInputs().containsKey("input1"));
        TypedValue input1 = result.getInputs().get("input1");
        assertEquals(Double.class.getSimpleName(), input1.getType());
        assertEquals(TypedValue.Kind.UNIT, input1.getKind());
        assertEquals(123.0, input1.toUnit().getValue().asDouble());

        assertEquals(1, result.getOutputs().size());
        assertTrue(result.getOutputs().containsKey("output1"));
        TypedValue output1 = result.getOutputs().get("output1");
        assertEquals(Double.class.getSimpleName(), output1.getType());
        assertEquals(TypedValue.Kind.UNIT, output1.getKind());
        assertEquals(555.0, output1.toUnit().getValue().asDouble());
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
        Consumer<CounterfactualResult> callback = mock(Consumer.class);

        handler.explainAsync(prediction, predictionProvider, callback);

        verify(explainer).explainAsync(eq(prediction), eq(predictionProvider), eq(callback));
    }
}
