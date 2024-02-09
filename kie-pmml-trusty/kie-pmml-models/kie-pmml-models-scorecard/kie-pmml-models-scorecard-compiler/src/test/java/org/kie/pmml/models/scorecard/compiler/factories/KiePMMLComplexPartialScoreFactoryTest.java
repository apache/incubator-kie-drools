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
package org.kie.pmml.models.scorecard.compiler.factories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLComplexPartialScore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLComplexPartialScoreFactoryTest {

    private static final Double value1 = 100.0;
    private static final String TEST_01_SOURCE = "KiePMMLComplexPartialScoreFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLComplexPartialScoreFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLComplexPartialScoreFactoryTest_03.txt";

    @Test
    void getComplexPartialScoreVariableDeclaration() throws IOException {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(constant);
        BlockStmt retrieved =
                KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName,
                        complexPartialScore);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, constant.getValue(),
                variableName));
        assertThat(retrieved).isEqualTo(expected);
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                KiePMMLComplexPartialScore.class,
                Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getComplexPartialScoreVariableDeclarationWithFieldRef() throws IOException {
        final String variableName = "variableName";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF");
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(fieldRef);
        BlockStmt retrieved =
                KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName,
                        complexPartialScore);
        String text = getFileContent(TEST_02_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text,fieldRef.getField(),
                variableName));
        assertThat(retrieved).isEqualTo(expected);
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class,
                KiePMMLComplexPartialScore.class,
                Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    void getComplexPartialScoreVariableDeclarationWithApply() throws IOException {
        final String variableName = "variableName";
        Constant constant = new Constant();
        constant.setValue(value1);
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField("FIELD_REF");
        Apply apply = new Apply();
        apply.setFunction("/");
        apply.addExpressions(constant, fieldRef);
        ComplexPartialScore complexPartialScore = new ComplexPartialScore();
        complexPartialScore.setExpression(apply);
        BlockStmt retrieved =
                KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration(variableName,
                        complexPartialScore);
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text,
                constant.getValue(),fieldRef.getField(),
                apply.getFunction(),
                apply.getInvalidValueTreatment().value(),
                variableName));
        assertThat(retrieved).isEqualTo(expected);
        List<Class<?>> imports = Arrays.asList(KiePMMLConstant.class,
                KiePMMLFieldRef.class,
                KiePMMLApply.class,
                KiePMMLComplexPartialScore.class,
                Arrays.class,
                Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}