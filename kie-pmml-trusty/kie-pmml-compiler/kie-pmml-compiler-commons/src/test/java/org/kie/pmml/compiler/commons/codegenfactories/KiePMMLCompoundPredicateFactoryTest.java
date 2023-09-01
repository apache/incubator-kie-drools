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
package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Field;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getSimplePredicate;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getStringObjects;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimpleSetPredicateFactoryTest.getSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLCompoundPredicateFactoryTest {

    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final SimplePredicate.Operator operator1 = SimplePredicate.Operator.EQUAL;
    private static final SimplePredicate.Operator operator2 = SimplePredicate.Operator.GREATER_THAN;
    private static final String TEST_01_SOURCE = "KiePMMLCompoundPredicateFactoryTest_01.txt";

    @Test
    void getCompoundPredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        SimplePredicate simplePredicate1 = getSimplePredicate(PARAM_1, value1, operator1);
        SimplePredicate simplePredicate2 = getSimplePredicate(PARAM_2, value2, operator2);

        Array.Type arrayType = Array.Type.STRING;
        List<String> values = getStringObjects(arrayType, 4);
        SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate(values, arrayType,
                                                                      SimpleSetPredicate.BooleanOperator.IS_IN);
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.AND);
        compoundPredicate.getPredicates().add(0, simplePredicate1);
        compoundPredicate.getPredicates().add(1, simplePredicate2);
        compoundPredicate.getPredicates().add(2, simpleSetPredicate);

        DataField dataField1 = new DataField();
        dataField1.setName(simplePredicate1.getField());
        dataField1.setDataType(DataType.DOUBLE);
        DataField dataField2 = new DataField();
        dataField2.setName(simplePredicate2.getField());
        dataField2.setDataType(DataType.DOUBLE);
        DataField dataField3 = new DataField();
        dataField3.setName(simpleSetPredicate.getField());
        dataField3.setDataType(DataType.DOUBLE);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField1, dataField2, dataField3);

        String booleanOperatorString =
                BOOLEAN_OPERATOR.class.getName() + "." + BOOLEAN_OPERATOR.byName(compoundPredicate.getBooleanOperator().value()).name();
        String valuesString = values.stream()
                .map(valueString -> "\"" + valueString + "\"")
                .collect(Collectors.joining(","));

        final List<Field<?>> fields = getFieldsFromDataDictionary(dataDictionary);
        BlockStmt retrieved = KiePMMLCompoundPredicateFactory.getCompoundPredicateVariableDeclaration(variableName,
                                                                                                      compoundPredicate, fields);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,
                                                                      valuesString,
                                                                      booleanOperatorString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLCompoundPredicate.class, KiePMMLSimplePredicate.class,
                                               KiePMMLSimpleSetPredicate.class, Arrays.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}