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
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.InvalidValueTreatmentMethod;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLApplyFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLApplyFactoryTest {

    private static final String function = "function";
    private static final Double value1 = 100.0;
    private static final Double value2 = 5.0;
    private static final String PARAM_1 = "PARAM_1";
    private static final String PARAM_2 = "PARAM_2";

    @Test
    public void getApplyVariableDeclarationWithConstants() {
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
        BlockStmt retrieved = KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLConstant variableName_0 = " +
                                                                              "new KiePMMLConstant" +
                                                                              "(\"variableName_0\", Collections" +
                                                                              ".emptyList(), %1$s);\n" +
                                                                              "    KiePMMLConstant variableName_1 = " +
                                                                              "new KiePMMLConstant" +
                                                                              "(\"variableName_1\", Collections" +
                                                                              ".emptyList(), %2$s);\n" +
                                                                              "    KiePMMLApply %3$s = " +
                                                                              "KiePMMLApply.builder(\"%3$s\"," +
                                                                              " Collections.emptyList(), " +
                                                                              "\"%4$s\").withDefaultValue" +
                                                                              "(\"%5$s\").withMapMissingTo" +
                                                                              "(\"%6$s\")" +
                                                                              ".withInvalidValueTreatmentMethod" +
                                                                              "(\"%7$s\")" +
                                                                              ".withKiePMMLExpressions(Arrays.asList" +
                                                                              "(variableName_0, variableName_1))" +
                                                                              ".build();\n" +
                                                                              "}", value1, value2, variableName, function,
                                                                      defaultValue, mapMissingTo, invalidValueTreatmentMethod.value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class, KiePMMLApply.class, Collections.class, Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getApplyVariableDeclarationWithFieldRefs() {
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
        fieldRef1.setField(FieldName.create(PARAM_1));
        FieldRef fieldRef2 = new FieldRef();
        fieldRef2.setField(FieldName.create(PARAM_2));
        apply.addExpressions(fieldRef1, fieldRef2);
        BlockStmt retrieved = KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLFieldRef variableName_0 = " +
                                                                              "new KiePMMLFieldRef" +
                                                                              "(\"%1$s\", Collections" +
                                                                              ".emptyList(), null);\n" +
                                                                              "    KiePMMLFieldRef variableName_1 = " +
                                                                              "new KiePMMLFieldRef" +
                                                                              "(\"%2$s\", Collections" +
                                                                              ".emptyList(), null);\n" +
                                                                              "    KiePMMLApply %3$s = " +
                                                                              "KiePMMLApply.builder(\"%3$s\"," +
                                                                              " Collections.emptyList(), " +
                                                                              "\"%4$s\").withDefaultValue" +
                                                                              "(\"%5$s\").withMapMissingTo" +
                                                                              "(\"%6$s\")" +
                                                                              ".withInvalidValueTreatmentMethod" +
                                                                              "(\"%7$s\")" +
                                                                              ".withKiePMMLExpressions(Arrays.asList" +
                                                                              "(variableName_0, variableName_1))" +
                                                                              ".build();\n" +
                                                                              "}",  PARAM_1, PARAM_2, variableName, function,
                                                                      defaultValue, mapMissingTo, invalidValueTreatmentMethod.value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, KiePMMLApply.class, Collections.class, Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getApplyVariableDeclarationWithApply() {
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
        fieldRef1.setField(FieldName.create(PARAM_1));
        FieldRef fieldRef2 = new FieldRef();
        fieldRef2.setField(FieldName.create(PARAM_2));
        nestedApply.addExpressions(fieldRef1, fieldRef2);
        Apply apply = new Apply();
        apply.setFunction(function);
        InvalidValueTreatmentMethod invalidValueTreatmentMethod = InvalidValueTreatmentMethod.AS_MISSING;
        apply.setInvalidValueTreatment(invalidValueTreatmentMethod);
        apply.addExpressions(nestedApply);
        BlockStmt retrieved = KiePMMLApplyFactory.getApplyVariableDeclaration(variableName, apply);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLFieldRef variableName_0_0 =" +
                                                                              " new KiePMMLFieldRef(\"%1$s\", Collections.emptyList(), null);\n" +
                                                                              "    KiePMMLFieldRef variableName_0_1 =" +
                                                                              " new KiePMMLFieldRef(\"%2$s\", " +
                                                                              "Collections.emptyList(), null);\n" +
                                                                              "    KiePMMLApply variableName_0 = " +
                                                                              "KiePMMLApply.builder" +
                                                                              "(\"variableName_0\", Collections" +
                                                                              ".emptyList(), \"nested_function\")" +
                                                                              ".withDefaultValue(\"%3$s\")" +
                                                                              ".withMapMissingTo(\"%4$s\")" +
                                                                              ".withInvalidValueTreatmentMethod" +
                                                                              "(\"%5$s\").withKiePMMLExpressions" +
                                                                              "(Arrays.asList(variableName_0_0, " +
                                                                              "variableName_0_1)).build();\n" +
                                                                              "    KiePMMLApply %6$s = " +
                                                                              "KiePMMLApply.builder(\"variableName\"," +
                                                                              " Collections.emptyList(), " +
                                                                              "\"function\").withDefaultValue(null)" +
                                                                              ".withMapMissingTo(null)" +
                                                                              ".withInvalidValueTreatmentMethod" +
                                                                              "(\"%7$s\")" +
                                                                              ".withKiePMMLExpressions(Arrays.asList" +
                                                                              "(variableName_0)).build();\n" +
                                                                              "}",  PARAM_1, PARAM_2,
                                                                      defaultValue, mapMissingTo, nestedInvalidValueTreatmentMethod.value(),
                                                                      variableName, invalidValueTreatmentMethod.value()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, KiePMMLApply.class, Collections.class, Arrays.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }


}