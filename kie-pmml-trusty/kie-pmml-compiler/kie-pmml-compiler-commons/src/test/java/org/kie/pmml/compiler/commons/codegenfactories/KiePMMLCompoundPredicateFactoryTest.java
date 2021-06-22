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

package org.kie.pmml.compiler.commons.codegenfactories;

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
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.junit.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLSimpleSetPredicateFactoryTest.getSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getSimplePredicate;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getStringObjects;

public class KiePMMLCompoundPredicateFactoryTest {

    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final SimplePredicate.Operator operator1 = SimplePredicate.Operator.EQUAL;
    private static final SimplePredicate.Operator operator2 = SimplePredicate.Operator.GREATER_THAN;

    @Test
    public void getCompoundPredicateVariableDeclaration() {
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

        BlockStmt retrieved = KiePMMLCompoundPredicateFactory.getCompoundPredicateVariableDeclaration(variableName,
                                                                                                      compoundPredicate, Collections.emptyList(), dataDictionary);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLSimplePredicate " +
                                                                              "%1$s_0 = " +
                                                                              "KiePMMLSimplePredicate.builder" +
                                                                              "(\"PARAM_1\", Collections.emptyList()," +
                                                                              " org.kie.pmml.api.enums.OPERATOR" +
                                                                              ".EQUAL).withValue(100.0).build();\n" +
                                                                              "    KiePMMLSimplePredicate " +
                                                                              "%1$s_1 = " +
                                                                              "KiePMMLSimplePredicate.builder" +
                                                                              "(\"PARAM_2\", Collections.emptyList()," +
                                                                              " org.kie.pmml.api.enums.OPERATOR" +
                                                                              ".GREATER_THAN).withValue(5.0).build();" +
                                                                              "\n" +
                                                                              "    KiePMMLSimpleSetPredicate " +
                                                                              "%1$s_2 = " +
                                                                              "KiePMMLSimpleSetPredicate.builder" +
                                                                              "(\"SIMPLESETPREDICATENAME\", " +
                                                                              "Collections.emptyList(), org.kie.pmml" +
                                                                              ".api.enums.ARRAY_TYPE.STRING, org.kie" +
                                                                              ".pmml.api.enums.IN_NOTIN.IN)" +
                                                                              ".withValues(Arrays.asList(%2$s))" +
                                                                              ".build();\n" +
                                                                              "    KiePMMLCompoundPredicate " +
                                                                              "%1$s = " +
                                                                              "KiePMMLCompoundPredicate.builder" +
                                                                              "(Collections.emptyList(), %3$s)" +
                                                                              ".withKiePMMLPredicates(Arrays.asList" +
                                                                              "(%1$s_0, %1$s_1, " +
                                                                              "%1$s_2)).build();\n" +
                                                                              "}", variableName,
                                                                      valuesString,
                                                                      booleanOperatorString));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLCompoundPredicate.class, KiePMMLSimplePredicate.class, KiePMMLSimpleSetPredicate.class, Arrays.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }


}