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
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.apache.commons.text.StringEscapeUtils;
import org.dmg.pmml.TextIndexNormalization;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLInlineTableFactory.getInlineTableVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTextIndexNormalization</code> code-generators
 * out of <code>TextIndexNormalization</code>s
 */
public class KiePMMLTextIndexNormalizationFactory {

    static final String KIE_PMML_TEXTINDEXNORMALIZATION_TEMPLATE_JAVA = "KiePMMLTextIndexNormalizationTemplate.tmpl";
    static final String KIE_PMML_TEXTINDEXNORMALIZATION_TEMPLATE = "KiePMMLTextIndexNormalizationTemplate";
    static final String GETKIEPMMLTEXTINDEXNORMALIZATION = "getKiePMMLTextIndexNormalization";
    static final String TEXTINDEXNORMALIZATION = "textIndexNormalization";
    static final ClassOrInterfaceDeclaration TEXTINDEXNORMALIZATION_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TEXTINDEXNORMALIZATION_TEMPLATE_JAVA);
        TEXTINDEXNORMALIZATION_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TEXTINDEXNORMALIZATION_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TEXTINDEXNORMALIZATION_TEMPLATE));
        TEXTINDEXNORMALIZATION_TEMPLATE.getMethodsByName(GETKIEPMMLTEXTINDEXNORMALIZATION).get(0).clone();
    }

    private KiePMMLTextIndexNormalizationFactory() {
        // Avoid instantiation
    }

    static BlockStmt getTextIndexNormalizationVariableDeclaration(final String variableName,
                                                                  final TextIndexNormalization textIndexNormalization) {
        if (textIndexNormalization.getInlineTable() == null && textIndexNormalization.getTableLocator() != null) {
            throw new UnsupportedOperationException("TableLocator not supported, yet");
        }
        final MethodDeclaration methodDeclaration =
                TEXTINDEXNORMALIZATION_TEMPLATE.getMethodsByName(GETKIEPMMLTEXTINDEXNORMALIZATION).get(0).clone();
        final BlockStmt textIndexNormalizationBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(textIndexNormalizationBody, TEXTINDEXNORMALIZATION).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TEXTINDEXNORMALIZATION, textIndexNormalizationBody)));
        variableDeclarator.setName(variableName);
        String inlineTableVariableName = String.format("%s_InlineTable", variableName);
        final BlockStmt toReturn = new BlockStmt();
        BlockStmt toAdd = getInlineTableVariableDeclaration(inlineTableVariableName,
                                                            textIndexNormalization.getInlineTable());
        toAdd.getStatements().forEach(toReturn::addStatement);
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TEXTINDEXNORMALIZATION, textIndexNormalizationBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(variableName);
        builder.setArgument(0, nameExpr);
        getChainedMethodCallExprFrom("withInField", initializer).setArgument(0,
                                                                             getExpressionForObject(textIndexNormalization.getInField()));
        getChainedMethodCallExprFrom("withOutField", initializer).setArgument(0,
                                                                              getExpressionForObject(textIndexNormalization.getOutField()));
        getChainedMethodCallExprFrom("withKiePMMLInlineTable", initializer).setArgument(0,
                                                                                        new NameExpr(inlineTableVariableName));
        getChainedMethodCallExprFrom("withRegexField", initializer).setArgument(0,
                                                                              getExpressionForObject(textIndexNormalization.getRegexField()));
        getChainedMethodCallExprFrom("withRecursive", initializer).setArgument(0,
                                                                                getExpressionForObject(textIndexNormalization.isRecursive()));
        BooleanLiteralExpr isCaseSensitiveExpression = textIndexNormalization.isCaseSensitive() != null ? (BooleanLiteralExpr) getExpressionForObject(textIndexNormalization.isCaseSensitive()) : new BooleanLiteralExpr(false);
        getChainedMethodCallExprFrom("withIsCaseSensitive", initializer).setArgument(0, isCaseSensitiveExpression);
        getChainedMethodCallExprFrom("withMaxLevenshteinDistance", initializer).setArgument(0,
                                                                               getExpressionForObject(textIndexNormalization.getMaxLevenshteinDistance()));
        Expression wordSeparatorCharacterREExpression;
        if (textIndexNormalization.getWordSeparatorCharacterRE() != null) {
            String wordSeparatorCharacterRE = StringEscapeUtils.escapeJava(textIndexNormalization.getWordSeparatorCharacterRE());
            wordSeparatorCharacterREExpression = new StringLiteralExpr(wordSeparatorCharacterRE);
        } else {
            wordSeparatorCharacterREExpression = new NullLiteralExpr();
        }
        getChainedMethodCallExprFrom("withWordSeparatorCharacterRE", initializer).setArgument(0, wordSeparatorCharacterREExpression);
        BooleanLiteralExpr tokenizeExpression = textIndexNormalization.isTokenize() != null ? (BooleanLiteralExpr) getExpressionForObject(textIndexNormalization.isTokenize()) : new BooleanLiteralExpr(false);
        getChainedMethodCallExprFrom("withTokenize", initializer).setArgument(0, tokenizeExpression);
        textIndexNormalizationBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
