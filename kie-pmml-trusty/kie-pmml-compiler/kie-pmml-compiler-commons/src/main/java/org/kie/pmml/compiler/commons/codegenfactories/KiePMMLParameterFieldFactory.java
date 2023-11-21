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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.ParameterField;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForDataType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForOpType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLParameterField</code> code-generators
 * out of <code>ParameterField</code>s
 */
public class KiePMMLParameterFieldFactory {

    private KiePMMLParameterFieldFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_PARAMETER_FIELD_TEMPLATE_JAVA = "KiePMMLParameterFieldTemplate.tmpl";
    static final String KIE_PMML_PARAMETER_FIELD_TEMPLATE = "KiePMMLParameterFieldTemplate";
    static final String GEKIEPMMLPARAMETERFIELD = "geKiePMMLParameterField";
    static final String PARAMETER_FIELD = "parameterField";
    static final ClassOrInterfaceDeclaration PARAMETER_FIELD_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_PARAMETER_FIELD_TEMPLATE_JAVA);
        PARAMETER_FIELD_TEMPLATE = cloneCU.getClassByName(KIE_PMML_PARAMETER_FIELD_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_PARAMETER_FIELD_TEMPLATE));
        PARAMETER_FIELD_TEMPLATE.getMethodsByName(GEKIEPMMLPARAMETERFIELD).get(0).clone();
    }

    static BlockStmt getParameterFieldVariableDeclaration(final String variableName, final ParameterField parameterField) {
        final MethodDeclaration methodDeclaration = PARAMETER_FIELD_TEMPLATE.getMethodsByName(GEKIEPMMLPARAMETERFIELD).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, PARAMETER_FIELD) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, PARAMETER_FIELD, toReturn)));
        variableDeclarator.setName(variableName);
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, PARAMETER_FIELD, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(parameterField.getName()));
        final Expression dataTypeExpression = getExpressionForDataType(parameterField.getDataType());
        final Expression opTypeExpression = getExpressionForOpType(parameterField.getOpType());
        getChainedMethodCallExprFrom("withDataType", initializer).setArgument(0, dataTypeExpression);
        getChainedMethodCallExprFrom("withOpType", initializer).setArgument(0, opTypeExpression);
        getChainedMethodCallExprFrom("withDisplayName", initializer).setArgument(0, getExpressionForObject(parameterField.getDisplayName()));
        return toReturn;
    }
}
