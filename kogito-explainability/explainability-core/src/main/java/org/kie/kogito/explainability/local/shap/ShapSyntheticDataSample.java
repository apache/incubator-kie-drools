/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.local.shap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionInput;

public class ShapSyntheticDataSample {
    private final PredictionInput x;
    private final boolean[] mask;
    private final RealMatrix background;
    private double weight;
    private final boolean fixed;
    private final List<PredictionInput> syntheticData;

    /**
     * Define a ShapSyntheticDataSample object, which tracks the synthetic data generated in the kernel shap process.
     * This is used internally by the ShapKernelExplainer, and should never need to be directly
     * interacted with by the user.
     *
     * @param x: The current PredictionInput being tracked by this sample
     * @param mask: The feature exclusion/inclusions of this particular sample
     * @param background: The background data specified by the user
     * @param weight: The weight of this particular sample
     * @param fixed: Whether or not this sample comes from a fully enumerated subset or not. If not, the weight
     *        provided will need to be adjusted later.
     *
     */

    public ShapSyntheticDataSample(PredictionInput x, boolean[] mask, RealMatrix background, double weight, boolean fixed) {
        this.x = x;
        this.mask = mask;
        this.background = background;
        this.weight = weight;
        this.fixed = fixed;
        this.syntheticData = this.createSyntheticData();
    }

    /**
     * Create synthetic data for this particular sample,
     * according to the conditions set up in the ShapSyntheticDataSample initialization.
     *
     * @return Synthetic data for this particular sample
     */
    private List<PredictionInput> createSyntheticData() {
        List<Feature> piFeatures = this.x.getFeatures();
        List<PredictionInput> synthData = new ArrayList<>();
        for (int i = 0; i < this.background.getRowDimension(); i++) {
            List<Feature> maskedFeatures = new ArrayList<>();
            for (int j = 0; j < this.mask.length; j++) {
                Feature oldFeature = piFeatures.get(j);
                if (this.mask[j]) {
                    maskedFeatures.add(oldFeature);
                } else {
                    maskedFeatures.add(FeatureFactory.newNumericalFeature(oldFeature.getName(), this.background.getEntry(i, j)));
                }
            }
            synthData.add(new PredictionInput(maskedFeatures));
        }
        return synthData;
    }

    /**
     * getters and setters for the various attributes
     */
    public boolean isFixed() {
        return this.fixed;
    }

    public List<PredictionInput> getSyntheticData() {
        return this.syntheticData;
    }

    public boolean[] getMask() {
        return this.mask;
    }

    public void incrementWeight() {
        this.weight += 1.;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
