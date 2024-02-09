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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLExpressionFactory.getKiePMMLExpressionBlockStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLComplexPartialScore</code> code-generators
 * out of <code>ComplexPartialScore</code>s
 */
public class KiePMMLComplexPartialScoreFactory {

    private KiePMMLComplexPartialScoreFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_COMPLEX_PARTIAL_SCORE_TEMPLATE_JAVA = "KiePMMLComplexPartialScoreTemplate.tmpl";
    static final String KIE_PMML_COMPLEX_PARTIAL_SCORE_TEMPLATE = "KiePMMLComplexPartialScoreTemplate";
    static final String GETKIEPMMLCOMPLEXPARTIALSCORE = "getKiePMMLComplexPartialScore";
    static final String COMPLEX_PARTIAL_SCORE = "complexPartialScore";
    static final ClassOrInterfaceDeclaration COMPLEX_PARTIAL_SCORE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_COMPLEX_PARTIAL_SCORE_TEMPLATE_JAVA);
        COMPLEX_PARTIAL_SCORE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_COMPLEX_PARTIAL_SCORE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_COMPLEX_PARTIAL_SCORE_TEMPLATE));
        COMPLEX_PARTIAL_SCORE_TEMPLATE.getMethodsByName(GETKIEPMMLCOMPLEXPARTIALSCORE).get(0).clone();
    }

    static BlockStmt getComplexPartialScoreVariableDeclaration(final String variableName, final ComplexPartialScore complexPartialScore) {
        final MethodDeclaration methodDeclaration = COMPLEX_PARTIAL_SCORE_TEMPLATE.getMethodsByName(GETKIEPMMLCOMPLEXPARTIALSCORE).get(0).clone();
        final BlockStmt complexPartialScoreBody = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(complexPartialScoreBody, COMPLEX_PARTIAL_SCORE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, COMPLEX_PARTIAL_SCORE, complexPartialScoreBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, 0);
        BlockStmt toAdd = getKiePMMLExpressionBlockStmt(nestedVariableName, complexPartialScore.getExpression());
        toAdd.getStatements().forEach(toReturn::addStatement);

        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, COMPLEX_PARTIAL_SCORE, toReturn)))
        .asObjectCreationExpr();
        objectCreationExpr.getArguments().set(0, new StringLiteralExpr(variableName));
        objectCreationExpr.getArguments().set(2, new NameExpr(nestedVariableName));
        complexPartialScoreBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
