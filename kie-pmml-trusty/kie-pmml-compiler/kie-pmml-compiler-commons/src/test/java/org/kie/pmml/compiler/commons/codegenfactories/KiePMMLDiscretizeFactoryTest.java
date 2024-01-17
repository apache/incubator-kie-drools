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
import org.dmg.pmml.DataType;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.DiscretizeBin;
import org.dmg.pmml.Interval;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretize;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getDATA_TYPEString;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLDiscretizeFactoryTest {

    private static final String NAME = "name";
    private static final String MAP_MISSING_TO = "mapMissingTo";
    private static final String DEFAULTVALUE = "defaultValue";
    private static final DataType dataType = DataType.INTEGER;
    private static final String TEST_01_SOURCE = "KiePMMLDiscretizeFactoryTest_01.txt";

    private static DiscretizeBin discretizeBin1;
    private static DiscretizeBin discretizeBin2;
    private static List<DiscretizeBin> discretizeBins;

    @BeforeAll
    public static void setup() {
        discretizeBin1 = getDiscretizeBin(getInterval(null, 20, Interval.Closure.OPEN_OPEN), "discretizeBin1");
        discretizeBin2 = getDiscretizeBin(getInterval(21, 30,
                                                      Interval.Closure.OPEN_CLOSED), "discretizeBin2");
        discretizeBins = Arrays.asList(discretizeBin1, discretizeBin2);
    }

    @Test
    void getDiscretizeVariableDeclaration() throws IOException {
        String variableName = "variableName";
        Discretize discretize = new Discretize();
        discretize.setField(NAME);
        discretize.setDataType(dataType);
        discretize.setMapMissingTo(MAP_MISSING_TO);
        discretize.setDefaultValue(DEFAULTVALUE);
        discretize.addDiscretizeBins(discretizeBins.toArray(new DiscretizeBin[0]));

        BlockStmt retrieved = KiePMMLDiscretizeFactory.getDiscretizeVariableDeclaration(variableName,
                                                                                        discretize);
        String dataTypeString = getDATA_TYPEString(discretize.getDataType());
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, NAME, MAP_MISSING_TO,
                                                                      DEFAULTVALUE, dataTypeString));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLDiscretize.class,
                                               KiePMMLDiscretizeBin.class, KiePMMLInterval.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private static Interval getInterval(Number leftMargin, Number rightMargin, Interval.Closure closure) {
        Interval toReturn = new Interval();
        toReturn.setLeftMargin(leftMargin);
        toReturn.setRightMargin(rightMargin);
        toReturn.setClosure(closure);
        return toReturn;
    }

    private static DiscretizeBin getDiscretizeBin(Interval interval, String binValue) {
        DiscretizeBin toReturn = new DiscretizeBin();
        toReturn.setBinValue(binValue);
        toReturn.setInterval(interval);
        return toReturn;
    }
}