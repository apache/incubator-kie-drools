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
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDefineFunctionFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLDefineFunctionFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;

    @Test
    public void getDefineFunctionVariableDeclaration() {
        ParameterField parameterField1 = new ParameterField(FieldName.create(PARAM_1));
        parameterField1.setDataType(DataType.DOUBLE);
        parameterField1.setOpType(OpType.CONTINUOUS);
        parameterField1.setDisplayName("displayName1");
        ParameterField parameterField2 = new ParameterField(FieldName.create(PARAM_2));
        parameterField2.setDataType(DataType.DOUBLE);
        parameterField2.setOpType(OpType.CONTINUOUS);
        parameterField2.setDisplayName("displayName2");
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF"));
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        DefineFunction defineFunction = new DefineFunction();
        defineFunction.setName(CUSTOM_FUNCTION);
        defineFunction.addParameterFields(parameterField1, parameterField2);
        defineFunction.setDataType(DataType.DOUBLE);
        defineFunction.setOpType(OpType.CONTINUOUS);
        defineFunction.setExpression(apply);
        BlockStmt retrieved = KiePMMLDefineFunctionFactory.getDefineFunctionVariableDeclaration(defineFunction);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLParameterField CUSTOM_FUNCTION_0 = " +
                                                  "KiePMMLParameterField.builder(\"%s\", Collections" +
                                                  ".emptyList())" +
                                                  ".withDataType(\"%s\")" +
                                                  ".withOpType(\"%s\")" +
                                                  ".withDisplayName(\"%s\")" +
                                                  ".build();\n" +
                                                  "    KiePMMLParameterField CUSTOM_FUNCTION_1 = " +
                                                  "KiePMMLParameterField.builder(\"%s\", Collections" +
                                                  ".emptyList()).withDataType(\"%s\")" +
                                                  ".withOpType(\"%s\")" +
                                                  ".withDisplayName(\"%s\")" +
                                                  ".build();\n" +
                                                  "    KiePMMLConstant CUSTOM_FUNCTION_Expression_0 = " +
                                                  "new KiePMMLConstant(\"CUSTOM_FUNCTION_Expression_0\", " +
                                                  "Collections.emptyList(), %s);\n" +
                                                  "    KiePMMLFieldRef CUSTOM_FUNCTION_Expression_1 = " +
                                                  "new KiePMMLFieldRef(\"%s\", Collections.emptyList(), null);" +
                                                  "\n" +
                                                  "    KiePMMLApply CUSTOM_FUNCTION_Expression = " +
                                                  "KiePMMLApply.builder(\"CUSTOM_FUNCTION_Expression\", Collections" +
                                                  ".emptyList(), \"%s\")" +
                                                  ".withDefaultValue(null)" +
                                                  ".withMapMissingTo(null)" +
                                                  ".withInvalidValueTreatmentMethod" +
                                                  "(\"%s\")" +
                                                  ".withKiePMMLExpressions(Arrays.asList" +
                                                  "(CUSTOM_FUNCTION_Expression_0, CUSTOM_FUNCTION_Expression_1))" +
                                                  ".build()" +
                                                  ";\n" +
                                                  "    KiePMMLDefineFunction CUSTOM_FUNCTION = " +
                                                  "new KiePMMLDefineFunction(\"CUSTOM_FUNCTION\", Collections" +
                                                  ".emptyList(), \"%s\", Arrays" +
                                                  ".asList(CUSTOM_FUNCTION_0, CUSTOM_FUNCTION_1), " +
                                                  "CUSTOM_FUNCTION_Expression);\n" +
                                                  "}",
                                          parameterField1.getName().getValue(),
                                          parameterField1.getDataType().value(),
                                          parameterField1.getOpType().value(),
                                          parameterField1.getDisplayName(),
                                          parameterField2.getName().getValue(),
                                          parameterField2.getDataType().value(),
                                          parameterField2.getOpType().value(),
                                          parameterField2.getDisplayName(),
                                          constant.getValue(),
                                          fieldRef.getField().getValue(),
                                          apply.getFunction(),
                                          apply.getInvalidValueTreatment().value(),
                                          defineFunction.getOpType().value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
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