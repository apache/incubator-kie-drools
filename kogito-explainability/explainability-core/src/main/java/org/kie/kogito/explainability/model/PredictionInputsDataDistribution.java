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
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.kie.kogito.explainability.utils.DataUtils;

/**
 * Data distribution based on list of {@code PredictionInputs}.
 */
public class PredictionInputsDataDistribution implements DataDistribution {

    private final List<PredictionInput> inputs;
    private final Random random;

    public PredictionInputsDataDistribution(List<PredictionInput> inputs) {
        this(inputs, new SecureRandom());
    }

    public PredictionInputsDataDistribution(List<PredictionInput> inputs, Random random) {
        this.inputs = Collections.unmodifiableList(inputs);
        this.random = random;
    }

    @Override
    public PredictionInput sample() {
        if (inputs.isEmpty()) {
            return new PredictionInput(Collections.emptyList());
        } else {
            List<PredictionInput> singleSample = sample(1);
            if (singleSample.isEmpty()) {
                return new PredictionInput(Collections.emptyList());
            } else {
                return singleSample.get(0);
            }
        }
    }

    @Override
    public List<PredictionInput> sample(int sampleSize) {
        return DataUtils.sampleWithReplacement(inputs, sampleSize, random);
    }

    @Override
    public List<PredictionInput> getAllSamples() {
        List<PredictionInput> copy = new ArrayList<>(inputs);
        Collections.shuffle(copy);
        return copy;
    }

    @Override
    public List<FeatureDistribution> asFeatureDistributions() {
        if (inputs.isEmpty()) {
            return Collections.emptyList();
        } else {
            PredictionInput firstInput = inputs.get(0);
            List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(firstInput.getFeatures());
            int shape = linearizedFeatures.size();
            List<FeatureDistribution> featureDistributions = new ArrayList<>(shape);
            for (int i = 0; i < shape; i++) {
                Feature firstInputIthfeature = linearizedFeatures.get(i);
                List<Value> values = new ArrayList<>(inputs.size());
                for (PredictionInput input : inputs) {
                    List<Feature> currentInputLinearizedFeatures = DataUtils.getLinearizedFeatures(input.getFeatures());
                    if (currentInputLinearizedFeatures.size() > i) {
                        values.add(currentInputLinearizedFeatures.get(i).getValue());
                    } else {
                        values.add(new Value(null));
                    }
                }
                Feature feature = FeatureFactory.copyOf(firstInputIthfeature, new Value(null));
                featureDistributions.add(new GenericFeatureDistribution(feature, values));
            }
            return featureDistributions;
        }
    }
}
