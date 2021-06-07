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
package org.kie.pmml.compiler.commons.utils;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;

import static org.kie.pmml.compiler.commons.utils.KiePMMLApplyFactory.getApplyVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLConstantFactory.getConstantVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLFieldRefFactory.getFieldRefVariableDeclaration;

/**
 * Facade for actual implementations
 */
public class KiePMMLExpressionFactory {

    private static final String EXPRESSION_NOT_MANAGED = "Expression %s not managed";

    private KiePMMLExpressionFactory() {
        // Avoid instantiation
    }

    static BlockStmt getKiePMMLExpression(final String variableName, final org.dmg.pmml.Expression expression) {
        /*if (expression instanceof Aggregate) {
            return null;
        } else*/ if (expression instanceof Apply) {
            return getApplyVariableDeclaration(variableName, (Apply) expression);
        } else if (expression instanceof Constant) {
            return getConstantVariableDeclaration(variableName, (Constant) expression);
//        } else if (expression instanceof Discretize) {
//            return null;
        } else if (expression instanceof FieldRef) {
            return getFieldRefVariableDeclaration(variableName, (FieldRef) expression);
        } else if (expression instanceof Lag) {
            return null;
        } else if (expression instanceof MapValues) {
            return null;
        } else if (expression instanceof NormContinuous) {
            return null;
        } else if (expression instanceof NormDiscrete) {
            return null;
        } else if (expression instanceof TextIndex) {
            return null;
        } else {
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
        }
    }


}
