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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.math3.linear.RealVector;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Saliency;

public class ShapResults {
    private final Saliency[] saliencies;
    private final RealVector fnull;

    public ShapResults(Saliency[] saliencies, RealVector fnull) {
        this.saliencies = saliencies;
        this.fnull = fnull;
    }

    public Saliency[] getSaliencies() {
        return saliencies;
    }

    public RealVector getFnull() {
        return fnull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShapResults other = (ShapResults) o;
        if (this.saliencies.length != other.getSaliencies().length) {
            return false;
        }
        if (!this.fnull.equals(other.getFnull())) {
            return false;
        }
        for (int i = 0; i < this.saliencies.length; i++) {
            List<FeatureImportance> thisPFIs = this.saliencies[i].getPerFeatureImportance();
            List<FeatureImportance> otherPFIs = other.getSaliencies()[i].getPerFeatureImportance();
            if (thisPFIs.size() != otherPFIs.size()) {
                return false;
            }
            for (int j = 0; j < thisPFIs.size(); j++) {
                if (!thisPFIs.get(j).equals(otherPFIs.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(saliencies), fnull);
    }
}
