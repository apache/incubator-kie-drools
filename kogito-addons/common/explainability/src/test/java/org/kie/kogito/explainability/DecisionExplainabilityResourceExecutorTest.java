/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.explainability;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.explainability.model.ModelIdentifier;
import org.kie.kogito.explainability.model.PredictInput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.explainability.model.ModelIdentifier.RESOURCE_ID_SEPARATOR;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DecisionExplainabilityResourceExecutorTest {

    static Stream<Arguments> getDecisionModelTestProvider() {
        return Stream.of(
                Arguments.of((Object) new String[] { "namespace", "name" }),
                Arguments.of((Object) new String[] { "myFancyNamespace:/fdss2344-+{}\"", "myFancyName" }),
                Arguments.of((Object) new String[] { "hello::::", "myFancyName" }),
                Arguments.of((Object) new String[] { "hello()", "myFanc+_-_234';yName" }));
    }

    @ParameterizedTest
    @MethodSource("getDecisionModelTestProvider")
    public void testGetDecisionModel(String[] args) {
        String namespace = args[0];
        String name = args[1];
        DecisionExplainabilityResourceExecutor executor = new DecisionExplainabilityResourceExecutor();
        DecisionModels decisionModels = mock(DecisionModels.class);
        DecisionModel model = new DmnDecisionModel(generateDMNRuntime(namespace, name), namespace, name);
        when(decisionModels.getDecisionModel(eq(namespace), eq(name))).thenReturn(model);

        ModelIdentifier modelIdentifier = new ModelIdentifier("dmn", String.format("%s%s%s", namespace, RESOURCE_ID_SEPARATOR, name));
        DecisionModel decisionModelResponse = executor.getDecisionModel(decisionModels, modelIdentifier);
        assertThat(decisionModelResponse).isNotNull();
        assertThat(decisionModelResponse.getDMNModel().getNamespace()).isEqualTo(namespace);
        assertThat(decisionModelResponse.getDMNModel().getName()).isEqualTo(name);
    }

    @Test
    public void testAcceptRequest() {
        DecisionExplainabilityResourceExecutor executor = new DecisionExplainabilityResourceExecutor();
        ModelIdentifier notADMNModelIdentifier = new ModelIdentifier("notAdmn", String.format("%s%s%s", "namespace", RESOURCE_ID_SEPARATOR, "name"));
        ModelIdentifier DMNModelIdentifier = new ModelIdentifier("dmn", String.format("%s%s%s", "namespace", RESOURCE_ID_SEPARATOR, "name"));

        PredictInput notADMNModelPredictInput = new PredictInput(notADMNModelIdentifier, null);
        PredictInput DMNModelPredictInput = new PredictInput(DMNModelIdentifier, null);
        assertThat(executor.acceptRequest(notADMNModelPredictInput)).isFalse();
        assertThat(executor.acceptRequest(DMNModelPredictInput)).isTrue();
    }

    private DMNRuntime generateDMNRuntime(String namespace, String name) {
        DMNRuntime runtime = mock(DMNRuntime.class);
        DMNModel dmnModel = mock(DMNModel.class);
        when(dmnModel.getNamespace()).thenReturn(namespace);
        when(dmnModel.getName()).thenReturn(name);
        when(runtime.getModel(eq(namespace), eq(name))).thenReturn(dmnModel);
        return runtime;
    }
}
