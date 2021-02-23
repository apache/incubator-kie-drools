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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartialDependencePlotExplainerTest {

    private PredictionProviderMetadata getMetadata(Random random) {
        return new PredictionProviderMetadata() {
            @Override
            public DataDistribution getDataDistribution() {
                return DataUtils.generateRandomDataDistribution(3, 100, random);
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
                outputs.add(new Output("sum-but0", Type.BOOLEAN, new Value<>(false), 0d));
                return new PredictionOutput(outputs);
            }
        };
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void testPdpNumericClassifier(int seed) throws Exception {
        Random random = new Random();
        random.setSeed(seed);
        PredictionProvider modelInfo = TestUtils.getSumSkipModel(0);
        PartialDependencePlotExplainer partialDependencePlotProvider = new PartialDependencePlotExplainer();
        List<PartialDependenceGraph> pdps = partialDependencePlotProvider.explainFromMetadata(modelInfo, getMetadata(random));
        assertNotNull(pdps);
        for (PartialDependenceGraph pdp : pdps) {
            assertNotNull(pdp.getFeature());
            assertNotNull(pdp.getX());
            assertNotNull(pdp.getY());
            assertEquals(pdp.getX().size(), pdp.getY().size());
            assertGraph(pdp);
        }
        // the first feature is always skipped by the model, so the predictions are not affected, hence PDP Y values are constant
        PartialDependenceGraph fixedFeatureGraph = pdps.get(0);
        assertEquals(1, fixedFeatureGraph.getY().stream().distinct().count());

        // the other two instead vary in Y values
        assertThat(pdps.get(1).getY().stream().distinct().count()).isGreaterThan(1);
        assertThat(pdps.get(2).getY().stream().distinct().count()).isGreaterThan(1);
    }

    private void assertGraph(PartialDependenceGraph pdp) {
        for (int i = 0; i < pdp.getX().size(); i++) {
            assertNotEquals(Double.NaN, pdp.getY().get(i).asNumber());
            if (i > 0) {
                assertTrue(pdp.getX().get(i).asNumber() >= pdp.getX().get(i - 1).asNumber());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void testBrokenPredict(int seed) {
        Random random = new Random();
        random.setSeed(seed);
        Config.INSTANCE.setAsyncTimeout(1);
        Config.INSTANCE.setAsyncTimeUnit(TimeUnit.MILLISECONDS);
        PartialDependencePlotExplainer partialDependencePlotProvider = new PartialDependencePlotExplainer();
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
                                () -> partialDependencePlotProvider.explainFromMetadata(brokenProvider, getMetadata(random)));
        Config.INSTANCE.setAsyncTimeout(Config.DEFAULT_ASYNC_TIMEOUT);
        Config.INSTANCE.setAsyncTimeUnit(Config.DEFAULT_ASYNC_TIMEUNIT);
    }

    @Test
    void testTextClassifier() throws Exception {
        Random random = new Random();
        for (int seed = 0; seed < 5; seed++) {
            random.setSeed(seed);
            PartialDependencePlotExplainer partialDependencePlotExplainer = new PartialDependencePlotExplainer();
            PredictionProvider model = TestUtils.getDummyTextClassifier();
            Collection<Prediction> predictions = new ArrayList<>(3);

            List<String> texts = List.of("we want your money", "please reply quickly", "you are the lucky winner",
                                         "huge donation for you!", "bitcoin for you");
            for (String text : texts) {
                List<Feature> features = new ArrayList<>();
                features.add(FeatureFactory.newFulltextFeature("text", text));
                PredictionInput predictionInput = new PredictionInput(features);
                PredictionOutput predictionOutput = model.predictAsync(List.of(predictionInput)).get().get(0);
                predictions.add(new Prediction(predictionInput, predictionOutput));
            }
            List<PartialDependenceGraph> pdps = partialDependencePlotExplainer.explainFromPredictions(model, predictions);
            assertThat(pdps).isNotEmpty();
        }
    }
}