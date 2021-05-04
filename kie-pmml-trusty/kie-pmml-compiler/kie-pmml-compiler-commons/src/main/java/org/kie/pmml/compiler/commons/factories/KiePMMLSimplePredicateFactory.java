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
package org.kie.pmml.compiler.commons.factories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getActualValue;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLSimplePredicateFactory {

    static final String KIE_PMML_SIMPLE_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA =
            "KiePMMLSimplePredicateEvaluateMethodTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_PREDICATE_EVALUATE_METHOD_TEMPLATE = "KiePMMLSimplePredicateEvaluateMethodTemplate";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimplePredicateFactory.class.getName());

    private KiePMMLSimplePredicateFactory() {
    }

    public static BlockStmt getSimplePredicateBody(final SimplePredicate predicate, final DataType dataType) {
        OPERATOR kiePMMLOperator = OPERATOR.byName(predicate.getOperator().value());
        Object value = getActualValue(predicate.getValue(), dataType);
        try {
            String methodName;
            if (kiePMMLOperator.isNumberOperator() && !(value instanceof Number)) {
                methodName = "evaluateFALSE";
            } else {
                methodName = String.format("evaluate%s", kiePMMLOperator.name());
            }
            CompilationUnit templateEvaluate =
                    getFromFileName(KIE_PMML_SIMPLE_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA);
            CompilationUnit cloneEvaluate = templateEvaluate.clone();
            ClassOrInterfaceDeclaration evaluateTemplateClass =
                    cloneEvaluate.getClassByName(KIE_PMML_SIMPLE_PREDICATE_EVALUATE_METHOD_TEMPLATE)
                            .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
            BlockStmt toReturn = evaluateTemplateClass
                    .getMethodsByName(methodName).get(0)
                    .getBody()
                    .orElseThrow(() -> new KiePMMLInternalException("Failed to find body for " + kiePMMLOperator));
            CommonCodegenUtils.replaceStringLiteralExpressionInStatement(toReturn, "avalue", predicate.getField().getValue());
            if (kiePMMLOperator.isValueOperator() &&
                    !methodName.equals("evaluateFALSE")
                    && value != null) {
                Expression valueExpression;
                if (value instanceof String) {
                    valueExpression = new StringLiteralExpr(value.toString());
                } else {
                    valueExpression = new NameExpr(value.toString());
                }
                CommonCodegenUtils.setVariableDeclaratorValue(toReturn, "value", valueExpression);
            }
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLInternalException(e.getMessage());
        }
    }
}
