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
package org.kie.kogito.explainability.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.utils.DataUtils;

/**
 * Numeric feature distribution based on {@code double[]}.
 */
public class NumericFeatureDistribution implements FeatureDistribution {

    private final Feature feature;
    private final List<Value> doubles;
    private final Random random;

    public NumericFeatureDistribution(Feature feature, double[] doubles) {
        this(feature, doubles, new SecureRandom());
    }

    public NumericFeatureDistribution(Feature feature, double[] doubles, Random random) {
        this.feature = feature;
        this.doubles = toValuesList(doubles);
        this.random = random;
    }

    @Override
    public Feature getFeature() {
        return feature;
    }

    @Override
    public Value sample() {
        if (doubles.isEmpty()) {
            return new Value(null);
        } else {
            List<Value> samples = sample(1);
            if (samples.isEmpty()) {
                return new Value(null);
            } else {
                return samples.get(0);
            }
        }
    }

    @Override
    public List<Value> sample(int sampleSize) {
        return DataUtils.sampleWithReplacement(doubles, sampleSize, random);
    }

    private List<Value> toValuesList(double[] doubles) {
        return Arrays.stream(doubles).boxed().map(Value::new).collect(Collectors.toList());
    }

    @Override
    public List<Value> getAllSamples() {
        List<Value> values = new ArrayList<>(doubles);
        Collections.shuffle(values);
        return Collections.unmodifiableList(values);
    }
}
