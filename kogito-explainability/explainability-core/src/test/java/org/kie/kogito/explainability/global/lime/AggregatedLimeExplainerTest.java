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
package org.kie.kogito.explainability.global.lime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregatedLimeExplainerTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testExplainWithMetadata(int seed) throws ExecutionException, InterruptedException {
        Random random = new Random();
        random.setSeed(seed);
        PredictionProvider sumSkipModel = TestUtils.getSumSkipModel(1);
        PredictionProviderMetadata metadata = new PredictionProviderMetadata() {
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
                outputs.add(new Output("sum-but1", Type.BOOLEAN, new Value(false), 0d));
                return new PredictionOutput(outputs);
            }
        };

        AggregatedLimeExplainer aggregatedLimeExplainer = new AggregatedLimeExplainer();
        Map<String, Saliency> explain = aggregatedLimeExplainer.explainFromMetadata(sumSkipModel, metadata).get();
        assertNotNull(explain);
        assertEquals(1, explain.size());
        assertTrue(explain.containsKey("sum-but1"));
        Saliency saliency = explain.get("sum-but1");
        assertNotNull(saliency);
        List<String> collect = saliency.getPositiveFeatures(2).stream()
                .map(FeatureImportance::getFeature).map(Feature::getName).collect(Collectors.toList());
        assertFalse(collect.contains("f1")); // skipped feature should not appear in top two positive features
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void testExplainWithPredictions(int seed) throws ExecutionException, InterruptedException {
        Random random = new Random();
        random.setSeed(seed);
        PredictionProvider sumSkipModel = TestUtils.getSumSkipModel(1);
        DataDistribution dataDistribution = DataUtils.generateRandomDataDistribution(3, 100, random);
        List<PredictionInput> samples = dataDistribution.sample(10);
        List<PredictionOutput> predictionOutputs = sumSkipModel.predictAsync(samples).get();
        List<Prediction> predictions = DataUtils.getPredictions(samples, predictionOutputs);
        AggregatedLimeExplainer aggregatedLimeExplainer = new AggregatedLimeExplainer();
        Map<String, Saliency> explain = aggregatedLimeExplainer.explainFromPredictions(sumSkipModel, predictions).get();
        assertNotNull(explain);
        assertEquals(1, explain.size());
        assertTrue(explain.containsKey("sum-but1"));
        Saliency saliency = explain.get("sum-but1");
        assertNotNull(saliency);
        List<String> collect = saliency.getPositiveFeatures(2).stream()
                .map(FeatureImportance::getFeature).map(Feature::getName).collect(Collectors.toList());
        assertFalse(collect.contains("f1")); // skipped feature should not appear in top two positive features
    }
}
