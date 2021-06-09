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
import java.util.stream.IntStream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TransformationDictionary;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLTransformationDictionaryFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;

    @Test
    public void getKiePMMLTransformationDictionaryVariableDeclaration() {
        TransformationDictionary transformationDictionary = new TransformationDictionary();
        transformationDictionary.addDefineFunctions(getDefineFunctions());
        transformationDictionary.addDerivedFields(getDerivedFields());

        BlockStmt retrieved = KiePMMLTransformationDictionaryFactory.getKiePMMLTransformationDictionaryVariableDeclaration(transformationDictionary);
        Statement expected = JavaParserUtils
                .parseBlock("{\n" +
                                    "    KiePMMLParameterField CUSTOM_FUNCTION0_0 = KiePMMLParameterField.builder" +
                                    "(\"PARAM_10\", Collections.emptyList()).withDataType(\"double\").withOpType" +
                                    "(\"continuous\").withDisplayName(\"displayName10\").build();\n" +
                                    "    KiePMMLParameterField CUSTOM_FUNCTION0_1 = KiePMMLParameterField.builder" +
                                    "(\"PARAM_20\", Collections.emptyList()).withDataType(\"double\").withOpType" +
                                    "(\"continuous\").withDisplayName(\"displayName20\").build();\n" +
                                    "    KiePMMLConstant CUSTOM_FUNCTION0_Expression_0 = new KiePMMLConstant" +
                                    "(\"CUSTOM_FUNCTION0_Expression_0\", Collections.emptyList(), 100.0);\n" +
                                    "    KiePMMLFieldRef CUSTOM_FUNCTION0_Expression_1 = new KiePMMLFieldRef" +
                                    "(\"FIELD_REF0\", Collections.emptyList(), null);\n" +
                                    "    KiePMMLApply CUSTOM_FUNCTION0_Expression = KiePMMLApply.builder" +
                                    "(\"CUSTOM_FUNCTION0_Expression\", Collections.emptyList(), \"/\")" +
                                    ".withDefaultValue(null).withMapMissingTo(null).withInvalidValueTreatmentMethod" +
                                    "(\"returnInvalid\").withKiePMMLExpressions(Arrays.asList" +
                                    "(CUSTOM_FUNCTION0_Expression_0, CUSTOM_FUNCTION0_Expression_1)).build();\n" +
                                    "    KiePMMLDefineFunction CUSTOM_FUNCTION0 = new KiePMMLDefineFunction" +
                                    "(\"CUSTOM_FUNCTION0\", Collections.emptyList(), \"continuous\", Arrays.asList" +
                                    "(CUSTOM_FUNCTION0_0, CUSTOM_FUNCTION0_1), CUSTOM_FUNCTION0_Expression);\n" +
                                    "    KiePMMLParameterField CUSTOM_FUNCTION1_0 = KiePMMLParameterField.builder" +
                                    "(\"PARAM_11\", Collections.emptyList()).withDataType(\"double\").withOpType" +
                                    "(\"continuous\").withDisplayName(\"displayName11\").build();\n" +
                                    "    KiePMMLParameterField CUSTOM_FUNCTION1_1 = KiePMMLParameterField.builder" +
                                    "(\"PARAM_21\", Collections.emptyList()).withDataType(\"double\").withOpType" +
                                    "(\"continuous\").withDisplayName(\"displayName21\").build();\n" +
                                    "    KiePMMLConstant CUSTOM_FUNCTION1_Expression_0 = new KiePMMLConstant" +
                                    "(\"CUSTOM_FUNCTION1_Expression_0\", Collections.emptyList(), 100.0);\n" +
                                    "    KiePMMLFieldRef CUSTOM_FUNCTION1_Expression_1 = new KiePMMLFieldRef" +
                                    "(\"FIELD_REF1\", Collections.emptyList(), null);\n" +
                                    "    KiePMMLApply CUSTOM_FUNCTION1_Expression = KiePMMLApply.builder" +
                                    "(\"CUSTOM_FUNCTION1_Expression\", Collections.emptyList(), \"/\")" +
                                    ".withDefaultValue(null).withMapMissingTo(null).withInvalidValueTreatmentMethod" +
                                    "(\"returnInvalid\").withKiePMMLExpressions(Arrays.asList" +
                                    "(CUSTOM_FUNCTION1_Expression_0, CUSTOM_FUNCTION1_Expression_1)).build();\n" +
                                    "    KiePMMLDefineFunction CUSTOM_FUNCTION1 = new KiePMMLDefineFunction" +
                                    "(\"CUSTOM_FUNCTION1\", Collections.emptyList(), \"continuous\", Arrays.asList" +
                                    "(CUSTOM_FUNCTION1_0, CUSTOM_FUNCTION1_1), CUSTOM_FUNCTION1_Expression);\n" +
                                    "    KiePMMLConstant transformationDictionaryDerivedField_0_0 = new " +
                                    "KiePMMLConstant(\"transformationDictionaryDerivedField_0_0\", Collections" +
                                    ".emptyList(), 100.0);\n" +
                                    "    KiePMMLDerivedField transformationDictionaryDerivedField_0 = " +
                                    "KiePMMLDerivedField.builder(\"PARAM_20\", Collections.emptyList(), \"double\", " +
                                    "\"continuous\", transformationDictionaryDerivedField_0_0).withDisplayName(null)" +
                                    ".build();\n" +
                                    "    KiePMMLConstant transformationDictionaryDerivedField_1_0 = new " +
                                    "KiePMMLConstant(\"transformationDictionaryDerivedField_1_0\", Collections" +
                                    ".emptyList(), 100.0);\n" +
                                    "    KiePMMLDerivedField transformationDictionaryDerivedField_1 = " +
                                    "KiePMMLDerivedField.builder(\"PARAM_21\", Collections.emptyList(), \"double\", " +
                                    "\"continuous\", transformationDictionaryDerivedField_1_0).withDisplayName(null)" +
                                    ".build();\n" +
                                    "    KiePMMLTransformationDictionary transformationDictionary = " +
                                    "KiePMMLTransformationDictionary.builder(\"transformationDictionary\", " +
                                    "Collections.emptyList()).withDefineFunctions(Arrays.asList(CUSTOM_FUNCTION0, " +
                                    "CUSTOM_FUNCTION1)).withDerivedFields(Arrays.asList" +
                                    "(transformationDictionaryDerivedField_0, transformationDictionaryDerivedField_1)" +
                                    ").build();\n" +
                                    "}");
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLParameterField.class,
                                               KiePMMLConstant.class,
                                               KiePMMLFieldRef.class,
                                               KiePMMLApply.class,
                                               KiePMMLDerivedField.class,
                                               KiePMMLDefineFunction.class,
                                               KiePMMLTransformationDictionary.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private DefineFunction[] getDefineFunctions() {
        return IntStream.range(0, 2)
                .mapToObj(this::getDefineFunction)
                .toArray(DefineFunction[]::new);
    }

    private DefineFunction getDefineFunction(int counter) {
        ParameterField parameterField1 = new ParameterField(FieldName.create(PARAM_1 + counter));
        parameterField1.setDataType(DataType.DOUBLE);
        parameterField1.setOpType(OpType.CONTINUOUS);
        parameterField1.setDisplayName("displayName1" + counter);
        ParameterField parameterField2 = new ParameterField(FieldName.create(PARAM_2 + counter));
        parameterField2.setDataType(DataType.DOUBLE);
        parameterField2.setOpType(OpType.CONTINUOUS);
        parameterField2.setDisplayName("displayName2" + counter);
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF" + counter));
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        DefineFunction toReturn = new DefineFunction();
        toReturn.setName(CUSTOM_FUNCTION + counter);
        toReturn.addParameterFields(parameterField1, parameterField2);
        toReturn.setDataType(DataType.DOUBLE);
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setExpression(apply);
        return toReturn;
    }

    private DerivedField[] getDerivedFields() {
        return IntStream.range(0, 2)
                .mapToObj(this::getDerivedField)
                .toArray(DerivedField[]::new);
    }

    private DerivedField getDerivedField(int counter) {
        Constant constant = new Constant();
        constant.setValue(value1);
        DerivedField toReturn = new DerivedField();
        toReturn.setName(FieldName.create(PARAM_2 + counter));
        toReturn.setDataType(DataType.DOUBLE);
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setExpression(constant);
        return toReturn;
    }
}