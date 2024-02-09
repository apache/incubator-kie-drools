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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TextIndex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndex;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndexNormalization;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;
import static org.drools.util.FileUtils.getFileInputStream;

public class KiePMMLTextIndexFactoryTest {

    private static final String TRANSFORMATIONS_SAMPLE = "TransformationsSample.pmml";
    private static final String TEXT_INDEX_NORMALIZATION_FUNCTION = "TEXT_INDEX_NORMALIZATION_FUNCTION";
    private static final String TEST_01_SOURCE = "KiePMMLTextIndexFactoryTest_01.txt";
    private static TextIndex TEXTINDEX;

    @BeforeAll
    public static void setup() throws Exception {
        PMML pmmlModel = KiePMMLUtil.load(getFileInputStream(TRANSFORMATIONS_SAMPLE), TRANSFORMATIONS_SAMPLE);
        DefineFunction definedFunction = pmmlModel.getTransformationDictionary()
                .getDefineFunctions()
                .stream()
                .filter(defineFunction -> TEXT_INDEX_NORMALIZATION_FUNCTION.equals(defineFunction.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing derived field " + TEXT_INDEX_NORMALIZATION_FUNCTION));
        TEXTINDEX = ((TextIndex) definedFunction.getExpression());
    }

    @Test
    void getTextIndexVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLTextIndexFactory.getTextIndexVariableDeclaration(variableName,
                                                                                      TEXTINDEX);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,TEXTINDEX.getTextField()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, Collectors.class,
                                               KiePMMLFieldRef.class, KiePMMLInlineTable.class,
                                               KiePMMLTextIndex.class, KiePMMLTextIndexNormalization.class,
                                               KiePMMLRow.class, Map.class, Stream.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}