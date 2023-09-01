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
package org.kie.pmml.models.scorecard.compiler.factories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLAttribute;
import org.kie.pmml.models.scorecard.model.KiePMMLComplexPartialScore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getSimplePredicate;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getStringObjects;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimpleSetPredicateFactoryTest.getSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLAttributeFactoryTest {

    private static final String REASON_CODE = "REASON_CODE";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final SimplePredicate.Operator operator1 = SimplePredicate.Operator.EQUAL;
    private static final SimplePredicate.Operator operator2 = SimplePredicate.Operator.GREATER_THAN;
    private static final String TEST_01_SOURCE = "KiePMMLAttributeFactoryTest_01.txt";

    @Test
    void getAttributeVariableDeclarationWithComplexPartialScore() throws IOException {
        final String variableName = "variableName";
        Attribute attribute = new Attribute();
        attribute.setReasonCode(REASON_CODE);
        Array.Type arrayType = Array.Type.STRING;
        List<String> values = getStringObjects(arrayType, 4);
        CompoundPredicate compoundPredicate = getCompoundPredicate(values, arrayType);
        attribute.setPredicate(compoundPredicate);
        attribute.setComplexPartialScore(getComplexPartialScore());
        String valuesString = values.stream()
                .map(valueString -> "\"" + valueString + "\"")
                .collect(Collectors.joining(","));
        DataDictionary dataDictionary = new DataDictionary();
        for (Predicate predicate : compoundPredicate.getPredicates()) {
            DataField toAdd = null;
            if (predicate instanceof SimplePredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimplePredicate) predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            } else if (predicate instanceof SimpleSetPredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimpleSetPredicate) predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            }
            if (toAdd != null) {
                dataDictionary.addDataFields(toAdd);
            }
        }

        BlockStmt retrieved = KiePMMLAttributeFactory.getAttributeVariableDeclaration(variableName, attribute,
                getFieldsFromDataDictionary(dataDictionary));
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, valuesString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLAttribute.class,
                KiePMMLComplexPartialScore.class,
                KiePMMLCompoundPredicate.class,
                KiePMMLConstant.class,
                KiePMMLSimplePredicate.class,
                KiePMMLSimpleSetPredicate.class,
                Arrays.class,
                Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private CompoundPredicate getCompoundPredicate(List<String> values, Array.Type arrayType) {
        SimplePredicate simplePredicate1 = getSimplePredicate(PARAM_1, value1, operator1);
        SimplePredicate simplePredicate2 = getSimplePredicate(PARAM_2, value2, operator2);
        SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate(values, arrayType,
                                                                      SimpleSetPredicate.BooleanOperator.IS_IN);
        CompoundPredicate toReturn = new CompoundPredicate();
        toReturn.setBooleanOperator(CompoundPredicate.BooleanOperator.AND);
        toReturn.getPredicates().add(0, simplePredicate1);
        toReturn.getPredicates().add(1, simplePredicate2);
        toReturn.getPredicates().add(2, simpleSetPredicate);
        return toReturn;
    }

    private ComplexPartialScore getComplexPartialScore() {
        Constant constant = new Constant();
        constant.setValue(value1);
        ComplexPartialScore toReturn = new ComplexPartialScore();
        toReturn.setExpression(constant);
        return toReturn;
    }
}