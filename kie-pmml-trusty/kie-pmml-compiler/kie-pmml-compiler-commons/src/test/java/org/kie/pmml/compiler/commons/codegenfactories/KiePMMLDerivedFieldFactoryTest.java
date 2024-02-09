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
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getDATA_TYPEString;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getOP_TYPEString;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLDerivedFieldFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final String TEST_01_SOURCE = "KiePMMLDerivedFieldFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLDerivedFieldFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLDerivedFieldFactoryTest_03.txt";

    @Test
    void getDerivedFieldVariableDeclarationWithConstant() throws IOException {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        DerivedField derivedField = new DerivedField();
        derivedField.setName(PARAM_1);
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(constant);
        String dataType = getDATA_TYPEString(derivedField.getDataType());
        String opType = getOP_TYPEString(derivedField.getOpType());
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils
                .parseBlock(String.format(text, constant.getValue(),
                                          variableName,derivedField.getName(),
                                          dataType,
                                          opType));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLDerivedField.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getDerivedFieldVariableDeclarationWithFieldRef() throws IOException {
        final String variableName = "variableName";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF");
        DerivedField derivedField = new DerivedField();
        derivedField.setName(PARAM_1);
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(fieldRef);
        String dataType = getDATA_TYPEString(derivedField.getDataType());
        String opType = getOP_TYPEString(derivedField.getOpType());
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        String text = getFileContent(TEST_02_SOURCE);
        Statement expected = JavaParserUtils
                .parseBlock(String.format(text,fieldRef.getField(),
                                          variableName,derivedField.getName(),
                                          dataType,
                                          opType));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class,
                                               KiePMMLDerivedField.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getDerivedFieldVariableDeclarationWithApply() throws IOException {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF");
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        DerivedField derivedField = new DerivedField();
        derivedField.setName(PARAM_1);
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(apply);
        String dataType = getDATA_TYPEString(derivedField.getDataType());
        String opType = getOP_TYPEString(derivedField.getOpType());
        BlockStmt retrieved = KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils
                .parseBlock(String.format(text,
                                          constant.getValue(),fieldRef.getField(),
                                          apply.getFunction(),
                                          apply.getInvalidValueTreatment().value(),
                                          variableName,derivedField.getName(),
                                          dataType,
                                          opType));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLFieldRef.class,
                                               KiePMMLApply.class,
                                               KiePMMLDerivedField.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}