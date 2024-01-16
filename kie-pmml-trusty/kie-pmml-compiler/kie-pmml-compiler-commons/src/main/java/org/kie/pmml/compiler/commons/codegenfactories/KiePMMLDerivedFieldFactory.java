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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DerivedField;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLExpressionFactory.getKiePMMLExpressionBlockStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForDataType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForOpType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDerivedField</code> code-generators
 * out of <code>DerivedField</code>s
 */
public class KiePMMLDerivedFieldFactory {

    static final String KIE_PMML_DERIVED_FIELD_TEMPLATE_JAVA = "KiePMMLDerivedFieldTemplate.tmpl";
    static final String KIE_PMML_DERIVED_FIELD_TEMPLATE = "KiePMMLDerivedFieldTemplate";
    static final String GETKIEPMMLDERIVEDFIELD = "getKiePMMLDerivedField";
    static final String DERIVED_FIELD = "derivedField";
    static final ClassOrInterfaceDeclaration DERIVED_FIELD_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_DERIVED_FIELD_TEMPLATE_JAVA);
        DERIVED_FIELD_TEMPLATE = cloneCU.getClassByName(KIE_PMML_DERIVED_FIELD_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_DERIVED_FIELD_TEMPLATE));
        DERIVED_FIELD_TEMPLATE.getMethodsByName(GETKIEPMMLDERIVEDFIELD).get(0).clone();
    }

    private KiePMMLDerivedFieldFactory() {
        // Avoid instantiation
    }

    static BlockStmt getDerivedFieldVariableDeclaration(final String variableName, final DerivedField derivedField) {
        final MethodDeclaration methodDeclaration =
                DERIVED_FIELD_TEMPLATE.getMethodsByName(GETKIEPMMLDERIVEDFIELD).get(0).clone();
        final BlockStmt derivedFieldBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(derivedFieldBody, DERIVED_FIELD).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, DERIVED_FIELD, derivedFieldBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, 0);
        BlockStmt toAdd = getKiePMMLExpressionBlockStmt(nestedVariableName, derivedField.getExpression());
        toAdd.getStatements().forEach(toReturn::addStatement);
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, DERIVED_FIELD, derivedFieldBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final Expression dataTypeExpression = getExpressionForDataType(derivedField.getDataType());
        final Expression opTypeExpression = getExpressionForOpType(derivedField.getOpType());
        builder.setArgument(0, new StringLiteralExpr(derivedField.getName()));
        builder.setArgument(2, dataTypeExpression);
        builder.setArgument(3, opTypeExpression);
        builder.setArgument(4, new NameExpr(nestedVariableName));
        getChainedMethodCallExprFrom("withDisplayName", initializer).setArgument(0, getExpressionForObject(derivedField.getDisplayName()));
        derivedFieldBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
