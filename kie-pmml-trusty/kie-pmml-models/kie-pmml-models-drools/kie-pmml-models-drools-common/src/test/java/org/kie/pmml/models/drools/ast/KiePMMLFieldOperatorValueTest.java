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
package org.kie.pmml.models.drools.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLFieldOperatorValueTest {

    private static final String NAME = "NAME";
    private static final BOOLEAN_OPERATOR BOOLEANOPERATOR = BOOLEAN_OPERATOR.SURROGATE;

    @Test
    void getConstraintsAsString() {
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithName();
        String expected = "value < 35 surrogate value > 85";
        String retrieved = kiePMMLFieldOperatorValue.getConstraintsAsString();
        assertThat(retrieved).isEqualTo(expected);
        kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithoutName();
        expected = "value < 35 surrogate value > 85";
        retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void buildConstraintsString() {
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithName();
        String expected = "value < 35 surrogate value > 85";
        String retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertThat(retrieved).isEqualTo(expected);
        kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithoutName();
        expected = "value < 35 surrogate value > 85";
        retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertThat(retrieved).isEqualTo(expected);
    }

    private KiePMMLFieldOperatorValue getKiePMMLFieldOperatorValueWithName() {
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35),
                                                                         new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85));
        return new KiePMMLFieldOperatorValue(NAME, BOOLEANOPERATOR, kiePMMLOperatorValues, Collections.emptyList());
    }

    private KiePMMLFieldOperatorValue getKiePMMLFieldOperatorValueWithoutName() {
        String humidityField = "HUMIDITY";
        final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues = Arrays
                .asList(new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 56)), null),
                        new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 91)), null));
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35),
                                                                         new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85));

        return new KiePMMLFieldOperatorValue(null, BOOLEANOPERATOR, kiePMMLOperatorValues, nestedKiePMMLFieldOperatorValues);
    }
}