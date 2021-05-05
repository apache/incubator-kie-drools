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

package org.kie.pmml.compiler.commons.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.supportedExpressionSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.unsupportedExpressionSupplier;

public class DerivedFieldFunctionUtilsTest {

    private static final Function<Supplier<Expression>, DerivedField> derivedFieldCreator = supplier -> {
        Expression expression = supplier.get();
        DerivedField defineFunction = new DerivedField();
        defineFunction.setName(FieldName.create("DERIVED_FIELD_" + expression.getClass().getSimpleName()));
        defineFunction.setExpression(expression);
        return defineFunction;
    };

    @Test(expected = KiePMMLException.class)
    public void getDerivedFieldsMethodMapUnsupportedExpression() {
        List<DerivedField> derivedFields =
                unsupportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields, arityCounter);
    }

    @Test
    public void getDerivedFieldsMethodMapSupportedExpression() {
        List<DerivedField> derivedFields =
                supportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        Map<String, MethodDeclaration> retrieved = DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields,
                                                                                                       arityCounter);
        assertEquals(derivedFields.size(), retrieved.size());
    }

    @Test
    public void getDerivedFieldMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            DerivedField derivedField = derivedFieldCreator.apply(supplier);
            try {
                DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, new AtomicInteger());
                fail(String.format("Expecting KiePMMLException for %s", derivedField));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        }
    }

    @Test
    public void getDerivedFieldMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            DerivedField derivedField = derivedFieldCreator.apply(supplier);
            try {
                DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, new AtomicInteger());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, derivedField.getExpression().getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getDerivedFieldMethodDeclarationWithoutExpression() {
        DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(new DerivedField(), new AtomicInteger());
    }

}