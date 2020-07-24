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

package org.kie.dmn.pmml;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class DMNNeuralNetworkPMMLTest {

    private static final Logger LOG = LoggerFactory.getLogger(DMNNeuralNetworkPMMLTest.class);

    private DMNRuntime runtime;
    private DMNModel dmnModel;

    @Test
    public void testNeuralNetworks() {
        runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("NeuralNetwork.dmn",
                                                                      DMNKMeansModelPMMLTest.class,
                                                                      "test_nn.pmml");

        dmnModel = runtime.getModel("https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456",
                                    "NeuralNetworkDMN");
        Assertions.assertThat(dmnModel).isNotNull();
        Assertions.assertThat(dmnModel.hasErrors()).isFalse();

        Assertions.assertThat(evaluateNeuralNetwork(5.7, 3.8, 1.7, 0.3))
                .isEqualTo("setosa");
        Assertions.assertThat(evaluateNeuralNetwork(6.4, 2.8, 5.6, 2.1))
                .isEqualTo("virginica");
        Assertions.assertThat(evaluateNeuralNetwork(5.7, 2.9, 4.2, 1.3))
                .isEqualTo("versicolor");
    }

    public String evaluateNeuralNetwork(final double sepalLength, final double sepalWidth, final double petalLength,
                                        final double petalWidth) {
        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("Sepal.Length", sepalLength);
        dmnContext.set("Sepal.Width", sepalWidth);
        dmnContext.set("Petal.Length", petalLength);
        dmnContext.set("Petal.Width", petalWidth);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
        LOG.debug("{}", dmnResult);
        Assertions.assertThat(dmnResult.hasErrors()).isFalse();
        Assertions.assertThat(dmnResult.getDecisionResultByName("Decision1").getResult()).isNotNull();
        final Map<String, Object> decisionResult = (Map<String, Object>) dmnResult.getDecisionResultByName("Decision1").getResult();
        final String predictedClassName = (String) decisionResult.get("Predicted_Species");

        return predictedClassName;
    }
}
