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

package org.kie.pmml.models.drools.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

import static org.junit.Assert.assertEquals;

public class KiePMMLFieldOperatorValueTest {

    private static final String NAME = "NAME";
    private static final String OPERATOR = "OPERATOR";

    @Test
    public void getConstraintsAsString() {
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithName();
        String expected = "value < 35 OPERATOR value > 85";
        String retrieved = kiePMMLFieldOperatorValue.getConstraintsAsString();
        assertEquals(expected, retrieved);
        kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithoutName();
        expected = "value < 35 OPERATOR value > 85";
        retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertEquals(expected, retrieved);
    }

    @Test
    public void buildConstraintsString() {
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithName();
        String expected = "value < 35 OPERATOR value > 85";
        String retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertEquals(expected, retrieved);
        kiePMMLFieldOperatorValue = getKiePMMLFieldOperatorValueWithoutName();
        expected = "value < 35 OPERATOR value > 85";
        retrieved = kiePMMLFieldOperatorValue.buildConstraintsString();
        assertEquals(expected, retrieved);
    }

    private KiePMMLFieldOperatorValue getKiePMMLFieldOperatorValueWithName() {
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35),
                                                                         new KiePMMLOperatorValue(">", 85));
        return new KiePMMLFieldOperatorValue(NAME, OPERATOR, kiePMMLOperatorValues, Collections.emptyList());
    }

    private KiePMMLFieldOperatorValue getKiePMMLFieldOperatorValueWithoutName() {
        String humidityField = "HUMIDITY";
        final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues = Arrays
                .asList(new KiePMMLFieldOperatorValue(humidityField, "or", Collections.singletonList(new KiePMMLOperatorValue("<", 56)), null),
                        new KiePMMLFieldOperatorValue(humidityField, "or", Collections.singletonList(new KiePMMLOperatorValue(">", 91)), null));
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35),
                                                                         new KiePMMLOperatorValue(">", 85));

        return new KiePMMLFieldOperatorValue(null, OPERATOR, kiePMMLOperatorValues, nestedKiePMMLFieldOperatorValues);
    }
}