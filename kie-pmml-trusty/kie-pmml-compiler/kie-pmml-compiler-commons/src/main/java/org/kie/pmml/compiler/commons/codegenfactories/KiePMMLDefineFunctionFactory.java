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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.ParameterField;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForDataType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForOpType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLExpressionFactory.getKiePMMLExpressionBlockStmt;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLParameterFieldFactory.getParameterFieldVariableDeclaration;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDefineFunction</code> code-generators
 * out of <code>DefineFunction</code>s
 */
public class KiePMMLDefineFunctionFactory {

    static final String KIE_PMML_DEFINE_FUNCTION_TEMPLATE_JAVA = "KiePMMLDefineFunctionTemplate.tmpl";
    static final String KIE_PMML_DEFINE_FUNCTION_TEMPLATE = "KiePMMLDefineFunctionTemplate";
    static final String GETKIEPMMLDEFINEFUNCTION = "getKiePMMLDefineFunction";
    static final String DEFINE_FUNCTION = "defineFunction";
    static final ClassOrInterfaceDeclaration DEFINE_FUNCTION_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_DEFINE_FUNCTION_TEMPLATE_JAVA);
        DEFINE_FUNCTION_TEMPLATE = cloneCU.getClassByName(KIE_PMML_DEFINE_FUNCTION_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_DEFINE_FUNCTION_TEMPLATE));
        DEFINE_FUNCTION_TEMPLATE.getMethodsByName(GETKIEPMMLDEFINEFUNCTION).get(0).clone();
    }

    private KiePMMLDefineFunctionFactory() {
        // Avoid instantiation
    }

    static BlockStmt getDefineFunctionVariableDeclaration(final DefineFunction defineFunction) {
        final MethodDeclaration methodDeclaration =
                DEFINE_FUNCTION_TEMPLATE.getMethodsByName(GETKIEPMMLDEFINEFUNCTION).get(0).clone();
        final BlockStmt defineFunctionBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(defineFunctionBody, DEFINE_FUNCTION).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, DEFINE_FUNCTION, defineFunctionBody)));
        variableDeclarator.setName(defineFunction.getName());
        final BlockStmt toReturn = new BlockStmt();
        int counter = 0;
        final NodeList<Expression> parameterFieldArguments = new NodeList<>();
        for (ParameterField parameterField: defineFunction.getParameterFields()) {
            String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, defineFunction.getName(), counter);
            parameterFieldArguments.add(new NameExpr(nestedVariableName));
            BlockStmt toAdd = getParameterFieldVariableDeclaration(nestedVariableName, parameterField);
            toAdd.getStatements().forEach(toReturn::addStatement);
            counter ++;
        }
        String kiePMMLExpression = String.format("%s_Expression", defineFunction.getName());
        BlockStmt toAdd = getKiePMMLExpressionBlockStmt(kiePMMLExpression, defineFunction.getExpression());
        toAdd.getStatements().forEach(toReturn::addStatement);


        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, DEFINE_FUNCTION, toReturn)))
                .asObjectCreationExpr();
        final StringLiteralExpr nameExpr = new StringLiteralExpr(defineFunction.getName());
        objectCreationExpr.getArguments().set(0, nameExpr);
        final Expression dataTypeExpression = getExpressionForDataType(defineFunction.getDataType());
        final Expression opTypeExpression = getExpressionForOpType(defineFunction.getOpType());
        objectCreationExpr.getArguments().set(2, dataTypeExpression);
        objectCreationExpr.getArguments().set(3, opTypeExpression);
        objectCreationExpr.getArguments().get(4).asMethodCallExpr().setArguments(parameterFieldArguments);
        objectCreationExpr.getArguments().set(5, new NameExpr(kiePMMLExpression));
        defineFunctionBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
