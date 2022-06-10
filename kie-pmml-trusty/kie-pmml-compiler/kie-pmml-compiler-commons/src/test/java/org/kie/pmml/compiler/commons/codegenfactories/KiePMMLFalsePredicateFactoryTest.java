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
import org.dmg.pmml.False;
import org.junit.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLFalsePredicateFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLFalsePredicateFactoryTest_01.txt";

    @Test
    public void getFalsePredicateVariableDeclaration() throws IOException {
        String variableName = "variableName";
        BlockStmt retrieved = KiePMMLFalsePredicateFactory.getFalsePredicateVariableDeclaration(variableName,
                                                                                                new False());
        String text = getFileContent(TEST_01_SOURCE);
        Statement expected = JavaParserUtils.parseBlock(String.format(text, variableName));
        assertThat(JavaParserUtils.equalsNode(expected,  retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(KiePMMLFalsePredicate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}