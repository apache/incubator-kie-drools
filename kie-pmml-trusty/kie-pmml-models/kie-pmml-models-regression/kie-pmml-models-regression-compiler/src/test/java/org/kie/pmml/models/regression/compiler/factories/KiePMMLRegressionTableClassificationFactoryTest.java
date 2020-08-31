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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableClassificationFactory.KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableClassificationFactory.KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA;

public class KiePMMLRegressionTableClassificationFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE).get();
    }

    @Test
    public void getRegressionTableTest() throws Exception {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        List<RegressionTable> regressionTables = Arrays.asList(regressionTableProf, regressionTableCler);
        KiePMMLOutputField outputFieldCat = getOutputField("CAT-1", RESULT_FEATURE.PROBABILITY, "CatPred-1");
        KiePMMLOutputField outputFieldNum = getOutputField("NUM-1", RESULT_FEATURE.PROBABILITY, "NumPred-0");
        KiePMMLOutputField outputFieldPrev = getOutputField("PREV", RESULT_FEATURE.PREDICTED_VALUE, null);
        List<KiePMMLOutputField> outputFields = Arrays.asList(outputFieldCat, outputFieldNum, outputFieldPrev);
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionTableClassificationFactory.getRegressionTables(regressionTables, RegressionModel.NormalizationMethod.SOFTMAX, OpType.CATEGORICAL, outputFields, "targetField", "packageName");
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));
    }

    @Test
    public void setConstructor() {
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        SimpleName generatedClassName = new SimpleName("GeneratedClassName");
        String targetField = "targetField";
        REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = REGRESSION_NORMALIZATION_METHOD.CAUCHIT;
        OP_TYPE opType = OP_TYPE.CATEGORICAL;
        KiePMMLRegressionTableClassificationFactory.setConstructor(constructorDeclaration,
                                                                   generatedClassName,
                                                                   targetField,
                                                                   regressionNormalizationMethod,
                                                                   opType);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("regressionNormalizationMethod", new NameExpr(regressionNormalizationMethod.getClass().getSimpleName() + "." + regressionNormalizationMethod.name()));
        assignExpressionMap.put("opType", new NameExpr(opType.getClass().getSimpleName() + "." + opType.name()));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName.asString(), superInvocationExpressionsMap, assignExpressionMap));
    }

    private KiePMMLOutputField getOutputField(String name, RESULT_FEATURE resultFeature, String targetField) {
        return KiePMMLOutputField.builder(name, Collections.emptyList())
                .withResultFeature(resultFeature)
                .withTargetField(targetField)
                .build();
    }
}