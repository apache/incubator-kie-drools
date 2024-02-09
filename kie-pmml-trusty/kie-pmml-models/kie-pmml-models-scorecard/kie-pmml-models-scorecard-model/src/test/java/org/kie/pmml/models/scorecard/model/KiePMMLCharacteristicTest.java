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
package org.kie.pmml.models.scorecard.model;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLCharacteristicTest {

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
    void evaluateNoMatchingAttributes() {
        KiePMMLAttribute attribute1 = KiePMMLAttribute.builder(ATTRIBUTE_1, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .build();
        KiePMMLAttribute attribute2 = KiePMMLAttribute.builder(ATTRIBUTE_2, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value2)
                .build();
        KiePMMLCharacteristic kiePMMLCharacteristic = KiePMMLCharacteristic.builder(CUSTOM_FIELD, Collections.emptyList(), Arrays.asList(attribute1, attribute2))
                .withBaselineScore(baselineScore)
                .withReasonCode(REASON_CODE)
                .build();
        assertThat(kiePMMLCharacteristic.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                Collections.emptyMap())).isNull();
    }

    @Test
    void evaluateFirstMatchingAttributesNoAttributeReasonCode() {
        KiePMMLAttribute attribute1 = KiePMMLAttribute.builder(ATTRIBUTE_1, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .build();
        KiePMMLAttribute attribute2 = KiePMMLAttribute.builder(ATTRIBUTE_2, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value2)
                .build();
        KiePMMLCharacteristic kiePMMLCharacteristic = KiePMMLCharacteristic.builder(CUSTOM_FIELD, Collections.emptyList(), Arrays.asList(attribute1, attribute2))
                .withBaselineScore(baselineScore)
                .withReasonCode(REASON_CODE)
                .build();
        KiePMMLCharacteristic.ReasonCodeValue retrieved = kiePMMLCharacteristic.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getReasonCode()).isEqualTo(REASON_CODE);
        assertThat(retrieved.getScore()).isEqualTo(value1);
    }

    @Test
    void evaluateLastMatchingAttributesNoAttributeReasonCode() {
        KiePMMLAttribute attribute1 = KiePMMLAttribute.builder(ATTRIBUTE_1, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .build();
        KiePMMLAttribute attribute2 = KiePMMLAttribute.builder(ATTRIBUTE_2, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value2)
                .build();
        KiePMMLCharacteristic kiePMMLCharacteristic = KiePMMLCharacteristic.builder(CUSTOM_FIELD, Collections.emptyList(), Arrays.asList(attribute1, attribute2))
                .withBaselineScore(baselineScore)
                .withReasonCode(REASON_CODE)
                .build();
        KiePMMLCharacteristic.ReasonCodeValue retrieved = kiePMMLCharacteristic.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getReasonCode()).isEqualTo(REASON_CODE);
        assertThat(retrieved.getScore()).isEqualTo(value2);
    }

    @Test
    void evaluateFirstMatchingAttributesAttributeReasonCode() {
        KiePMMLAttribute attribute1 = KiePMMLAttribute.builder(ATTRIBUTE_1, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .withReasonCode(REASON_CODE_1)
                .build();
        KiePMMLAttribute attribute2 = KiePMMLAttribute.builder(ATTRIBUTE_2, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value2)
                .withReasonCode(REASON_CODE_2)
                .build();
        KiePMMLCharacteristic kiePMMLCharacteristic = KiePMMLCharacteristic.builder(CUSTOM_FIELD, Collections.emptyList(), Arrays.asList(attribute1, attribute2))
                .withBaselineScore(baselineScore)
                .withReasonCode(REASON_CODE)
                .build();
        KiePMMLCharacteristic.ReasonCodeValue retrieved = kiePMMLCharacteristic.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getReasonCode()).isEqualTo(REASON_CODE_1);
        assertThat(retrieved.getScore()).isEqualTo(value1);
    }

    @Test
    void evaluateLastMatchingAttributesAttributeReasonCode() {
        KiePMMLAttribute attribute1 = KiePMMLAttribute.builder(ATTRIBUTE_1, Collections.emptyList(), KiePMMLFalsePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value1)
                .withReasonCode(REASON_CODE_1)
                .build();
        KiePMMLAttribute attribute2 = KiePMMLAttribute.builder(ATTRIBUTE_2, Collections.emptyList(), KiePMMLTruePredicate.builder(Collections.emptyList()).build())
                .withPartialScore(value2)
                .withReasonCode(REASON_CODE_2)
                .build();
        KiePMMLCharacteristic kiePMMLCharacteristic = KiePMMLCharacteristic.builder(CUSTOM_FIELD, Collections.emptyList(), Arrays.asList(attribute1, attribute2))
                .withBaselineScore(baselineScore)
                .withReasonCode(REASON_CODE)
                .build();
        KiePMMLCharacteristic.ReasonCodeValue retrieved = kiePMMLCharacteristic.evaluate(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getReasonCode()).isEqualTo(REASON_CODE_2);
        assertThat(retrieved.getScore()).isEqualTo(value2);
    }
}