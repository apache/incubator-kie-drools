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
package org.kie.kogito.explainability.global.pdp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.FakeRandom;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.global.GlobalExplanationException;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartialDependencePlotExplainerTest {

    PartialDependencePlotExplainer partialDependencePlotProvider = new PartialDependencePlotExplainer();
    PredictionProviderMetadata metadata = new PredictionProviderMetadata() {
        @Override
        public DataDistribution getDataDistribution() {
            return DataUtils.generateRandomDataDistribution(3, 100, new FakeRandom());
        }

        @Override
        public PredictionInput getInputShape() {
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newNumericalFeature("f0", 0));
            features.add(FeatureFactory.newNumericalFeature("f1", 0));
            features.add(FeatureFactory.newNumericalFeature("f2", 0));
            return new PredictionInput(features);
        }

        @Override
        public PredictionOutput getOutputShape() {
            List<Output> outputs = new LinkedList<>();
            outputs.add(new Output("spam", Type.BOOLEAN, new Value<>(false), 0d));
            return new PredictionOutput(outputs);
        }
    };

    @Test
    void testPdpNumericClassifier() throws Exception {
        PredictionProvider modelInfo = TestUtils.getSumSkipModel(0);
        List<PartialDependenceGraph> pdps = partialDependencePlotProvider.explain(modelInfo, metadata);
        assertNotNull(pdps);
        for (PartialDependenceGraph pdp : pdps) {
            assertNotNull(pdp.getFeature());
            assertNotNull(pdp.getX());
            assertNotNull(pdp.getY());
            assertEquals(pdp.getX().length, pdp.getY().length);
            assertGraph(pdp);
        }
        // the first feature is always skipped by the model, so the predictions are not affected, hence PDP Y values are constant
        PartialDependenceGraph fixedFeatureGraph = pdps.get(0);
        assertEquals(1, Arrays.stream(fixedFeatureGraph.getY()).distinct().count());

        // the other two instead change but in the same way, due the behaviour of FakeRandom in generating data/distributions
        assertArrayEquals(pdps.get(1).getY(), pdps.get(2).getY());
    }

    private void assertGraph(PartialDependenceGraph pdp) {
        for (int i = 0; i < pdp.getX().length; i++) {
            assertNotEquals(Double.NaN, pdp.getY()[i]);
            if (i > 0) {
                assertTrue(pdp.getX()[i] > pdp.getX()[i - 1]);
            }
        }
    }

    @Test
    void testBrokenPredict() {
        Config.INSTANCE.setAsyncTimeout(1);
        Config.INSTANCE.setAsyncTimeUnit(TimeUnit.MILLISECONDS);

        PredictionProvider brokenProvider = inputs -> supplyAsync(
                () -> {
                    try {
                        Thread.sleep(1000);
                        return Collections.emptyList();
                    } catch (InterruptedException e) {
                        throw new RuntimeException("this is a test");
                    }
                });

        Assertions.assertThrows(TimeoutException.class,
                                () -> partialDependencePlotProvider.explain(brokenProvider, metadata));

        Config.INSTANCE.setAsyncTimeout(Config.DEFAULT_ASYNC_TIMEOUT);
        Config.INSTANCE.setAsyncTimeUnit(Config.DEFAULT_ASYNC_TIMEUNIT);
    }
}