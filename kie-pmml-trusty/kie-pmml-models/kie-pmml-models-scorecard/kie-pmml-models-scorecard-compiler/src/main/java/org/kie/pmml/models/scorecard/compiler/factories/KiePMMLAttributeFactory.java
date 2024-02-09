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
package org.kie.pmml.models.scorecard.compiler.factories;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Field;
import org.dmg.pmml.scorecard.Attribute;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLPredicateFactory.getKiePMMLPredicate;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLComplexPartialScoreFactory.getComplexPartialScoreVariableDeclaration;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLAttribute</code> code-generators
 * out of <code>Attribute</code>s
 */
public class KiePMMLAttributeFactory {

    private KiePMMLAttributeFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_ATTRIBUTE_TEMPLATE_JAVA = "KiePMMLAttributeTemplate.tmpl";
    static final String KIE_PMML_ATTRIBUTE_TEMPLATE = "KiePMMLAttributeTemplate";
    static final String GETKIEPMMLATTRIBUTE = "getKiePMMLAttribute";
    static final String ATTRIBUTE = "attribute";
    static final ClassOrInterfaceDeclaration ATTRIBUTE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_ATTRIBUTE_TEMPLATE_JAVA);
        ATTRIBUTE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_ATTRIBUTE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_ATTRIBUTE_TEMPLATE));
        ATTRIBUTE_TEMPLATE.getMethodsByName(GETKIEPMMLATTRIBUTE).get(0).clone();
    }

    static BlockStmt getAttributeVariableDeclaration(final String variableName,
                                                     final Attribute attribute,
                                                     final List<Field<?>> fields) {
        final MethodDeclaration methodDeclaration = ATTRIBUTE_TEMPLATE.getMethodsByName(GETKIEPMMLATTRIBUTE).get(0).clone();
        final BlockStmt attributeBody = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(attributeBody, ATTRIBUTE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, ATTRIBUTE, attributeBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        String predicateVariableName =  String.format("%s_Predicate", variableName);
        BlockStmt toAdd = getKiePMMLPredicate(predicateVariableName, attribute.getPredicate(), fields);
        toAdd.getStatements().forEach(toReturn::addStatement);

        final Expression complexPartialScoreExpression;
        if (attribute.getComplexPartialScore() != null) {
            String complexPartialScoreVariableName = String.format("%s_ComplexPartialScore", variableName);
            toAdd = getComplexPartialScoreVariableDeclaration(complexPartialScoreVariableName, attribute.getComplexPartialScore());
            toAdd.getStatements().forEach(toReturn::addStatement);
            complexPartialScoreExpression = new NameExpr(complexPartialScoreVariableName);
        } else {
            complexPartialScoreExpression = new NullLiteralExpr();
        }

        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, ATTRIBUTE, attributeBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(variableName));
        builder.setArgument(2, new NameExpr(predicateVariableName));
        getChainedMethodCallExprFrom("withPartialScore", initializer).setArgument(0, getExpressionForObject(attribute.getPartialScore()));
        getChainedMethodCallExprFrom("withComplexPartialScore", initializer).setArgument(0, complexPartialScoreExpression);
        attributeBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
