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

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDerivedFieldFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLDerivedFieldFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;

    @Test
    public void getDerivedFieldVariableDeclarationWithConstant() {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        DerivedField derivedField = new DerivedField();
        derivedField.setName(FieldName.create(PARAM_1));
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(constant);
        BlockStmt retrieved = KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLConstant variableName_0 = new KiePMMLConstant" +
                                                  "(\"variableName_0\", Collections.emptyList(), %s);\n" +
                                                  "    KiePMMLDerivedField %s = KiePMMLDerivedField.builder" +
                                                  "(\"%s\", Collections.emptyList(), \"%s\", \"%s\"," +
                                                  " variableName_0).withDisplayName(null).build();\n" +
                                                  "}", constant.getValue(),
                                          variableName,
                                          derivedField.getName().getValue(),
                                          derivedField.getDataType().value(),
                                          derivedField.getOpType().value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLDerivedField.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getDerivedFieldVariableDeclarationWithFieldRef() {
        final String variableName = "variableName";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF"));
        DerivedField derivedField = new DerivedField();
        derivedField.setName(FieldName.create(PARAM_1));
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(fieldRef);
        BlockStmt retrieved = KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLFieldRef variableName_0 = new KiePMMLFieldRef" +
                                                  "(\"%s\", Collections.emptyList(), null);\n" +
                                                  "    KiePMMLDerivedField %s = KiePMMLDerivedField.builder" +
                                                  "(\"%s\", Collections.emptyList(), \"%s\", \"%s\"," +
                                                  " variableName_0).withDisplayName(null).build();\n" +
                                                  "}", fieldRef.getField().getValue(),
                                          variableName,
                                          derivedField.getName().getValue(),
                                          derivedField.getDataType().value(),
                                          derivedField.getOpType().value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class,
                                               KiePMMLDerivedField.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getDerivedFieldVariableDeclarationWithApply() {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF"));
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        DerivedField derivedField = new DerivedField();
        derivedField.setName(FieldName.create(PARAM_1));
        derivedField.setDataType(DataType.DOUBLE);
        derivedField.setOpType(OpType.CONTINUOUS);
        derivedField.setExpression(apply);
        BlockStmt retrieved = KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration(variableName, derivedField);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLConstant variableName_0_0 = new KiePMMLConstant" +
                                                  "(\"variableName_0_0\", Collections.emptyList(), %s);\n" +
                                                  "    KiePMMLFieldRef variableName_0_1 = new KiePMMLFieldRef" +
                                                  "(\"%s\", Collections.emptyList(), null);\n" +
                                                  "    KiePMMLApply variableName_0 = KiePMMLApply.builder" +
                                                  "(\"variableName_0\", Collections.emptyList(), \"%s\")" +
                                                  ".withDefaultValue(null).withMapMissingTo(null)" +
                                                  ".withInvalidValueTreatmentMethod(\"%s\")" +
                                                  ".withKiePMMLExpressions(Arrays.asList(variableName_0_0, " +
                                                  "variableName_0_1)).build();\n" +
                                                  "    KiePMMLDerivedField %s = KiePMMLDerivedField.builder" +
                                                  "(\"%s\", Collections.emptyList(), \"%s\", \"%s\"," +
                                                  " variableName_0).withDisplayName(null).build();\n" +
                                                  "}",
                                          constant.getValue(),
                                          fieldRef.getField().getValue(),
                                          apply.getFunction(),
                                          apply.getInvalidValueTreatment().value(),
                                          variableName,
                                          derivedField.getName().getValue(),
                                          derivedField.getDataType().value(),
                                          derivedField.getOpType().value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLFieldRef.class,
                                               KiePMMLApply.class,
                                               KiePMMLDerivedField.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}