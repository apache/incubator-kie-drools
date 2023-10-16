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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.PredictionProviderFactory;
import org.kie.kogito.explainability.api.BaseExplainabilityRequest;
import org.kie.kogito.explainability.api.BaseExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualDomainRange;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitValue;
import org.kie.kogito.explainability.api.ExplainabilityStatus;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.model.CounterfactualPrediction;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.model.domain.EmptyFeatureDomain;
import org.kie.kogito.explainability.model.domain.NumericalFeatureDomain;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
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

    private static final Long MAX_RUNNING_TIME_SECONDS = 60L;

    private static final Long MAX_RUNNING_TIME_MILLISECONDS = MAX_RUNNING_TIME_SECONDS * 1000;

    private CounterfactualExplainer explainer;

    private CounterfactualExplainerServiceHandler handler;

    @BeforeEach
    public void setup() {
        PredictionProviderFactory predictionProviderFactory = mock(PredictionProviderFactory.class);

        this.explainer = mock(CounterfactualExplainer.class);
        this.handler = new CounterfactualExplainerServiceHandler(explainer,
                predictionProviderFactory,
                MAX_RUNNING_TIME_MILLISECONDS);
    }

    @Test
    public void testSupports() {
        assertTrue(handler.supports(CounterfactualExplainabilityRequest.class));
        assertFalse(handler.supports(BaseExplainabilityRequest.class));
    }

    @Test
    public void testGetPredictionWithEmptyDefinition() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertTrue(counterfactualPrediction.getInput().getFeatures().isEmpty());

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithFlatInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                List.of(new NamedTypedValue("input1",
                        new UnitValue("number", new IntNode(20)))),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getInput().getFeatures().size());
        Optional<Feature> oInput1 = counterfactualPrediction.getInput().getFeatures().stream().filter(f -> f.getName().equals("input1")).findFirst();
        assertTrue(oInput1.isPresent());
        Feature input1 = oInput1.get();
        assertEquals(Type.NUMBER, input1.getType());
        assertEquals(20, input1.getValue().asNumber());
        assertTrue(counterfactualPrediction.getInput().getFeatures().stream().allMatch(f -> f.getDomain().isEmpty()));
        assertTrue(counterfactualPrediction.getInput().getFeatures().stream().allMatch(Feature::isConstrained));

        assertTrue(counterfactualPrediction.getOutput().getOutputs().isEmpty());

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithStructuredInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                List.of(new NamedTypedValue("input1",
                        new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55)))))),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionInputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                List.of(new NamedTypedValue("input1",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100)))))),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithFlatOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                List.of(new NamedTypedValue("output1",
                        new UnitValue("number", new IntNode(20)))),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

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

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithFlatOutputModelReordered() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                List.of(new NamedTypedValue("inputsAreValid",
                        new UnitValue("boolean", BooleanNode.FALSE)),
                        new NamedTypedValue("canRequestLoan",
                                new UnitValue("booelan", BooleanNode.TRUE)),
                        new NamedTypedValue("my-scoring-function",
                                new UnitValue("number", new DoubleNode(0.85)))),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        List<Output> outputs = counterfactualPrediction.getOutput().getOutputs();
        assertEquals(3, outputs.size());
        Output output1 = outputs.get(0);
        assertEquals("my-scoring-function", output1.getName());
        assertEquals(Type.NUMBER, output1.getType());
        assertEquals(0.85, output1.getValue().asNumber());

        Output output2 = outputs.get(1);
        assertEquals("inputsAreValid", output2.getName());
        assertEquals(Type.BOOLEAN, output2.getType());
        assertEquals(Boolean.FALSE, output2.getValue().getUnderlyingObject());

        Output output3 = outputs.get(2);
        assertEquals("canRequestLoan", output3.getName());
        assertEquals(Type.BOOLEAN, output3.getType());
        assertEquals(Boolean.TRUE, output3.getValue().getUnderlyingObject());

        assertTrue(counterfactualPrediction.getInput().getFeatures().isEmpty());

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithStructuredOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                List.of(new NamedTypedValue("input1",
                        new StructureValue("number", Map.of("input2b", new UnitValue("number", new IntNode(55)))))),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionOutputModel() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                List.of(new NamedTypedValue("input1",
                        new CollectionValue("number", List.of(new UnitValue("number", new IntNode(100)))))),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithFlatSearchDomainsFixed() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                List.of(new NamedTypedValue("output1", new UnitValue("number", new IntNode(25)))),
                Collections.emptyList(),
                List.of(new CounterfactualSearchDomain("output1",
                        new CounterfactualSearchDomainUnitValue("number",
                                "number",
                                true,
                                new CounterfactualDomainRange(new IntNode(10), new IntNode(20))))),
                MAX_RUNNING_TIME_SECONDS);

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getInput().getFeatures().size());
        Feature feature1 = counterfactualPrediction.getInput().getFeatures().get(0);
        assertTrue(feature1.getDomain() instanceof EmptyFeatureDomain);

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithFlatSearchDomainsNotFixed() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                List.of(new NamedTypedValue("output1", new UnitValue("number", new IntNode(25)))),
                Collections.emptyList(),
                List.of(new CounterfactualSearchDomain("output1",
                        new CounterfactualSearchDomainUnitValue("number",
                                "number",
                                false,
                                new CounterfactualDomainRange(new IntNode(10), new IntNode(20))))),
                MAX_RUNNING_TIME_SECONDS);

        Prediction prediction = handler.getPrediction(request);
        assertTrue(prediction instanceof CounterfactualPrediction);
        CounterfactualPrediction counterfactualPrediction = (CounterfactualPrediction) prediction;

        assertEquals(1, counterfactualPrediction.getInput().getFeatures().size());
        Feature feature1 = counterfactualPrediction.getInput().getFeatures().get(0);
        assertTrue(feature1.getDomain() instanceof NumericalFeatureDomain);

        final NumericalFeatureDomain domain = (NumericalFeatureDomain) feature1.getDomain();
        assertEquals(10, domain.getLowerBound());
        assertEquals(20, domain.getUpperBound());

        assertEquals(counterfactualPrediction.getMaxRunningTimeSeconds(), request.getMaxRunningTimeSeconds());
    }

    @Test
    public void testGetPredictionWithStructuredSearchDomains() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(new CounterfactualSearchDomain("input1",
                        new CounterfactualSearchDomainStructureValue("number",
                                Map.of("input2b",
                                        new CounterfactualSearchDomainUnitValue("number",
                                                "number",
                                                true,
                                                new CounterfactualDomainRange(new IntNode(10), new IntNode(20))))))),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testGetPredictionWithCollectionSearchDomains() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                List.of(new CounterfactualSearchDomain("input1",
                        new CounterfactualSearchDomainCollectionValue("number",
                                List.of(new CounterfactualSearchDomainUnitValue("number",
                                        "number",
                                        true,
                                        new CounterfactualDomainRange(new IntNode(10), new IntNode(20))))))),
                MAX_RUNNING_TIME_SECONDS);

        assertThrows(IllegalArgumentException.class, () -> handler.getPrediction(request));
    }

    @Test
    public void testCreateSucceededResult() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        List<CounterfactualEntity> entities = List.of(DoubleEntity.from(new Feature("input1", Type.NUMBER, new Value(123.0d)), 0, 1000));
        CounterfactualResult counterfactuals = new CounterfactualResult(entities,
                entities.stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList()),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID),
                0);

        BaseExplainabilityResult base = handler.createSucceededResult(request, counterfactuals);
        assertTrue(base instanceof CounterfactualExplainabilityResult);
        CounterfactualExplainabilityResult result = (CounterfactualExplainabilityResult) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(CounterfactualExplainabilityResult.Stage.FINAL, result.getStage());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(1, result.getInputs().size());
        assertTrue(result.getInputs().stream().anyMatch(i -> i.getName().equals("input1")));
        NamedTypedValue input1 = result.getInputs().iterator().next();
        assertEquals(Double.class.getSimpleName(), input1.getValue().getType());
        assertEquals(TypedValue.Kind.UNIT, input1.getValue().getKind());
        assertEquals(123.0, input1.getValue().toUnit().getValue().asDouble());

        assertEquals(1, result.getOutputs().size());
        assertTrue(result.getOutputs().stream().anyMatch(o -> o.getName().equals("output1")));
        NamedTypedValue output1 = result.getOutputs().iterator().next();
        assertEquals(Double.class.getSimpleName(), output1.getValue().getType());
        assertEquals(TypedValue.Kind.UNIT, output1.getValue().getKind());
        assertEquals(555.0, output1.getValue().toUnit().getValue().asDouble());
    }

    @Test
    public void testCreateSucceededResultWithNullPredictions() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(),
                Collections.emptyList(),
                null,
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID),
                0);

        assertThrows(NullPointerException.class, () -> handler.createSucceededResult(request, counterfactuals));
    }

    @Test
    public void testCreateSucceededResultWithEmptyPredictions() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID),
                0);

        assertThrows(IllegalStateException.class, () -> handler.createSucceededResult(request, counterfactuals));
    }

    @Test
    public void testCreateSucceededResultWithMoreThanOnePrediction() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        CounterfactualResult counterfactuals = new CounterfactualResult(Collections.emptyList(), Collections.emptyList(),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0))),
                        new PredictionOutput(List.of(new Output("output2", Type.NUMBER, new Value(777.0d), 2.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID),
                0);

        assertThrows(IllegalStateException.class, () -> handler.createSucceededResult(request, counterfactuals));
    }

    @Test
    public void testCreateIntermediateResult() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        List<CounterfactualEntity> entities = List.of(DoubleEntity.from(new Feature("input1", Type.NUMBER, new Value(123.0d)), 0, 1000));
        CounterfactualResult counterfactuals = new CounterfactualResult(entities, entities.stream().map(
                CounterfactualEntity::asFeature).collect(
                        Collectors.toList()),
                List.of(new PredictionOutput(List.of(new Output("output1", Type.NUMBER, new Value(555.0d), 1.0)))),
                true,
                UUID.fromString(SOLUTION_ID),
                UUID.fromString(EXECUTION_ID),
                0);

        BaseExplainabilityResult base = handler.createIntermediateResult(request, counterfactuals);
        assertTrue(base instanceof CounterfactualExplainabilityResult);
        CounterfactualExplainabilityResult result = (CounterfactualExplainabilityResult) base;

        assertEquals(ExplainabilityStatus.SUCCEEDED, result.getStatus());
        assertEquals(CounterfactualExplainabilityResult.Stage.INTERMEDIATE, result.getStage());
        assertEquals(EXECUTION_ID, result.getExecutionId());
        assertEquals(COUNTERFACTUAL_ID, result.getCounterfactualId());
        assertEquals(1, result.getInputs().size());
        assertTrue(result.getInputs().stream().anyMatch(i -> i.getName().equals("input1")));
        NamedTypedValue input1 = result.getInputs().iterator().next();
        assertEquals(Double.class.getSimpleName(), input1.getValue().getType());
        assertEquals(TypedValue.Kind.UNIT, input1.getValue().getKind());
        assertEquals(123.0, input1.getValue().toUnit().getValue().asDouble());

        assertEquals(1, result.getOutputs().size());
        assertTrue(result.getOutputs().stream().anyMatch(o -> o.getName().equals("output1")));
        NamedTypedValue output1 = result.getOutputs().iterator().next();
        assertEquals(Double.class.getSimpleName(), output1.getValue().getType());
        assertEquals(TypedValue.Kind.UNIT, output1.getValue().getKind());
        assertEquals(555.0, output1.getValue().toUnit().getValue().asDouble());
    }

    @Test
    public void testCreateFailedResult() {
        CounterfactualExplainabilityRequest request = new CounterfactualExplainabilityRequest(EXECUTION_ID,
                SERVICE_URL,
                MODEL_IDENTIFIER,
                COUNTERFACTUAL_ID,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                MAX_RUNNING_TIME_SECONDS);

        BaseExplainabilityResult base = handler.createFailedResult(request, new NullPointerException("Something went wrong"));
        assertTrue(base instanceof CounterfactualExplainabilityResult);
        CounterfactualExplainabilityResult result = (CounterfactualExplainabilityResult) base;

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
