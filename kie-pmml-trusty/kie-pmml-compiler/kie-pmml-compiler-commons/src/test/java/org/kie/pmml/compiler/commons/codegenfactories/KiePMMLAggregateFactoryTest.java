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
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.FieldName;
import org.junit.Test;
import org.kie.pmml.api.enums.AGGREGATE_FUNCTIONS;
import org.kie.pmml.commons.model.expressions.KiePMMLAggregate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;

public class KiePMMLAggregateFactoryTest {

    private static final Aggregate.Function function = Aggregate.Function.AVERAGE;
    private static final String FIELD_NAME = "FIELD_NAME";
    private static final String GROUP_BY_NAME = "GROUP_BY_NAME";
    private static final String SQL_WHERE = "SQL_WHERE";

    @Test
    public void getAggregateVariableDeclaration() {
        String variableName = "variableName";
        Aggregate aggregate = new Aggregate();
        aggregate.setField(FieldName.create(FIELD_NAME));
        aggregate.setFunction(function);
        aggregate.setGroupField(FieldName.create(GROUP_BY_NAME));
        aggregate.setSqlWhere(SQL_WHERE);

        String functionString =
                AGGREGATE_FUNCTIONS.class.getName() + "." + AGGREGATE_FUNCTIONS.byName(aggregate.getFunction().value()).name();

        BlockStmt retrieved = KiePMMLAggregateFactory.getAggregateVariableDeclaration(variableName, aggregate);
        Statement expected = JavaParserUtils.parseBlock(String.format("{\n" +
                                                                              "    KiePMMLAggregate %1$s " +
                                                                              "= KiePMMLAggregate.builder(\"%2$s\", Collections.emptyList(), %3$s)\n" +
                                                                              "                .withGroupField(\"%4$s\")\n" +
                                                                              "                .withSqlWhere(\"%5$s\")\n" +
                                                                              "                .build();\n" +
                                                                              "}", variableName, FIELD_NAME, functionString,
                                                                      GROUP_BY_NAME, SQL_WHERE));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        List<Class<?>> imports = Arrays.asList(AGGREGATE_FUNCTIONS.class, KiePMMLAggregate.class, Collections.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }


}