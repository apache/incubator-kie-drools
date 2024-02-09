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
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.InvalidValueTreatmentMethod;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLApplyFactoryTest {

    private static final String function = "function";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";
    private static final String TEST_01_SOURCE = "KiePMMLApplyFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLApplyFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLApplyFactoryTest_03.txt";

    @Test
    void getApplyVariableDeclarationWithConstants() throws IOException {
        String variableName = "variableName";
        Apply apply = new Apply();
        apply.setFunction(function);
        String mapMissingTo = "mapMissingTo";
        apply.setMapMissingTo(mapMissingTo);
        String defaultValue = "defaultValue";
        apply.setDefaultValue(defaultValue);
        InvalidValueTreatmentMethod invalidValueTreatmentMethod = InvalidValueTreatmentMethod.AS_MISSING;
        apply.setInvalidValueTreatment(invalidValueTreatmentMethod);
        Constant constant1 = new Constant();
        constant1.setValue(value1);
        Constant constant2 = new Constant();
        constant2.setValue(value2);
        apply.addExpressions(constant1, constant2);
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, value1, value2, variableName, function,
                                                                      defaultValue, mapMissingTo,
                                                                      invalidValueTreatmentMethod.value()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class, KiePMMLApply.class, Collections.class,
                                               Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getApplyVariableDeclarationWithFieldRefs() throws IOException {
        String variableName = "variableName";
        Apply apply = new Apply();
        apply.setFunction(function);
        String mapMissingTo = "mapMissingTo";
        apply.setMapMissingTo(mapMissingTo);
        String defaultValue = "defaultValue";
        apply.setDefaultValue(defaultValue);
        InvalidValueTreatmentMethod invalidValueTreatmentMethod = InvalidValueTreatmentMethod.AS_MISSING;
        apply.setInvalidValueTreatment(invalidValueTreatmentMethod);
        FieldRef fieldRef1 = new FieldRef();
        fieldRef1.setField(PARAM_1);
        FieldRef fieldRef2 = new FieldRef();
        fieldRef2.setField(PARAM_2);
        apply.addExpressions(fieldRef1, fieldRef2);
        BlockStmt retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        String text = getFileContent(TEST_02_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, PARAM_1, PARAM_2, variableName, function,
                                                                      defaultValue, mapMissingTo,
                                                                      invalidValueTreatmentMethod.value()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, KiePMMLApply.class, Collections.class,
                                               Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getApplyVariableDeclarationWithApply() throws IOException {
        String variableName = "variableName";
        Apply nestedApply = new Apply();
        nestedApply.setFunction("nested_function");
        String mapMissingTo = "mapMissingTo";
        nestedApply.setMapMissingTo(mapMissingTo);
        String defaultValue = "defaultValue";
        nestedApply.setDefaultValue(defaultValue);
        InvalidValueTreatmentMethod nestedInvalidValueTreatmentMethod = InvalidValueTreatmentMethod.AS_MISSING;
        nestedApply.setInvalidValueTreatment(nestedInvalidValueTreatmentMethod);
        FieldRef fieldRef1 = new FieldRef();
        fieldRef1.setField(PARAM_1);
        FieldRef fieldRef2 = new FieldRef();
        fieldRef2.setField(PARAM_2);
        nestedApply.addExpressions(fieldRef1, fieldRef2);
        Apply apply = new Apply();
        apply.setFunction(function);
        InvalidValueTreatmentMethod invalidValueTreatmentMethod = InvalidValueTreatmentMethod.AS_MISSING;
        apply.setInvalidValueTreatment(invalidValueTreatmentMethod);
        apply.addExpressions(nestedApply);
        BlockStmt retrieved = KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, PARAM_1, PARAM_2,
                                                                      defaultValue, mapMissingTo,
                                                                      nestedInvalidValueTreatmentMethod.value(),
                                                                      variableName,
                                                                      invalidValueTreatmentMethod.value()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, KiePMMLApply.class, Collections.class,
                                               Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}