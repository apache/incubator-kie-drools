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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TextIndex;
import org.dmg.pmml.TextIndexNormalization;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndexNormalization;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLTextIndexNormalizationFactoryTest {

    private static final String TRANSFORMATIONS_SAMPLE = "TransformationsSample.pmml";
    private static final String TEXT_INDEX_NORMALIZATION_FUNCTION = "TEXT_INDEX_NORMALIZATION_FUNCTION";
    private static TextIndexNormalization TEXTINDEXNORMALIZATION;

    @BeforeClass
    public static void setup() throws Exception {
        PMML pmmlModel = KiePMMLUtil.load(getFileInputStream(TRANSFORMATIONS_SAMPLE), TRANSFORMATIONS_SAMPLE);
        DefineFunction definedFunction = pmmlModel.getTransformationDictionary()
                .getDefineFunctions()
                .stream()
                .filter(defineFunction -> TEXT_INDEX_NORMALIZATION_FUNCTION.equals(defineFunction.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing derived field " + TEXT_INDEX_NORMALIZATION_FUNCTION));
        TextIndex textIndex = ((TextIndex) definedFunction.getExpression());
        TEXTINDEXNORMALIZATION = textIndex.getTextIndexNormalizations().get(0);
    }

    @Test
    public void getTextIndexNormalizationVariableDeclaration() {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLTextIndexNormalizationFactory.getTextIndexNormalizationVariableDeclaration(variableName,
                                                                                          TEXTINDEXNORMALIZATION);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    Map<String, Object> " +
                                                                              "%1$s_InlineTable_0_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"interfaces?\" }, { \"stem\", \"interface\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                              "    KiePMMLRow " +
                                                                              "%1$s_InlineTable_0 = new " +
                                                                              "KiePMMLRow" +
                                                                              "(%1$s_InlineTable_0_columnValues);\n" +
                                                                              "    Map<String, Object> " +
                                                                              "%1$s_InlineTable_1_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"is|are|seem(ed|s?)|were\" }, { \"stem\", \"be\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                              "    KiePMMLRow " +
                                                                              "%1$s_InlineTable_1 = new " +
                                                                              "KiePMMLRow" +
                                                                              "(%1$s_InlineTable_1_columnValues);\n" +
                                                                              "    Map<String, Object> " +
                                                                              "%1$s_InlineTable_2_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"user friendl(y|iness)\" }, { \"stem\", \"user_friendly\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                              "    KiePMMLRow " +
                                                                              "%1$s_InlineTable_2 = new " +
                                                                              "KiePMMLRow" +
                                                                              "(%1$s_InlineTable_2_columnValues);\n" +
                                                                              "    KiePMMLInlineTable " +
                                                                              "%1$s_InlineTable = new " +
                                                                              "KiePMMLInlineTable" +
                                                                              "(\"%1$s_InlineTable\", " +
                                                                              "Collections.emptyList(), Arrays.asList" +
                                                                              "(%1$s_InlineTable_0, " +
                                                                              "%1$s_InlineTable_1, " +
                                                                              "%1$s_InlineTable_2));\n" +
                                                                              "    KiePMMLTextIndexNormalization " +
                                                                              "%1$s = " +
                                                                              "KiePMMLTextIndexNormalization.builder" +
                                                                              "(\"%1$s\", Collections" +
                                                                              ".emptyList()).withInField(\"string\")" +
                                                                              ".withOutField(\"stem\")" +
                                                                              ".withKiePMMLInlineTable" +
                                                                              "(%1$s_InlineTable)" +
                                                                              ".withRegexField(\"regex\")" +
                                                                              ".withRecursive(false)" +
                                                                              ".withIsCaseSensitive(false)" +
                                                                              ".withMaxLevenshteinDistance(null)" +
                                                                              ".withWordSeparatorCharacterRE(null)" +
                                                                              ".withTokenize(false).build();\n" +
                                                                              "}", variableName));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, Collectors.class, KiePMMLInlineTable.class, KiePMMLTextIndexNormalization.class, KiePMMLRow.class, Map.class, Stream.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}