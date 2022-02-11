package org.kie.pmml.compiler.commons.factories;/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.junit.Test;
import org.kie.pmml.api.enums.PMML_MODEL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.GET_MODEL;

public class KiePMMLFactoryFactoryTest {

    private static final List<PMML_MODEL> NOT_CODEGEN = Arrays.asList(PMML_MODEL.REGRESSION_MODEL);

    @Test
    public void getInstantiationExpression() {
        final String kiePMMLModelClass = "org.kie.model.ClassModel";
        for (PMML_MODEL pmmlModel : PMML_MODEL.values()) {
            Expression retrieved = KiePMMLFactoryFactory.getInstantiationExpression(kiePMMLModelClass, pmmlModel);
            if (NOT_CODEGEN.contains(pmmlModel)) {
                validateNotCodegen(retrieved, kiePMMLModelClass);
            } else {
                validateCodegen(retrieved, kiePMMLModelClass);
            }
        }
    }

    private void validateNotCodegen(Expression toValidate, String kiePMMLModelClass) {
        assertNotNull(toValidate);
        assertTrue(toValidate instanceof MethodCallExpr);
        MethodCallExpr methodCallExpr = (MethodCallExpr) toValidate;
        assertEquals(kiePMMLModelClass, methodCallExpr.getScope().get().asNameExpr().toString());
        assertEquals(GET_MODEL, methodCallExpr.getName().asString());
    }

    private void validateCodegen(Expression toValidate, String kiePMMLModelClass) {
        assertNotNull(toValidate);
        assertTrue(toValidate instanceof ObjectCreationExpr);
        ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) toValidate;
        assertEquals(kiePMMLModelClass, objectCreationExpr.getType().asString());
    }
}