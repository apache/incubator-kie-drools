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

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getDATA_TYPEString;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getOP_TYPEString;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLDefineFunctionFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final String TEST_01_SOURCE = "KiePMMLDefineFunctionFactoryTest_01.txt";

    @Test
    void getDefineFunctionVariableDeclaration() throws IOException {
        ParameterField parameterField1 = new ParameterField(PARAM_1);
        parameterField1.setDataType(DataType.DOUBLE);
        parameterField1.setOpType(OpType.CONTINUOUS);
        parameterField1.setDisplayName("displayName1");
        ParameterField parameterField2 = new ParameterField(PARAM_2);
        parameterField2.setDataType(DataType.DOUBLE);
        parameterField2.setOpType(OpType.CONTINUOUS);
        parameterField2.setDisplayName("displayName2");
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF");
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        DefineFunction defineFunction = new DefineFunction();
        defineFunction.setName(CUSTOM_FUNCTION);
        defineFunction.addParameterFields(parameterField1, parameterField2);
        defineFunction.setDataType(DataType.DOUBLE);
        defineFunction.setOpType(OpType.CONTINUOUS);
        defineFunction.setExpression(apply);
        String dataType1 = getDATA_TYPEString(parameterField1.getDataType());
        String dataType2 = getDATA_TYPEString(parameterField2.getDataType());
        String dataType3 = getDATA_TYPEString(defineFunction.getDataType());
        String opType1 = getOP_TYPEString(parameterField1.getOpType());
        String opType2 = getOP_TYPEString(parameterField2.getOpType());
        String opType3 = getOP_TYPEString(defineFunction.getOpType());
        BlockStmt retrieved = KiePMMLDefineFunctionFactory.getDefineFunctionVariableDeclaration(defineFunction);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils
                .parseBlock(String.format(text,parameterField1.getName(),
                                          dataType1,
                                          opType1,
                                          parameterField1.getDisplayName(),parameterField2.getName(),
                                          dataType2,
                                          opType2,
                                          parameterField2.getDisplayName(),
                                          constant.getValue(),fieldRef.getField(),
                                          apply.getFunction(),
                                          apply.getInvalidValueTreatment().value(),
                                          dataType3,
                                          opType3));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLParameterField.class,
                                               KiePMMLConstant.class,
                                               KiePMMLFieldRef.class,
                                               KiePMMLApply.class,
                                               KiePMMLDefineFunction.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}