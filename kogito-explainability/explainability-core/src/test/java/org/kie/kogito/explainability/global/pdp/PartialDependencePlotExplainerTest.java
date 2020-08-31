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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PartialDependencePlotExplainerTest {

    PartialDependencePlotExplainer partialDependencePlotProvider = new PartialDependencePlotExplainer();
    PredictionProviderMetadata metadata = new PredictionProviderMetadata() {
        @Override
        public DataDistribution getDataDistribution() {
            return DataUtils.generateRandomDataDistribution(10, 100, new FakeRandom());
        }

        @Override
        public PredictionInput getInputShape() {
            List<Feature> features = new LinkedList<>();
            features.add(FeatureFactory.newTextFeature("text", ""));
            return new PredictionInput(features);
        }

        @Override
        public PredictionOutput getOutputShape() {
            List<Output> outputs = new LinkedList<>();
            outputs.add(new Output("spam", Type.BOOLEAN, new Value<>(null), 0d));
            return new PredictionOutput(outputs);
        }
    };

    @Test
    void testPdpTextClassifier() throws Exception {
        PredictionProvider modelInfo = TestUtils.getDummyTextClassifier();
        Collection<PartialDependenceGraph> pdps = partialDependencePlotProvider.explain(modelInfo, metadata);
        assertNotNull(pdps);
        for (PartialDependenceGraph pdp : pdps) {
            assertNotNull(pdp.getFeature());
            assertNotNull(pdp.getX());
            assertNotNull(pdp.getY());
            assertEquals(pdp.getX().length, pdp.getY().length);
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