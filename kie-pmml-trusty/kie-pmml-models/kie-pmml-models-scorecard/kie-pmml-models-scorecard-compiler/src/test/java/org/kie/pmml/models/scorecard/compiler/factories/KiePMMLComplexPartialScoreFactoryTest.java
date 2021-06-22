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

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLComplexPartialScore;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLComplexPartialScoreFactoryTest {

    private static final Double value1 = 100.0;

    @Test
    public void getComplexPartialScoreVariableDeclaration() {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(constant);
        BlockStmt retrieved = KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName, complexPartialScore);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLConstant variableName_0 = new KiePMMLConstant" +
                                                  "(\"variableName_0\", Collections.emptyList(), %1$s);\n" +
                                                  "    KiePMMLComplexPartialScore %2$s = new KiePMMLComplexPartialScore" +
                                                  "(\"%2$s\", Collections.emptyList()," +
                                                  " variableName_0);\n" +
                                                  "}", constant.getValue(),
                                          variableName));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLComplexPartialScore.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getComplexPartialScoreVariableDeclarationWithFieldRef() {
        final String variableName = "variableName";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF"));
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(fieldRef);
        BlockStmt retrieved = KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName, complexPartialScore);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLFieldRef variableName_0 = new KiePMMLFieldRef" +
                                                  "(\"%1$s\", Collections.emptyList(), null);\n" +
                                                  "    KiePMMLComplexPartialScore %2$s = new KiePMMLComplexPartialScore" +
                                                  "(\"%2$s\", Collections.emptyList(), variableName_0);\n" +
                                                  "}", fieldRef.getField().getValue(),
                                          variableName));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class,
                                               KiePMMLComplexPartialScore.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getComplexPartialScoreVariableDeclarationWithApply() {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create("FIELD_REF"));
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(apply);
        BlockStmt retrieved = KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName, complexPartialScore);
        Statement expected = JavaParserUtils
                .parseBlock(String.format("{\n" +
                                                  "    KiePMMLConstant variableName_0_0 = new KiePMMLConstant" +
                                                  "(\"variableName_0_0\", Collections.emptyList(), %1$s);\n" +
                                                  "    KiePMMLFieldRef variableName_0_1 = new KiePMMLFieldRef" +
                                                  "(\"%2$s\", Collections.emptyList(), null);\n" +
                                                  "    KiePMMLApply variableName_0 = KiePMMLApply.builder" +
                                                  "(\"variableName_0\", Collections.emptyList(), \"%3$s\")" +
                                                  ".withDefaultValue(null).withMapMissingTo(null)" +
                                                  ".withInvalidValueTreatmentMethod(\"%4$s\")" +
                                                  ".withKiePMMLExpressions(Arrays.asList(variableName_0_0, " +
                                                  "variableName_0_1)).build();\n" +
                                                  "    KiePMMLComplexPartialScore %5$s = new KiePMMLComplexPartialScore" +
                                                  "(\"%5$s\", Collections.emptyList(), variableName_0);\n" +
                                                  "}",
                                          constant.getValue(),
                                          fieldRef.getField().getValue(),
                                          apply.getFunction(),
                                          apply.getInvalidValueTreatment().value(),
                                          variableName));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                                               KiePMMLFieldRef.class,
                                               KiePMMLApply.class,
                                               KiePMMLComplexPartialScore.class,
                                               Arrays.class,
                                               Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}