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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.PMML;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;
import org.kie.pmml.commons.model.expressions.KiePMMLInlineTable;
import org.kie.pmml.commons.model.expressions.KiePMMLMapValues;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLMapValuesFactoryTest {

    private static final String TRANSFORMATIONS_SAMPLE = "TransformationsSample.pmml";
    private static final String MAPVALUED = "mapvalued";
    private static final String TEST_01_SOURCE = "KiePMMLMapValuesFactoryTest_01.txt";
    private static MapValues MAPVALUES;

    @BeforeClass
    public static void setup() throws Exception {
        PMML pmmlModel = KiePMMLUtil.load(getFileInputStream(TRANSFORMATIONS_SAMPLE), TRANSFORMATIONS_SAMPLE);
        DerivedField mapValued = pmmlModel.getTransformationDictionary()
                .getDerivedFields()
                .stream()
                .filter(derivedField -> MAPVALUED.equals(derivedField.getName().getValue()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing derived field " + MAPVALUED));
        MAPVALUES = ((MapValues) mapValued.getExpression());
    }

    @Test
    public void getMapValuesVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLMapValuesFactory.getMapValuesVariableDeclaration(variableName,
                                                                                      MAPVALUES);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, Collectors.class,
                                               KiePMMLFieldColumnPair.class, KiePMMLInlineTable.class,
                                               KiePMMLMapValues.class, KiePMMLRow.class, Map.class, Stream.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}