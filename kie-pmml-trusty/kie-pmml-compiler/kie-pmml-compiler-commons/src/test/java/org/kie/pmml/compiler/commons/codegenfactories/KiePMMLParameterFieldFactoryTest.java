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
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.ParameterField;
import org.junit.Test;
import org.kie.pmml.commons.transformations.KiePMMLParameterField;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLParameterFieldFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLParameterFieldFactoryTest {

    @Test
    public void getParameterFieldVariableDeclaration() {
        String variableName = "variableName";
        ParameterField parameterField = new ParameterField(FieldName.create(variableName));
        parameterField.setDataType(DataType.DOUBLE);
        parameterField.setOpType(OpType.CONTINUOUS);
        parameterField.setDisplayName("displayName");
        BlockStmt retrieved = KiePMMLParameterFieldFactory.getParameterFieldVariableDeclaration(variableName,
                                                                                                parameterField);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLParameterField %1$s" +
                                                                              " = KiePMMLParameterField.builder" +
                                                                              "(\"%1$s\", Collections" +
                                                                              ".emptyList()).withDataType(\"%2$s\")" +
                                                                              ".withOpType(\"%3$s\")" +
                                                                              ".withDisplayName(\"%4$s\")" +
                                                                              ".build();\n" +
                                                                              "}", variableName,
                                                                      parameterField.getDataType().value(),
                                                                      parameterField.getOpType().value(),
                                                                      parameterField.getDisplayName()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLParameterField.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}