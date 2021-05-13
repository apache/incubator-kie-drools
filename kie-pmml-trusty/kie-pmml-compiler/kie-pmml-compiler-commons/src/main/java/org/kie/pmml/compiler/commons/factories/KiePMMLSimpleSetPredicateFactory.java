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

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.SimpleSetPredicate;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getObjectsFromArray;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLSimpleSetPredicateFactory {

    static final String KIE_PMML_SIMPLE_SET_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA =
            "KiePMMLSimpleSetPredicateEvaluateMethodTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_SET_PREDICATE_EVALUATE_METHOD_TEMPLATE = "KiePMMLSimpleSetPredicateEvaluateMethodTemplate";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimpleSetPredicateFactory.class.getName());

    private KiePMMLSimpleSetPredicateFactory() {
    }

    public static BlockStmt getSimpleSetPredicateBody(final SimpleSetPredicate predicate) {
        ARRAY_TYPE arrayType = ARRAY_TYPE.byName(predicate.getArray().getType().value());
        IN_NOTIN kiePMMLIN_NOTINT = IN_NOTIN.byName(predicate.getBooleanOperator().value());
        String methodName = String.format("evaluate%s", kiePMMLIN_NOTINT.name());
        try {
            CompilationUnit templateEvaluate =
                    getFromFileName(KIE_PMML_SIMPLE_SET_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA);
            CompilationUnit cloneEvaluate = templateEvaluate.clone();
            ClassOrInterfaceDeclaration evaluateTemplateClass =
                    cloneEvaluate.getClassByName(KIE_PMML_SIMPLE_SET_PREDICATE_EVALUATE_METHOD_TEMPLATE)
                            .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
            final BlockStmt toReturn = evaluateTemplateClass
                    .getMethodsByName(methodName).get(0)
                    .getBody()
                    .orElseThrow(() -> new KiePMMLInternalException("Failed to find body for " + kiePMMLIN_NOTINT));
            String arrayTypeString = arrayType.getClass().getCanonicalName() + "." + arrayType.name();
            CommonCodegenUtils.setVariableDeclaratorValue(toReturn, "arrayType", new NameExpr(arrayTypeString));
            final List<Object> values = getObjectsFromArray(predicate.getArray());
            final NodeList<Expression> valuesExpressions = new NodeList<>();
            for (Object value : values) {
                if (arrayType == ARRAY_TYPE.STRING) {
                    valuesExpressions.add(new StringLiteralExpr(value.toString()));
                } else {
                    valuesExpressions.add(new NameExpr(value.toString()));
                }
            }
            MethodCallExpr valuesInit = new MethodCallExpr();
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName("asList");
            valuesInit.setArguments(valuesExpressions);
            CommonCodegenUtils.setVariableDeclaratorValue(toReturn, "values", valuesInit);
            CommonCodegenUtils.replaceStringLiteralExpressionInStatement(toReturn, "avalue", predicate.getField().getValue());
            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLInternalException(e.getMessage());
        }
    }

}
