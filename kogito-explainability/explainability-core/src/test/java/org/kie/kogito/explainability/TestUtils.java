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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils {

    public static PredictionProvider getFeaturePassModel(int featureIndex) {
        return inputs -> supplyAsync(() -> {
            List<PredictionOutput> predictionOutputs = new LinkedList<>();
            for (PredictionInput predictionInput : inputs) {
                List<Feature> features = predictionInput.getFeatures();
                Feature feature = features.get(featureIndex);
                PredictionOutput predictionOutput = new PredictionOutput(
                        List.of(new Output("feature-" + featureIndex, feature.getType(), feature.getValue(),
                                           1d)));
                predictionOutputs.add(predictionOutput);
            }
            return predictionOutputs;
        });
    }

    public static PredictionProvider getSumSkipModel(int skipFeatureIndex) {
        return inputs -> supplyAsync(() -> {
            List<PredictionOutput> predictionOutputs = new LinkedList<>();
            for (PredictionInput predictionInput : inputs) {
                List<Feature> features = predictionInput.getFeatures();
                double result = 0;
                for (int i = 0; i < features.size(); i++) {
                    if (skipFeatureIndex != i) {
                        result += features.get(i).getValue().asNumber();
                    }
                }
                PredictionOutput predictionOutput = new PredictionOutput(
                        List.of(new Output("sum-but" + skipFeatureIndex, Type.NUMBER, new Value<>(result), 1d)));
                predictionOutputs.add(predictionOutput);
            }
            return predictionOutputs;
        });
    }

    public static PredictionProvider getEvenFeatureModel(int featureIndex) {
        return inputs -> supplyAsync(() -> {
            List<PredictionOutput> predictionOutputs = new LinkedList<>();
            for (PredictionInput predictionInput : inputs) {
                List<Feature> features = predictionInput.getFeatures();
                Feature feature = features.get(featureIndex);
                double v = feature.getValue().asNumber();
                PredictionOutput predictionOutput = new PredictionOutput(
                        List.of(new Output("feature-" + featureIndex, Type.BOOLEAN, new Value<>(v % 2 == 0), 1d)));
                predictionOutputs.add(predictionOutput);
            }
            return predictionOutputs;
        });
    }

    public static PredictionProvider getEvenSumModel(int skipFeatureIndex) {
        return inputs -> supplyAsync(() -> {
            List<PredictionOutput> predictionOutputs = new LinkedList<>();
            for (PredictionInput predictionInput : inputs) {
                List<Feature> features = predictionInput.getFeatures();
                double result = 0;
                for (int i = 0; i < features.size(); i++) {
                    if (skipFeatureIndex != i) {
                        result += features.get(i).getValue().asNumber();
                    }
                }
                PredictionOutput predictionOutput = new PredictionOutput(
                        List.of(new Output("sum-even-but" + skipFeatureIndex, Type.BOOLEAN, new Value<>(((int) result) % 2 == 0), 1d)));
                predictionOutputs.add(predictionOutput);
            }
            return predictionOutputs;
        });
    }

    public static PredictionProvider getDummyTextClassifier() {
        List<String> blackList = Arrays.asList("money", "$", "£", "bitcoin");
        return inputs -> supplyAsync(() -> {
            List<PredictionOutput> outputs = new LinkedList<>();
            for (PredictionInput input : inputs) {
                boolean spam = false;
                for (Feature f : input.getFeatures()) {
                    if (!spam) {
                        String s = f.getValue().asString();
                        String[] words = s.split(" ");
                        for (String w : words) {
                            if (blackList.contains(w)) {
                                spam = true;
                                break;
                            }
                        }
                    }
                }
                Output output = new Output("spam", Type.BOOLEAN, new Value<>(spam), 1d);
                outputs.add(new PredictionOutput(List.of(output)));
            }
            return outputs;
        });
    }

    public static Feature getMockedNumericFeature() {
        return getMockedNumericFeature(1d);
    }

    public static Feature getMockedFeature(Type type, Value<?> v) {
        Feature f = mock(Feature.class);
        when(f.getType()).thenReturn(type);
        when(f.getName()).thenReturn("f-" + type.name());
        when(f.getValue()).thenReturn(v);
        return f;
    }

    public static Feature getMockedTextFeature(String s) {
        Feature f = mock(Feature.class);
        when(f.getType()).thenReturn(Type.TEXT);
        when(f.getName()).thenReturn("f-text");
        Value<String> value = mock(Value.class);
        when(value.getUnderlyingObject()).thenReturn(s);
        when(value.asNumber()).thenReturn(Double.NaN);
        when(value.asString()).thenReturn(s);
        when(f.getValue()).thenReturn(value);
        return f;
    }

    public static Feature getMockedNumericFeature(double d) {
        Feature f = mock(Feature.class);
        when(f.getType()).thenReturn(Type.NUMBER);
        when(f.getName()).thenReturn("f-num");
        Value<Double> value = mock(Value.class);
        when(value.getUnderlyingObject()).thenReturn(d);
        when(value.asNumber()).thenReturn(d);
        when(value.asString()).thenReturn(String.valueOf(d));
        when(f.getValue()).thenReturn(value);
        return f;
    }
}
