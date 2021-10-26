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
import java.util.stream.IntStream;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataField;
import org.dmg.pmml.Interval;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.Value;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLMiningFieldFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLMiningFieldFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLMiningFieldFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLMiningFieldFactoryTest_03.txt";
    private static final String VARIABLE_NAME = "variableName";

    @Test
    public void getMiningFieldVariableDeclarationNoAllowedValuesNoIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved = KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                          Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getMiningFieldVariableDeclarationWithAllowedValuesNoIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        dataField.addValues(new Value("A"), new Value("B"), new Value("C"));
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved = KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                          Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_02_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    @Test
    public void getMiningFieldVariableDeclarationWithAllowedValuesAndIntervals() throws IOException {
        DataField dataField = getRandomDataField();
        dataField.addValues(new Value("A"), new Value("B"), new Value("C"));
        IntStream.range(0, 3).forEach(i -> {
            Interval toAdd = new Interval(Interval.Closure.CLOSED_CLOSED);
            toAdd.setLeftMargin(i);
            toAdd.setRightMargin(i * 10);
            dataField.addIntervals(toAdd);
        });
        MiningField miningField = new MiningField();
        miningField.setName(dataField.getName());
        miningField.setUsageType(MiningField.UsageType.TARGET);
        BlockStmt retrieved = KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration(VARIABLE_NAME, miningField,
                                                                                          Collections.singletonList(dataField));
        String dataTypeString =
                DATA_TYPE.class.getName() + "." + DATA_TYPE.byName(dataField.getDataType().value()).name();
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, VARIABLE_NAME,
                                                                      miningField.getName().getValue(),
                                                                      dataTypeString));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLInterval.class,
                                               KiePMMLMiningField.class, DATA_TYPE.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}