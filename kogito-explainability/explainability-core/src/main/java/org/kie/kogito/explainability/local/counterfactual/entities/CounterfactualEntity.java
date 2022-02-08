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
package org.kie.kogito.explainability.local.counterfactual.entities;

import org.kie.kogito.explainability.model.Feature;

public interface CounterfactualEntity {
    public double distance();

    public Feature asFeature();

    public boolean isConstrained();

    public boolean isChanged();

    /**
     * Returns a similarity score akin to the Gower similarity, weighted by the search domain bounds.
     * See paper: "A general coefficient of similarity and some of its properties.", JC Gower.
     *
     * @return Similarity score, a {@link Double} between 0.0 (lowest similarity) and 1.0 (highest similarity).
     */
    public double similarity();
}
