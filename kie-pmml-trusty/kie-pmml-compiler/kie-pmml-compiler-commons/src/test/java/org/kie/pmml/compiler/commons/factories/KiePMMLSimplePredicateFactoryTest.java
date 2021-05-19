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

package org.kie.pmml.compiler.commons.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.SimplePredicate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getActualValue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomValue;

public class KiePMMLSimplePredicateFactoryTest {

    private static Map<String, DataType> simplePredicateNameType;
    private static List<SimplePredicate> simplePredicates;
    private static DataDictionary dataDictionary;

    @BeforeClass
    public static void setup() {
        simplePredicateNameType = new HashMap<>();
        simplePredicateNameType.put("age", DataType.INTEGER);
        simplePredicateNameType.put("weight", DataType.DOUBLE);
        simplePredicateNameType.put("name", DataType.STRING);
        simplePredicateNameType.put("runner", DataType.BOOLEAN);
        simplePredicates = simplePredicateNameType
                .entrySet()
                .stream()
                .map(entry -> PMMLModelTestUtils.getSimplePredicate(entry.getKey(),
                                                                    getRandomValue(entry.getValue()),
                                                                    getRandomSimplePredicateOperator()))
                .collect(Collectors.toList());
        List<DataField> dataFields = new ArrayList<>();
        simplePredicateNameType.forEach((name, dataType) -> {
            DataField toAdd = new DataField();
            toAdd.setName(FieldName.create(name));
            toAdd.setDataType(dataType);
            dataFields.add(toAdd);
        });
        dataDictionary = getDataDictionary(dataFields);
    }

    @Test
    public void getSimplePredicateBody() {
        simplePredicates.forEach(simplePredicate -> {
            final DataType dataType = simplePredicateNameType.get(simplePredicate.getField().getValue());
            final BlockStmt retrieved = KiePMMLSimplePredicateFactory.getSimplePredicateBody(simplePredicate, dataType);
            commonVerifySimplePredicate(retrieved, simplePredicate, dataType);
        });
    }

    private void commonVerifySimplePredicate(final BlockStmt toVerify, final SimplePredicate source, final DataType dataType) {
        OPERATOR kiePMMLOperator = OPERATOR.byName(source.getOperator().value());
        Object value = getActualValue(source.getValue(), dataType);
        BlockStmt  expected;
        if (kiePMMLOperator.isValueOperator() && value == null) {
            expected = JavaParserUtils.parseBlock("{" +
                    "    return false;" +
                    "}");
            JavaParserUtils.equalsNode(expected, toVerify);
        } else if (kiePMMLOperator.isOnlyNumberOperator() && value != null && !(value instanceof Number)) {
            expected = JavaParserUtils.parseBlock("{" +
                    "    return false;" +
                    "}");
            JavaParserUtils.equalsNode(expected, toVerify);
        } else {
            String expectedString = source.getField().getValue();
            assertTrue(toVerify.toString().contains(expectedString));
            assertFalse(toVerify.toString().contains("avalue"));
        }
    }

}