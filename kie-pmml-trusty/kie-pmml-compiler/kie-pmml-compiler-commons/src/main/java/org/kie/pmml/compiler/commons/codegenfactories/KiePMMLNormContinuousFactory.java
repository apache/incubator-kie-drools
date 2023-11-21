/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.commons.codegenfactories;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.LinearNorm;
import org.dmg.pmml.NormContinuous;
import org.kie.pmml.api.enums.OUTLIER_TREATMENT_METHOD;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLNormContinuous</code> code-generators
 * out of <code>NormContinuous</code>s
 */
public class KiePMMLNormContinuousFactory {

    static final String KIE_PMML_NORMCONTINUOUS_TEMPLATE_JAVA = "KiePMMLNormContinuousTemplate.tmpl";
    static final String KIE_PMML_NORMCONTINUOUS_TEMPLATE = "KiePMMLNormContinuousTemplate";
    static final String GETKIEPMMLNORMCONTINUOUS = "getKiePMMLNormContinuous";
    static final String NORM_CONTINUOUS = "normContinuous";
    static final ClassOrInterfaceDeclaration NORMCONTINUOUS_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_NORMCONTINUOUS_TEMPLATE_JAVA);
        NORMCONTINUOUS_TEMPLATE = cloneCU.getClassByName(KIE_PMML_NORMCONTINUOUS_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_NORMCONTINUOUS_TEMPLATE));
        NORMCONTINUOUS_TEMPLATE.getMethodsByName(GETKIEPMMLNORMCONTINUOUS).get(0).clone();
    }

    private KiePMMLNormContinuousFactory() {
        // Avoid instantiation
    }

    static BlockStmt getNormContinuousVariableDeclaration(final String variableName,
                                                          final NormContinuous normContinuous) {
        final MethodDeclaration methodDeclaration =
                NORMCONTINUOUS_TEMPLATE.getMethodsByName(GETKIEPMMLNORMCONTINUOUS).get(0).clone();
        final BlockStmt toReturn =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(toReturn, NORM_CONTINUOUS).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, NORM_CONTINUOUS, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      NORM_CONTINUOUS, toReturn)))
                .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(normContinuous.getField());
        final OUTLIER_TREATMENT_METHOD outlierTreatmentMethod =
                OUTLIER_TREATMENT_METHOD.byName(normContinuous.getOutliers().value());
        final NameExpr outlierTreatmentMethodExpr =
                new NameExpr(OUTLIER_TREATMENT_METHOD.class.getName() + "." + outlierTreatmentMethod.name());

        NodeList<Expression> arguments = new NodeList<>();
        int counter = 0;
        for (LinearNorm linearNorm : normContinuous.getLinearNorms()) {
            arguments.add(getNewKiePMMLLinearNormExpression(linearNorm, "LinearNorm-" + counter));
        }
        final Expression mapMissingToExpr = getExpressionForObject(normContinuous.getMapMissingTo());
        objectCreationExpr.getArguments().set(0, nameExpr);
        objectCreationExpr.getArguments().get(2).asMethodCallExpr().setArguments(arguments);
        objectCreationExpr.getArguments().set(3, outlierTreatmentMethodExpr);
        objectCreationExpr.getArguments().set(4, mapMissingToExpr);
        return toReturn;
    }

    static Expression getNewKiePMMLLinearNormExpression(LinearNorm linearNorm, String name) {
        ObjectCreationExpr toReturn = new ObjectCreationExpr();
        toReturn.setType(KiePMMLLinearNorm.class);
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(new StringLiteralExpr(name));
        arguments.add(new MethodCallExpr(new NameExpr("Collections"), "emptyList"));
        arguments.add(getExpressionForObject(linearNorm.getOrig()));
        arguments.add(getExpressionForObject(linearNorm.getNorm()));
        toReturn.setArguments(arguments);
        return toReturn;
    }
}
