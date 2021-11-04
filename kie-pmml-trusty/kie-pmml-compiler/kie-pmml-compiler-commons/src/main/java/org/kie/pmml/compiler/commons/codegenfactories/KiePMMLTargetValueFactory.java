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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLTargetValue;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTargetValue</code> code-generators
 * out of <code>KiePMMLTargetValue</code>s
 */
public class KiePMMLTargetValueFactory {

    static final String KIE_PMML_TARGETVALUE_TEMPLATE_JAVA = "KiePMMLTargetValueTemplate.tmpl";
    static final String KIE_PMML_TARGETVALUE_TEMPLATE = "KiePMMLTargetValueTemplate";
    static final String GETKIEPMMLTARGETVALUE = "getKiePMMLTargetValue";
    static final String TARGETVALUE = "targetValue";
    static final ClassOrInterfaceDeclaration TARGETVALUE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TARGETVALUE_TEMPLATE_JAVA);
        TARGETVALUE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TARGETVALUE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TARGETVALUE_TEMPLATE));
        TARGETVALUE_TEMPLATE.getMethodsByName(GETKIEPMMLTARGETVALUE).get(0).clone();
    }

    private KiePMMLTargetValueFactory() {
        // Avoid instantiation
    }

    static MethodCallExpr getKiePMMLTargetValueVariableInitializer(final KiePMMLTargetValue kiepmmlTargetValueField) {
        final MethodDeclaration methodDeclaration =
                TARGETVALUE_TEMPLATE.getMethodsByName(GETKIEPMMLTARGETVALUE).get(0).clone();
        final BlockStmt targetValueBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(targetValueBody, TARGETVALUE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TARGETVALUE, targetValueBody)));
        variableDeclarator.setName(kiepmmlTargetValueField.getName());
        final MethodCallExpr toReturn = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TARGETVALUE, targetValueBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", toReturn);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(kiepmmlTargetValueField.getName());
        builder.setArgument(0, nameExpr);
        getChainedMethodCallExprFrom("withValue", toReturn).setArgument(0,
                                                                        getExpressionForObject(kiepmmlTargetValueField.getValue()));
        getChainedMethodCallExprFrom("withDisplayValue", toReturn).setArgument(0,
                                                                               getExpressionForObject(kiepmmlTargetValueField.getDisplayValue()));
        getChainedMethodCallExprFrom("withPriorProbability", toReturn).setArgument(0,
                                                                                   getExpressionForObject(kiepmmlTargetValueField.getPriorProbability()));
        getChainedMethodCallExprFrom("withDefaultValue", toReturn).setArgument(0,
                                                                               getExpressionForObject(kiepmmlTargetValueField.getDefaultValue()));
        return toReturn;
    }
}
