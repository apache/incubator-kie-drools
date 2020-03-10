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

package org.kie.pmml.models.tree.model.predicates;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.models.tree.model.enums.BOOLEAN_OPERATOR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class KiePMMLCompoundPredicateTest {

    private final static List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private final static BOOLEAN_OPERATOR _BOOLEAN_OPERATOR = BOOLEAN_OPERATOR.OR;
    private final static List<KiePMMLPredicate> PREDICATES = new ArrayList<>();

    @Test
    public void builder() {
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder("CompoundPredicate", EXTENSIONS, _BOOLEAN_OPERATOR)
                .build();
        assertNotNull(retrieved);
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertEquals(_BOOLEAN_OPERATOR, retrieved.getBooleanOperator());
        assertNull(retrieved.getKiePMMLPredicates());
        retrieved = KiePMMLCompoundPredicate.builder("CompoundPredicate", EXTENSIONS, _BOOLEAN_OPERATOR)
                .withKiePMMLPredicates(PREDICATES)
                .build();
        assertNotNull(retrieved);
        assertEquals(EXTENSIONS, retrieved.getExtensions());
        assertEquals(_BOOLEAN_OPERATOR, retrieved.getBooleanOperator());
        assertEquals(PREDICATES, retrieved.getKiePMMLPredicates());
    }

    // TODO {gcardosi} re-implement with native drools rules
    /*@Test
    public void evaluateByName() {
        final KiePMMLSimplePredicate temperature = KiePMMLSimplePredicate.builder("TEMPERATURE", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(36.9).build();
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder(EXTENSIONS, BOOLEAN_OPERATOR.AND)
                .withKiePMMLPredicates(Collections.singletonList(temperature))
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("TEMPERATURE",  36.4)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("NEW_FIELD",  36.4)), true, false);
    }

    @Test
    public void evaluateSingleNumber() {
        final KiePMMLSimplePredicate temperature = KiePMMLSimplePredicate.builder("TEMPERATURE", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(36.9).build();
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder(EXTENSIONS, BOOLEAN_OPERATOR.AND)
                .withKiePMMLPredicates(Collections.singletonList(temperature))
                .build();
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("TEMPERATURE",  37)), false, true);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("TEMPERATURE",  36.9)), false, false);
        commonEvaluate(retrieved.evaluate(Collections.singletonMap("TEMPERATURE",  36.4)), false, false);
    }

    @Test
    public void evaluateNumbersAND() {
        final KiePMMLSimplePredicate temperature = KiePMMLSimplePredicate.builder("TEMPERATURE", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(36.9).build();
        final KiePMMLSimplePredicate hr = KiePMMLSimplePredicate.builder("HR", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(80).build();
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder(EXTENSIONS, BOOLEAN_OPERATOR.AND)
                .withKiePMMLPredicates(Arrays.asList(temperature, hr))
                .build();
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 37, "HR" , 81)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 36.9, "HR" , 81)), false, false);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 38, "HR" , 80)), false, false);
    }

    @Test
    public void evaluateNumbersOR() {
        final KiePMMLSimplePredicate temperature = KiePMMLSimplePredicate.builder("TEMPERATURE", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(36.9).build();
        final KiePMMLSimplePredicate hr = KiePMMLSimplePredicate.builder("HR", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(80).build();
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder(EXTENSIONS, BOOLEAN_OPERATOR.OR)
                .withKiePMMLPredicates(Arrays.asList(temperature, hr))
                .build();
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 37, "HR" , 81)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 36.9, "HR" , 81)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 38, "HR" , 80)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 36.9, "HR" , 80)), false, false);
    }

    @Test
    public void evaluateNumbersXOR() {
        final KiePMMLSimplePredicate temperature = KiePMMLSimplePredicate.builder("TEMPERATURE", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(36.9).build();
        final KiePMMLSimplePredicate hr = KiePMMLSimplePredicate.builder("HR", EXTENSIONS, OPERATOR.GREATER_THAN).withValue(80).build();
        KiePMMLCompoundPredicate retrieved = KiePMMLCompoundPredicate.builder(EXTENSIONS, BOOLEAN_OPERATOR.XOR)
                .withKiePMMLPredicates(Arrays.asList(temperature, hr))
                .build();
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 37, "HR" , 81)), false, false);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 36.9, "HR" , 81)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 38, "HR" , 80)), false, true);
        commonEvaluate(retrieved.evaluate(mapOf("TEMPERATURE", 36.9, "HR" , 80)), false, false);
    }*/
}