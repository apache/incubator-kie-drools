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

package org.kie.pmml.compiler.commons.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Array;
import org.dmg.pmml.SimpleSetPredicate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getObjectsFromArray;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimpleSetPredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getStringObjects;

public class KiePMMLSimpleSetPredicateFactoryTest {

    private static Map<String, Array.Type> simpleSetPredicateNameType;
    private static List<SimpleSetPredicate> simpleSetPredicates;

    @BeforeClass
    public static void setup() {
        simpleSetPredicateNameType = new HashMap<>();
        simpleSetPredicateNameType.put("age", Array.Type.INT);
        simpleSetPredicateNameType.put("weight", Array.Type.REAL);
        simpleSetPredicateNameType.put("name", Array.Type.STRING);
        simpleSetPredicates = simpleSetPredicateNameType
                .entrySet()
                .stream()
                .map(entry -> {
                    List<String> values = getStringObjects(entry.getValue(), 4);
                    return PMMLModelTestUtils.getSimpleSetPredicate(entry.getKey(),
                                                                    entry.getValue(),
                                                                    values,
                                                                    getRandomSimpleSetPredicateOperator());
                })
                .collect(Collectors.toList());
    }

    @Test
    public void getSimpleSetPredicateBody() {
        simpleSetPredicates.forEach(simpleSetPredicate -> {
            final BlockStmt retrieved = KiePMMLSimpleSetPredicateFactory.getSimpleSetPredicateBody(simpleSetPredicate);
            commonVerifySimpleSetPredicate(retrieved, simpleSetPredicate);
        });
    }

    private void commonVerifySimpleSetPredicate(final BlockStmt toVerify, final SimpleSetPredicate source) {
        String expected = "";
        if (source.getBooleanOperator().equals(SimpleSetPredicate.BooleanOperator.IS_IN)) {
            expected = "return values.contains(value);";
        } else if (source.getBooleanOperator().equals(SimpleSetPredicate.BooleanOperator.IS_NOT_IN)) {
            expected = "return !values.contains(value);";
        } else {
            fail("Unexpected BooleanOperator " + source.getBooleanOperator());
        }
        final String blockString = toVerify.toString();
        assertTrue(blockString.contains(expected));
        final List<Object> values = getObjectsFromArray(source.getArray());
        ARRAY_TYPE arrayType = ARRAY_TYPE.byName(source.getArray().getType().value());
        String expectedValues = values.stream()
                .map(value -> {
                    if (arrayType.equals(ARRAY_TYPE.STRING)) {
                        return String.format("\"%s\"", value.toString());
                    } else {
                        return value.toString();
                    }
                })
                .collect(Collectors.joining(", "));
        expected = String.format("List values = java.util.Arrays.asList(%s);", expectedValues);
        assertTrue(blockString.contains(expected));
        expected = source.getField().getValue();
        assertTrue(blockString.contains(expected));
        assertFalse(blockString.contains("avalue"));
    }
}