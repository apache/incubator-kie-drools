/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.models.tree.model;

import java.util.LinkedHashMap;

import org.kie.pmml.commons.model.tuples.KiePMMLProbabilityConfidence;

/**
 * Class used as DTO to propagate Tree model results
 */
public class KiePMMLNodeResult {

    private final Object score;

    private final LinkedHashMap<String, Double> probabilityMap;
    private final LinkedHashMap<String, Double> confidenceMap;

    public KiePMMLNodeResult(Object score,
                             LinkedHashMap<String, KiePMMLProbabilityConfidence> probabilityConfidenceMap) {
        this.score = score;
        probabilityMap = new LinkedHashMap<>();
        confidenceMap = new LinkedHashMap<>();
        probabilityConfidenceMap.forEach((targetClass, probabilityConfidenceTuple) -> {
            probabilityMap.put(targetClass, probabilityConfidenceTuple.getProbability());
            confidenceMap.put(targetClass, probabilityConfidenceTuple.getConfidence());
        });
    }

    public Object getScore() {
        return score;
    }

    public LinkedHashMap<String, Double> getProbabilityMap() {
        return probabilityMap;
    }

    public LinkedHashMap<String, Double> getConfidenceMap() {
        return confidenceMap;
    }
}
