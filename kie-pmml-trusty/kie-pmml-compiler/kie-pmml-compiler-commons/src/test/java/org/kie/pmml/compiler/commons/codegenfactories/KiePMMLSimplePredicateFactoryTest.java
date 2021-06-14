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
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.SimplePredicate;
import org.junit.Test;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLSimplePredicateFactoryTest {

    @Test
    public void getSimplePredicateVariableDeclaration() {
        String variableName = "variableName";
        final SimplePredicate simplePredicate = new SimplePredicate();
        simplePredicate.setField(FieldName.create("CUSTOM_FIELD"));
        simplePredicate.setValue("235.435");
        simplePredicate.setOperator(SimplePredicate.Operator.EQUAL);
        String operatorString = OPERATOR.class.getName() + "." + OPERATOR.byName(simplePredicate.getOperator().value());
        DataField dataField = new DataField();
        dataField.setName(simplePredicate.getField());
        dataField.setDataType(DataType.DOUBLE);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);

        BlockStmt retrieved = KiePMMLSimplePredicateFactory.getSimplePredicateVariableDeclaration(variableName, simplePredicate, Collections.emptyList(), dataDictionary);
        Statement expected = JavaParserUtils.parseBlock(String.format("{" +
                                                                              "KiePMMLSimplePredicate " +
                                                                              "%1$s = KiePMMLSimplePredicate.builder(\"%2$s\", Collections.emptyList(), %3$s)\n" +
                                                                              ".withValue(%4$s)\n" +
                                                                              ".build();" +
                                                                                  "}", variableName,
                                                                      simplePredicate.getField().getValue(),
                                                                      operatorString,
                                                                      simplePredicate.getValue()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(KiePMMLSimplePredicate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }
}