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

package org.kie.pmml.models.scorecard.compiler.factories;

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
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLAttribute;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristic;
import org.kie.pmml.models.scorecard.model.KiePMMLComplexPartialScore;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimpleSetPredicateFactoryTest.getSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getSimplePredicate;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getStringObjects;

public class KiePMMLCharacteristicFactoryTest {

    private static final String REASON_CODE = "REASON_CODE";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final SimplePredicate.Operator operator1 = SimplePredicate.Operator.EQUAL;
    private static final SimplePredicate.Operator operator2 = SimplePredicate.Operator.GREATER_THAN;

    @Test
    public void getAttributeVariableDeclarationWithComplexPartialScore() {
        final String variableName = "variableName";
        Array.Type arrayType = Array.Type.STRING;
        List<String> values1 = getStringObjects(arrayType, 4);
        Attribute attribute1 = getAttribute(values1, 1);
        List<String> values2 = getStringObjects(arrayType, 4);
        Attribute attribute2 = getAttribute(values2, 2);

        CompoundPredicate compoundPredicate1 = (CompoundPredicate) attribute1.getPredicate();
        CompoundPredicate compoundPredicate2 = (CompoundPredicate) attribute2.getPredicate();
        DataDictionary dataDictionary = new DataDictionary();
        for (Predicate predicate : compoundPredicate1.getPredicates()) {
            DataField toAdd = null;
            if (predicate instanceof SimplePredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimplePredicate)predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            } else if (predicate instanceof SimpleSetPredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimpleSetPredicate)predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            }
            if (toAdd != null) {
                dataDictionary.addDataFields(toAdd);
            }
        }
        for (Predicate predicate : compoundPredicate2.getPredicates()) {
            DataField toAdd = null;
            if (predicate instanceof SimplePredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimplePredicate)predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            } else if (predicate instanceof SimpleSetPredicate) {
                toAdd = new DataField();
                toAdd.setName(((SimpleSetPredicate)predicate).getField());
                toAdd.setDataType(DataType.DOUBLE);
            }
            if (toAdd != null) {
                dataDictionary.addDataFields(toAdd);
            }
        }


        String valuesString1 = values1.stream()
                .map(valueString -> "\"" + valueString + "\"")
                .collect(Collectors.joining(","));
        String valuesString2 = values2.stream()
                .map(valueString -> "\"" + valueString + "\"")
                .collect(Collectors.joining(","));
        Characteristic characteristic = new Characteristic();
        characteristic.addAttributes(attribute1, attribute2);
        characteristic.setBaselineScore(22);
        characteristic.setReasonCode(REASON_CODE);


        BlockStmt retrieved = KiePMMLCharacteristicFactory.getCharacteristicVariableDeclaration(variableName, characteristic, Collections.emptyList(), dataDictionary);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLSimplePredicate %1$s_0_Predicate_0 = " +
                                                  "KiePMMLSimplePredicate.builder(\"PARAM_1\", Collections.emptyList" +
                                                  "(), org.kie.pmml.api.enums.OPERATOR.EQUAL).withValue(100.0).build" +
                                                  "();\n" +
                                                  "    KiePMMLSimplePredicate %1$s_0_Predicate_1 = " +
                                                  "KiePMMLSimplePredicate.builder(\"PARAM_2\", Collections.emptyList" +
                                                  "(), org.kie.pmml.api.enums.OPERATOR.GREATER_THAN).withValue(5.0)" +
                                                  ".build();\n" +
                                                  "    KiePMMLSimpleSetPredicate %1$s_0_Predicate_2 = " +
                                                  "KiePMMLSimpleSetPredicate.builder(\"SIMPLESETPREDICATENAME\", " +
                                                  "Collections.emptyList(), org.kie.pmml.api.enums.ARRAY_TYPE.STRING," +
                                                  " org.kie.pmml.api.enums.IN_NOTIN.IN).withValues(Arrays.asList" +
                                                  "(%2$s)).build();\n" +
                                                  "    KiePMMLCompoundPredicate %1$s_0_Predicate = " +
                                                  "KiePMMLCompoundPredicate.builder(Collections.emptyList(), org.kie" +
                                                  ".pmml.api.enums.BOOLEAN_OPERATOR.AND).withKiePMMLPredicates(Arrays" +
                                                  ".asList(%1$s_0_Predicate_0, %1$s_0_Predicate_1, " +
                                                  "%1$s_0_Predicate_2)).build();\n" +
                                                  "    KiePMMLConstant %1$s_0_ComplexPartialScore_0 = new " +
                                                  "KiePMMLConstant(\"%1$s_0_ComplexPartialScore_0\", " +
                                                  "Collections.emptyList(), 100.0);\n" +
                                                  "    KiePMMLComplexPartialScore %1$s_0_ComplexPartialScore " +
                                                  "= new KiePMMLComplexPartialScore" +
                                                  "(\"%1$s_0_ComplexPartialScore\", Collections.emptyList(), " +
                                                  "%1$s_0_ComplexPartialScore_0);\n" +
                                                  "    KiePMMLAttribute %1$s_0 = KiePMMLAttribute.builder" +
                                                  "(\"%1$s_0\", Collections.emptyList(), " +
                                                  "%1$s_0_Predicate).withPartialScore(null)" +
                                                  ".withComplexPartialScore(%1$s_0_ComplexPartialScore).build" +
                                                  "();\n" +
                                                  "    KiePMMLSimplePredicate %1$s_1_Predicate_0 = " +
                                                  "KiePMMLSimplePredicate.builder(\"PARAM_1\", Collections.emptyList" +
                                                  "(), org.kie.pmml.api.enums.OPERATOR.EQUAL).withValue(100.0).build" +
                                                  "();\n" +
                                                  "    KiePMMLSimplePredicate %1$s_1_Predicate_1 = " +
                                                  "KiePMMLSimplePredicate.builder(\"PARAM_2\", Collections.emptyList" +
                                                  "(), org.kie.pmml.api.enums.OPERATOR.GREATER_THAN).withValue(5.0)" +
                                                  ".build();\n" +
                                                  "    KiePMMLSimpleSetPredicate %1$s_1_Predicate_2 = " +
                                                  "KiePMMLSimpleSetPredicate.builder(\"SIMPLESETPREDICATENAME\", " +
                                                  "Collections.emptyList(), org.kie.pmml.api.enums.ARRAY_TYPE.STRING," +
                                                  " org.kie.pmml.api.enums.IN_NOTIN.IN).withValues(Arrays.asList" +
                                                  "(%3$s)).build();\n" +
                                                  "    KiePMMLCompoundPredicate %1$s_1_Predicate = " +
                                                  "KiePMMLCompoundPredicate.builder(Collections.emptyList(), org.kie" +
                                                  ".pmml.api.enums.BOOLEAN_OPERATOR.AND).withKiePMMLPredicates(Arrays" +
                                                  ".asList(%1$s_1_Predicate_0, %1$s_1_Predicate_1, " +
                                                  "%1$s_1_Predicate_2)).build();\n" +
                                                  "    KiePMMLConstant %1$s_1_ComplexPartialScore_0 = new " +
                                                  "KiePMMLConstant(\"%1$s_1_ComplexPartialScore_0\", " +
                                                  "Collections.emptyList(), 100.0);\n" +
                                                  "    KiePMMLComplexPartialScore %1$s_1_ComplexPartialScore " +
                                                  "= new KiePMMLComplexPartialScore" +
                                                  "(\"%1$s_1_ComplexPartialScore\", Collections.emptyList(), " +
                                                  "%1$s_1_ComplexPartialScore_0);\n" +
                                                  "    KiePMMLAttribute %1$s_1 = KiePMMLAttribute.builder" +
                                                  "(\"%1$s_1\", Collections.emptyList(), " +
                                                  "%1$s_1_Predicate).withPartialScore(null)" +
                                                  ".withComplexPartialScore(%1$s_1_ComplexPartialScore).build" +
                                                  "();\n" +
                                                  "    KiePMMLCharacteristic %1$s = KiePMMLCharacteristic" +
                                                  ".builder(\"%1$s\", Collections.emptyList(), Arrays.asList" +
                                                  "(%1$s_0, %1$s_1)).withBaselineScore(%4$s)" +
                                                  ".withReasonCode(\"%5$s\").build();\n" +
                                                  "}", variableName,
                                          valuesString1,
                                          valuesString2,
                                          characteristic.getBaselineScore(),
                                          characteristic.getReasonCode()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLAttribute.class,
                                               KiePMMLCharacteristic.class,
                                               KiePMMLComplexPartialScore.class,
                                               KiePMMLCompoundPredicate.class,
                                               KiePMMLConstant.class,
                                               KiePMMLSimplePredicate.class,
                                               KiePMMLSimpleSetPredicate.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private Attribute getAttribute(List<String> values, int id) {
        Attribute toReturn = new Attribute();
        toReturn.setReasonCode(REASON_CODE+id);
        Array.Type arrayType = Array.Type.STRING;
        toReturn.setPredicate(getCompoundPredicate(values, arrayType));
        toReturn.setComplexPartialScore(getComplexPartialScore());
       return toReturn;
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