/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.tree.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.tuples.KiePMMLProbabilityConfidence;

public class KiePMMLNode extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -3166618610223066816L;

    protected KiePMMLNode(final String name,
                          final List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    protected static LinkedHashMap<String, KiePMMLProbabilityConfidence> getProbabilityConfidenceMap(final List<KiePMMLScoreDistribution> kiePMMLScoreDistributions,
                                                                                                     final double missingValuePenalty) {
        return (kiePMMLScoreDistributions != null && !kiePMMLScoreDistributions.isEmpty()) ?  evaluateProbabilityConfidenceMap(kiePMMLScoreDistributions, missingValuePenalty) : new LinkedHashMap<>();
    }

    protected static Optional<KiePMMLNodeResult> getNestedKiePMMLNodeResult(final List<Function<Map<String, Object>, KiePMMLNodeResult>> nodeFunctions, final Map<String, Object> requestData) {
        Optional<KiePMMLNodeResult> toReturn = Optional.empty();
        for (Function<Map<String, Object>, KiePMMLNodeResult> function : nodeFunctions) {
            final KiePMMLNodeResult evaluation = function.apply(requestData);
            toReturn = Optional.ofNullable(evaluation);
            if (toReturn.isPresent()) {
                break;
            }
        }
        return toReturn;
    }

    static LinkedHashMap<String, KiePMMLProbabilityConfidence> evaluateProbabilityConfidenceMap(final List<KiePMMLScoreDistribution> kiePMMLScoreDistributions,
                                                                                                final double missingValuePenalty) {
        LinkedHashMap<String, KiePMMLProbabilityConfidence> toReturn = new LinkedHashMap<>();
        if (kiePMMLScoreDistributions == null || kiePMMLScoreDistributions.isEmpty()) {
            return toReturn;
        }
        if (kiePMMLScoreDistributions.get(0).hasProbability()) {
            for (KiePMMLScoreDistribution kiePMMLScoreDistribution : kiePMMLScoreDistributions) {
                toReturn.put(kiePMMLScoreDistribution.getValue(), new KiePMMLProbabilityConfidence(kiePMMLScoreDistribution.getProbability(), kiePMMLScoreDistribution.getEvaluatedConfidence(missingValuePenalty)));
            }
        } else {
            int totalRecordCount = kiePMMLScoreDistributions.stream()
                    .map(KiePMMLScoreDistribution::getRecordCount)
                    .reduce(0, Integer::sum);
            for (KiePMMLScoreDistribution kiePMMLScoreDistribution : kiePMMLScoreDistributions) {
                toReturn.put(kiePMMLScoreDistribution.getValue(), new KiePMMLProbabilityConfidence(kiePMMLScoreDistribution.getEvaluatedProbability(totalRecordCount), kiePMMLScoreDistribution.getEvaluatedConfidence(missingValuePenalty)));
            }
        }
        return toReturn;
    }
    
}
