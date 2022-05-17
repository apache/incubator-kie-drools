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

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.FieldColumnPair;
import org.dmg.pmml.FieldName;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLFieldColumnPairFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLFieldColumnPairFactoryTest_01.txt";

    @Test
    public void getRowVariableDeclaration() throws IOException {
        String variableName = "variableName";
        String fieldName = "fieldName";
        String column = "column";
        FieldColumnPair fieldColumnPair = new FieldColumnPair();
        fieldColumnPair.setField(FieldName.create(fieldName));
        fieldColumnPair.setColumn(column);

        BlockStmt retrieved = KiePMMLFieldColumnPairFactory.getFieldColumnPairVariableDeclaration(variableName,
                                                                                                  fieldColumnPair);
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName, fieldName, column));
        assertThat(JavaParserUtils.equalsNode(expected,  retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Collections.class, KiePMMLFieldColumnPair.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}