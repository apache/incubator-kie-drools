/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.mockito.ArgumentCaptor;

import static org.kie.kogito.tracing.decision.DecisionTestUtils.DECISION_SERVICE_NODE_NAME;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_ALL_EXECUTION_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_DECISION_SERVICE_EXECUTION_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.MODEL_NAME;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.MODEL_NAMESPACE;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.createDMNRuntime;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.getEvaluateAllContext;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.getEvaluateDecisionServiceContext;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This is not a real test and should always be annotated with {@link Disabled}.
 * <p>
 * Its purpose is to demonstrate how to generate the content of the test JSON resources representing
 * the list of {@link EvaluateEvent} produced when calling {@link DMNRuntime#evaluateAll(DMNModel, DMNContext)}
 * or {@link DMNRuntime#evaluateDecisionService(DMNModel, DMNContext, String)} in case they need to be updated
 * (e.g. when adding/changing fields to the events).
 */
@Disabled
class EvaluateEventJsonGeneratorTest {

    static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false)
            .registerModule(new JavaTimeModule())
            .setDefaultPrettyPrinter(new TestPrettyPrinter());

    @Test
    void generateEvaluateAll() throws JsonProcessingException {
        generate(EVALUATE_ALL_EXECUTION_ID, getEvaluateAllContext(), DecisionModel::evaluateAll, 14);
    }

    @Test
    void generateEvaluateDecisionService() throws JsonProcessingException {
        generate(EVALUATE_DECISION_SERVICE_EXECUTION_ID, getEvaluateDecisionServiceContext(), (model, context) -> model.evaluateDecisionService(context, DECISION_SERVICE_NODE_NAME), 6);
    }

    private void generate(String executionId, Map<String, Object> contextVariables, BiConsumer<DecisionModel, DMNContext> modelConsumer, int expectedEvents) throws JsonProcessingException {
        final DMNRuntime runtime = createDMNRuntime();

        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        runtime.addListener(listener);

        final DecisionModel model = new DmnDecisionModel(runtime, MODEL_NAMESPACE, MODEL_NAME, () -> executionId);
        final DMNContext context = model.newContext(contextVariables);
        modelConsumer.accept(model, context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);
        verify(eventConsumer, times(expectedEvents)).accept(eventCaptor.capture());

        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(eventCaptor.getAllValues()));
    }

    static class TestPrettyPrinter extends DefaultPrettyPrinter {

        public TestPrettyPrinter() {
            _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
            _objectFieldValueSeparatorWithSpaces = ": ";
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new TestPrettyPrinter();
        }

        @Override
        public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException
        {
            if (!_objectIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfEntries > 0) {
                _objectIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw('}');
        }

        @Override
        public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException
        {
            if (!_arrayIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfValues > 0) {
                _arrayIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw(']');
        }
    }

}
