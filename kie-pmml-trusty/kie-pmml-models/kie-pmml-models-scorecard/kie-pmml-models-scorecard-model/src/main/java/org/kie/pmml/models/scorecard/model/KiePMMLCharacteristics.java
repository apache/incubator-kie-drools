/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.scorecard.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Scorecard.html#xsdElement_Characteristics>Characteristics</a>
 */
public class KiePMMLCharacteristics extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = 2399787298848608820L;

    public static Number addNumbers(Number a, Number b) {
        if (a == null && b == null) {
            return null;
        }
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.doubleValue() + b.doubleValue();
    }

    public static Number calculatePartialScore(Number baselineScore, Number partialScore, REASONCODE_ALGORITHM reasoncodeAlgorithm) {
        if (baselineScore == null && partialScore == null) {
            return null;
        }
        if (baselineScore == null) {
            return partialScore;
        }
        if (partialScore == null) {
            return baselineScore;
        }
        switch (reasoncodeAlgorithm) {
            case POINTS_BELOW:
                return baselineScore.doubleValue() - partialScore.doubleValue();
            case POINTS_ABOVE:
                return partialScore.doubleValue() - baselineScore.doubleValue();
            default:
                throw new IllegalArgumentException(String.format("Unknown REASONCODE_ALGORITHM %s", reasoncodeAlgorithm));
        }
    }

    protected KiePMMLCharacteristics(String modelName) {
        super(modelName, Collections.emptyList());
    }

    /**
     * Method to return the <b>first</b> matching <code>Characteristic</code> score
     * @param characteristicFunctions: the first <code>Map</code> is the input data, the second <code>Map</code> is the <b>outputfieldsmap</b>
     * @param requestData
     * @return
     */
    protected static Optional<Number> getCharacteristicsScore(final List<BiFunction<Map<String, Object>, Map<String, Object>, Number>> characteristicFunctions,
                                                              final Map<String, Object> requestData,
                                                              final Map<String, Object> outputFieldsMap,
                                                              final Number initialScore) {
        Number accumulator = null;
        for (BiFunction<Map<String, Object>, Map<String, Object>, Number> function : characteristicFunctions) {
            final Number evaluation = function.apply(requestData, outputFieldsMap);
            if (evaluation != null) {
                if (accumulator == null) {
                    accumulator = initialScore != null ? initialScore : 0;
                }
                accumulator = addNumbers(accumulator, evaluation);
            }
        }
        return Optional.ofNullable(accumulator);
    }

}
