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
import java.util.stream.IntStream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TransformationDictionary;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLTransformationDictionaryFactoryTest {

    private static final String CUSTOM_FUNCTION = "CUSTOM_FUNCTION";
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final Double value1 = 100.0;
    private static final String TEST_01_SOURCE = "KiePMMLTransformationDictionaryFactoryTest_01.txt";

    @Test
    void getKiePMMLTransformationDictionaryVariableDeclaration() throws IOException {
        TransformationDictionary transformationDictionary = new TransformationDictionary();
        transformationDictionary.addDefineFunctions(getDefineFunctions());
        transformationDictionary.addDerivedFields(getDerivedFields());

        BlockStmt retrieved =
                KiePMMLTransformationDictionaryFactory.getKiePMMLTransformationDictionaryVariableDeclaration(transformationDictionary);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
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
        ParameterField parameterField1 = new ParameterField(PARAM_1 + counter);
        parameterField1.setDataType(DataType.DOUBLE);
        parameterField1.setOpType(OpType.CONTINUOUS);
        parameterField1.setDisplayName("displayName1" + counter);
        ParameterField parameterField2 = new ParameterField(PARAM_2 + counter);
        parameterField2.setDataType(DataType.DOUBLE);
        parameterField2.setOpType(OpType.CONTINUOUS);
        parameterField2.setDisplayName("displayName2" + counter);
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF" + counter);
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
        toReturn.setName(PARAM_2 + counter);
        toReturn.setDataType(DataType.DOUBLE);
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setExpression(constant);
        return toReturn;
    }
}