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
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Apply;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLExpressionFactory.getKiePMMLExpressionBlockStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLApply</code> code-generators
 * out of <code>Apply</code>s
 */
public class KiePMMLApplyFactory {

    private KiePMMLApplyFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_APPLY_TEMPLATE_JAVA = "KiePMMLApplyTemplate.tmpl";
    static final String KIE_PMML_APPLY_TEMPLATE = "KiePMMLApplyTemplate";
    static final String GETKIEPMMLAPPLY = "getKiePMMLApply";
    static final String APPLY = "apply";
    static final ClassOrInterfaceDeclaration APPLY_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_APPLY_TEMPLATE_JAVA);
        APPLY_TEMPLATE = cloneCU.getClassByName(KIE_PMML_APPLY_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_APPLY_TEMPLATE));
        APPLY_TEMPLATE.getMethodsByName(GETKIEPMMLAPPLY).get(0).clone();
    }

    static BlockStmt getApplyVariableDeclaration(final String variableName, final Apply apply) {
        final MethodDeclaration methodDeclaration = APPLY_TEMPLATE.getMethodsByName(GETKIEPMMLAPPLY).get(0).clone();
        final BlockStmt applyBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(applyBody, APPLY).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, APPLY, applyBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        int counter = 0;
        final NodeList<Expression> arguments = new NodeList<>();
        for (org.dmg.pmml.Expression expression : apply.getExpressions()) {
            String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, counter);
            arguments.add(new NameExpr(nestedVariableName));
            BlockStmt toAdd = getKiePMMLExpressionBlockStmt(nestedVariableName, expression);
            toAdd.getStatements().forEach(toReturn::addStatement);
            counter++;
        }
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, APPLY, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(variableName);
        final StringLiteralExpr functionExpr = new StringLiteralExpr(apply.getFunction());
        builder.setArgument(0, nameExpr);
        builder.setArgument(2, functionExpr);
        getChainedMethodCallExprFrom("withDefaultValue", initializer).setArgument(0, getExpressionForObject(apply.getDefaultValue()));
        getChainedMethodCallExprFrom("withMapMissingTo", initializer).setArgument(0, getExpressionForObject(apply.getMapMissingTo()));
        final Expression invalidTreatmentExpr = apply.getInvalidValueTreatment() != null ? new StringLiteralExpr(apply.getInvalidValueTreatment().value()) : new NullLiteralExpr();
        getChainedMethodCallExprFrom("withInvalidValueTreatmentMethod", initializer).setArgument(0, invalidTreatmentExpr);
        getChainedMethodCallExprFrom("asList", initializer).setArguments(arguments);
        applyBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
