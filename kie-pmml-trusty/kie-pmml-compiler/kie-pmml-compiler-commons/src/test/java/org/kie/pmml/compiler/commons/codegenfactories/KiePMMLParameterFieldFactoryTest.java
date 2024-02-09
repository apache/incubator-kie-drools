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
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getDATA_TYPEString;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getOP_TYPEString;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLParameterFieldFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLParameterFieldFactoryTest_01.txt";

    @Test
    void getParameterFieldVariableDeclaration() throws IOException {
        String variableName = "variableName";
        ParameterField parameterField = new ParameterField(variableName);
        parameterField.setDataType(DataType.DOUBLE);
        parameterField.setOpType(OpType.CONTINUOUS);
        parameterField.setDisplayName("displayName");
        String dataType = getDATA_TYPEString(parameterField.getDataType());
        String opType = getOP_TYPEString(parameterField.getOpType());

        BlockStmt retrieved = KiePMMLParameterFieldFactory.getParameterFieldVariableDeclaration(variableName,
                                                                                                parameterField);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName,
                                                                      dataType,
                                                                      opType,
                                                                      parameterField.getDisplayName()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLParameterField.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}