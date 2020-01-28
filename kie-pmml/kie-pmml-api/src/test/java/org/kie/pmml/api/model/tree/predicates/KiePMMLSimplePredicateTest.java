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

package org.kie.pmml.api.model.tree.predicates;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.tree.enums.OPERATOR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class KiePMMLSimplePredicateTest {

    private final static String NAME = "NAME";
    private final static List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private final static OPERATOR _OPERATOR = OPERATOR.GREATER_OR_EQUAL;
    private final static Object VALUE = "VALUE";

    @Test
    public void builder() {
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, _OPERATOR)
                .build();
        assertNotNull(retrieved);
        assertEquals(NAME, retrieved.getName());
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertEquals(_OPERATOR, retrieved.getOperator());
        assertNull(retrieved.getValue());
        retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, _OPERATOR)
                .withValue(VALUE)
                .build();
        assertNotNull(retrieved);
        assertEquals(NAME, retrieved.getName());
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertEquals(_OPERATOR, retrieved.getOperator());
        assertEquals(VALUE, retrieved.getValue());
    }

    // TODO {gcardosi} re-implement with native drools rules
    /*@Test
    public void evaluateByFieldName() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, _OPERATOR)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("NEW_FIELD", 34)), true, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34)), false, false);
    }

    @Test
    public void evaluateEQUAL() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.EQUAL)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, false);

        retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.EQUAL)
                .withValue(VALUE)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, VALUE)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, "NEW_VALUE")), false, false);
    }

    @Test
    public void evaluateNOT_EQUAL() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.NOT_EQUAL)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, true);

        retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.NOT_EQUAL)
                .withValue(VALUE)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, VALUE)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, "NEW_VALUE")), false, true);
    }

    @Test
    public void evaluateNumberLESS_THAN() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.LESS_THAN)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, true);
    }

    @Test
    public void evaluateNumberLESS_OR_EQUAL() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.LESS_OR_EQUAL)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, true);
    }

    @Test
    public void evaluateNumberGREATER_THAN() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.GREATER_THAN)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, false);
    }

    @Test
    public void evaluateNumberGREATER_OR_EQUAL() {
        double doubleValue = 34.6;
        KiePMMLSimplePredicate retrieved = KiePMMLSimplePredicate.builder(NAME, EXTENSIONS, OPERATOR.GREATER_OR_EQUAL)
                .withValue(doubleValue)
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, doubleValue)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.7)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap(NAME, 34.5)), false, false);
    }*/

}