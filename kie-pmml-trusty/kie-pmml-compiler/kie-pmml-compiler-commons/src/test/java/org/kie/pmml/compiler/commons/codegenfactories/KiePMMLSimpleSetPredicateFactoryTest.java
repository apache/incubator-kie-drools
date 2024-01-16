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
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.SimpleSetPredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getArray;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getStringObjects;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLSimpleSetPredicateFactoryTest {

    private final static String SIMPLE_SET_PREDICATE_NAME = "SIMPLESETPREDICATENAME";
    private static final String TEST_01_SOURCE = "KiePMMLSimpleSetPredicateFactoryTest_01.txt";

    @Test
    void getSimpleSetPredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        Array.Type arrayType = Array.Type.STRING;
        List<String> values = getStringObjects(arrayType, 4);
        SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate(values, arrayType,
                                                                      SimpleSetPredicate.BooleanOperator.IS_IN);
        String arrayTypeString =
                ARRAY_TYPE.class.getName() + "." + ARRAY_TYPE.byName(simpleSetPredicate.getArray().getType().value());
        String booleanOperatorString =
                IN_NOTIN.class.getName() + "." + IN_NOTIN.byName(simpleSetPredicate.getBooleanOperator().value());

        String valuesString = values.stream()
                .map(valueString -> "\"" + valueString + "\"")
                .collect(Collectors.joining(","));

        DataField dataField = new DataField();
        dataField.setName(simpleSetPredicate.getField());
        dataField.setDataType(DataType.DOUBLE);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        BlockStmt retrieved = KiePMMLSimpleSetPredicateFactory.getSimpleSetPredicateVariableDeclaration(variableName,
                                                                                                        simpleSetPredicate);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,simpleSetPredicate.getField(),
                                                                      arrayTypeString,
                                                                      booleanOperatorString,
                                                                      valuesString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLSimpleSetPredicate.class, Arrays.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    public static SimpleSetPredicate getSimpleSetPredicate(List<String> values, final Array.Type arrayType,
                                                           final SimpleSetPredicate.BooleanOperator inNotIn) {
        Array array = getArray(arrayType, values);
        SimpleSetPredicate toReturn = new SimpleSetPredicate();
        toReturn.setField(SIMPLE_SET_PREDICATE_NAME);
        toReturn.setBooleanOperator(inNotIn);
        toReturn.setArray(array);
        return toReturn;
    }
}