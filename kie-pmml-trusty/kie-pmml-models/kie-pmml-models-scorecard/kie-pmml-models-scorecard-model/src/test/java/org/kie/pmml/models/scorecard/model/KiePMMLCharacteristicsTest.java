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

package org.kie.pmml.models.scorecard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLCharacteristicsTest {

    private static final String CUSTOM_FIELD = "CUSTOM_FIELD";
    private static final String REASON_CODE = "REASON_CODE";
    private static final String REASON_CODE_1 = "REASON_CODE_1";
    private static final String REASON_CODE_2 = "REASON_CODE_2";
    private static final String ATTRIBUTE_1 = "ATTRIBUTE_1";
    private static final String ATTRIBUTE_2 = "ATTRIBUTE_2";
    private static final Double baselineScore = 123.0;
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;

    @Test
    public void evaluate() {
        Double initialScore = 25.23;
        KiePMMLCharacteristics kiePMMLCharacteristics = new KiePMMLCharacteristics("NAME", Collections.emptyList(),
                                                                                   getKiePMMLCharacteristicList());
        final Map<String, Object> outputFieldsMap = new HashMap<>();
        Optional<Number> retrieved = kiePMMLCharacteristics.evaluate(Collections.emptyList(), Collections.emptyList()
                , Collections.emptyList(), Collections.emptyMap(),
                                                                     outputFieldsMap,
                                                                     initialScore,
                                                                     REASONCODE_ALGORITHM.POINTS_BELOW,
                                                                     true,
                                                                     0);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        Double EVALUATION_20 = baselineScore - value2;
        Double EVALUATION_11 = baselineScore - value1;
        Double expected = initialScore + value2 + value1 + 1;
        assertEquals(expected, retrieved.get());

        assertEquals(2, outputFieldsMap.size());
        assertTrue(outputFieldsMap.containsKey("REASON_CODE_20"));
        assertEquals(EVALUATION_20, outputFieldsMap.get("REASON_CODE_20"));
        assertTrue(outputFieldsMap.containsKey("REASON_CODE_11"));
        assertEquals(EVALUATION_11, outputFieldsMap.get("REASON_CODE_11"));
    }

    private List<KiePMMLCharacteristic> getKiePMMLCharacteristicList() {
        List<KiePMMLCharacteristic> toReturn = new ArrayList<>();
        // <Characteristic name="CUSTOM_FIELD_0" baselineScore="123" reasonCode="REASONCODE_0">
        //   <Attribute partialScore="100" reasonCode="REASON_CODE_10" >
        //     <False/>
        //   </Attribute>
        //   <Attribute partialScore="5" reasonCode="REASON_CODE_20" >
        //     <True/>
        //   </Attribute>
        // </Characteristic>
        KiePMMLFalsePredicate falsePredicate = KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        KiePMMLTruePredicate truePredicate = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        KiePMMLAttribute attribute00 = KiePMMLAttribute.builder(ATTRIBUTE_1 + 0, Collections.emptyList(),
                                                                falsePredicate)
                .withPartialScore(value1)
                .withReasonCode(REASON_CODE_1 + 0)
                .build();
        KiePMMLAttribute attribute01 = KiePMMLAttribute.builder(ATTRIBUTE_2 + 0, Collections.emptyList(),
                                                                truePredicate)
                .withPartialScore(value2)
                .withReasonCode(REASON_CODE_2 + 0)
                .build();
        toReturn.add(KiePMMLCharacteristic.builder(CUSTOM_FIELD + 0,
                                                   Collections.emptyList(),
                                                   Arrays.asList(attribute00,
                                                                 attribute01))
                             .withBaselineScore(baselineScore)
                             .withReasonCode(REASON_CODE + 0)
                             .build());
        // <Characteristic name="CUSTOM_FIELD_1" baselineScore="124" reasonCode="REASONCODE_1">
        //   <Attribute partialScore="101" reasonCode="REASON_CODE_11">
        //     <True/>
        //   </Attribute>
        //   <Attribute partialScore="6" reasonCode="REASON_CODE_21">
        //     <False/>
        //   </Attribute>
        // </Characteristic>
        KiePMMLAttribute attribute10 = KiePMMLAttribute.builder(ATTRIBUTE_1 + 1, Collections.emptyList(),
                                                                truePredicate)
                .withPartialScore(value1+ 1)
                .withReasonCode(REASON_CODE_1 + 1)
                .build();
        KiePMMLAttribute attribute11 = KiePMMLAttribute.builder(ATTRIBUTE_2 + 1, Collections.emptyList(),
                                                               falsePredicate)
                .withPartialScore(value2+ 1)
                .withReasonCode(REASON_CODE_2 + 1)
                .build();
        toReturn.add(KiePMMLCharacteristic.builder(CUSTOM_FIELD + 1,
                                                   Collections.emptyList(),
                                                   Arrays.asList(attribute10,
                                                                 attribute11))
                             .withBaselineScore(baselineScore+ 1)
                             .withReasonCode(REASON_CODE + 1)
                             .build());
        return toReturn;
    }

}