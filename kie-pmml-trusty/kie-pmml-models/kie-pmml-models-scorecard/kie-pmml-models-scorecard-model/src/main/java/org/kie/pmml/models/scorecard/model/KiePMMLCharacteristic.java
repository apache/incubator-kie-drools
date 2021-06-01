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
 * @see <a href=http://dmg.org/pmml/v4-4-1/Scorecard.html#xsdElement_Characteristic>Characteristic</a>
 */
public class KiePMMLCharacteristic extends AbstractKiePMMLComponent {


    protected KiePMMLCharacteristic(String modelName) {
        super(modelName, Collections.emptyList());
    }

    /**
     * Method to return the <b>most specific</b> <code>Attribute</code> score
     * @param attributeFunctions
     * @param requestData
     * @return
     */
    public static Optional<Number> getCharacteristicScore(final List<BiFunction<Map<String, Object>, Map<String, Object>, Number>> attributeFunctions,
                                                          final Map<String, Object> requestData,
                                                          final Map<String, Object> outputFieldsMap,
                                                          final String reasonCode,
                                                          final Number baseScore,
                                                          final REASONCODE_ALGORITHM reasoncodeAlgorithm) {
        Optional<Number> toReturn = Optional.empty();
        for (BiFunction<Map<String, Object>, Map<String, Object>, Number> function : attributeFunctions) {
            final Number evaluation = function.apply(requestData, outputFieldsMap);
            if (evaluation != null) {
                toReturn = Optional.ofNullable(evaluation);
                if (reasonCode != null) {
                    Number rankingScore = KiePMMLCharacteristics.calculatePartialScore(baseScore, evaluation, reasoncodeAlgorithm);
                    outputFieldsMap.put(reasonCode, rankingScore);
                }
            }
            if (toReturn.isPresent()) {
                break;
            }
        }
        return toReturn;
    }

}
