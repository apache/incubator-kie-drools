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
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldRef;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLFieldRefFactory;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLFieldRefFactoryTest {

    @Test
    public void getFieldRefVariableDeclaration() {
        String variableName = "variableName";
        String fieldName = "fieldName";
        String mapMissingTo = "mapMissingTo";
        FieldRef fieldRef = new FieldRef();
        fieldRef.setField(FieldName.create(fieldName));
        fieldRef.setMapMissingTo(mapMissingTo);
        BlockStmt retrieved = KiePMMLFieldRefFactory.getFieldRefVariableDeclaration(variableName, fieldRef);
        Statement expected = JavaParserUtils.parseBlock(String.format("{" +
                                                                              "KiePMMLFieldRef %1$s = new " +
                                                                              "KiePMMLFieldRef(\"%2$s\", Collections" +
                                                                              ".emptyList(),\"%3$s\");" +
                                                                              "}", variableName, fieldName, mapMissingTo));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLFieldRef.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}