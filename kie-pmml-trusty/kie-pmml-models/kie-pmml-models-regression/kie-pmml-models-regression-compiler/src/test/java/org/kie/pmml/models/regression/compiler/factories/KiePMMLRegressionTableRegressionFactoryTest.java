/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.regression.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA;

public class KiePMMLRegressionTableRegressionFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE).get();
    }

    @Test
    public void getRegressionTableTest() throws Exception {
        regressionTable = getRegressionTable(3.5, "professional");
        List<RegressionTable> regressionTables = Collections.singletonList(regressionTable);
        Map<String, KiePMMLTableSourceCategory> retrieved =
                KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables,
                                                                            RegressionModel.NormalizationMethod.CAUCHIT, "targetField", "packageName");
        assertNotNull(retrieved);
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));
    }

    @Test
    public void setConstructor() {
        regressionTable = getRegressionTable(3.5, "professional");
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        SimpleName tableName = new SimpleName("TableName");
        String targetField = "targetField";
        KiePMMLRegressionTableRegressionFactory.setConstructor(regressionTable,
                                                               constructorDeclaration,
                                                               tableName,
                                                               targetField);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("intercept", new DoubleLiteralExpr(String.valueOf(3.5)));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, tableName.asString(), superInvocationExpressionsMap, assignExpressionMap));
    }
}