/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.explainability.model.Saliency;

public class ShapResults {
    private final Saliency[] saliencies;
    private final double[] fnull;

    public ShapResults(Saliency[] saliencies, double[] fnull) {
        this.saliencies = saliencies;
        this.fnull = fnull;
    }

    public Saliency[] getSaliencies() {
        return saliencies;
    }

    public double[] getFnull() {
        return fnull;
    }
}
