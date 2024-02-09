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
import org.apache.commons.text.StringEscapeUtils;
import org.dmg.pmml.TextIndex;
import org.dmg.pmml.TextIndexNormalization;
import org.kie.pmml.api.enums.COUNT_HITS;
import org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLExpressionFactory.getKiePMMLExpressionBlockStmt;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLTextIndexNormalizationFactory.getTextIndexNormalizationVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTextIndex</code> code-generators
 * out of <code>TextIndex</code>s
 */
public class KiePMMLTextIndexFactory {

    static final String KIE_PMML_TEXTINDEX_TEMPLATE_JAVA = "KiePMMLTextIndexTemplate.tmpl";
    static final String KIE_PMML_TEXTINDEX_TEMPLATE = "KiePMMLTextIndexTemplate";
    static final String GETKIEPMMLTEXTINDEX = "getKiePMMLTextIndex";
    static final String TEXTINDEX = "textIndex";
    static final ClassOrInterfaceDeclaration TEXTINDEX_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TEXTINDEX_TEMPLATE_JAVA);
        TEXTINDEX_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TEXTINDEX_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TEXTINDEX_TEMPLATE));
        TEXTINDEX_TEMPLATE.getMethodsByName(GETKIEPMMLTEXTINDEX).get(0).clone();
    }

    private KiePMMLTextIndexFactory() {
        // Avoid instantiation
    }

    static BlockStmt getTextIndexVariableDeclaration(final String variableName, final TextIndex textIndex) {
        final MethodDeclaration methodDeclaration =
                TEXTINDEX_TEMPLATE.getMethodsByName(GETKIEPMMLTEXTINDEX).get(0).clone();
        final BlockStmt textIndexBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(textIndexBody, TEXTINDEX).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TEXTINDEX, textIndexBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        String expressionVariableName = String.format("%s_Expression", variableName);
        final BlockStmt expressionBlockStatement = getKiePMMLExpressionBlockStmt(expressionVariableName,
                                                                                 textIndex.getExpression());
        expressionBlockStatement.getStatements().forEach(toReturn::addStatement);
        int counter = 0;
        final NodeList<Expression> arguments = new NodeList<>();
        if (textIndex.hasTextIndexNormalizations()) {
            for (TextIndexNormalization textIndexNormalization : textIndex.getTextIndexNormalizations()) {
                String nestedVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, counter);
                arguments.add(new NameExpr(nestedVariableName));
                BlockStmt toAdd = getTextIndexNormalizationVariableDeclaration(nestedVariableName,
                                                                               textIndexNormalization);
                toAdd.getStatements().forEach(toReturn::addStatement);
                counter++;
            }
        }
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                TEXTINDEX, toReturn)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final StringLiteralExpr nameExpr = new StringLiteralExpr(textIndex.getTextField());
        final NameExpr expressionExpr = new NameExpr(expressionVariableName);
        builder.setArgument(0, nameExpr);
        builder.setArgument(2, expressionExpr);
        Expression localTermWeightsExpression;
        if (textIndex.getLocalTermWeights() != null) {
            final LOCAL_TERM_WEIGHTS localTermWeights =  LOCAL_TERM_WEIGHTS.byName(textIndex.getLocalTermWeights().value());
            localTermWeightsExpression = new NameExpr(LOCAL_TERM_WEIGHTS.class.getName() + "." + localTermWeights.name());
        } else {
            localTermWeightsExpression = new NullLiteralExpr();
        }
        getChainedMethodCallExprFrom("withLocalTermWeights", initializer).setArgument(0, localTermWeightsExpression);
        getChainedMethodCallExprFrom("withIsCaseSensitive", initializer).setArgument(0, getExpressionForObject(textIndex.isCaseSensitive()));
        getChainedMethodCallExprFrom("withMaxLevenshteinDistance", initializer).setArgument(0,
                                                                                            getExpressionForObject(textIndex.getMaxLevenshteinDistance()));
        Expression countHitsExpression;
        if (textIndex.getCountHits() != null) {
            final COUNT_HITS countHits =  COUNT_HITS.byName(textIndex.getCountHits().value());
            countHitsExpression = new NameExpr(COUNT_HITS.class.getName() + "." + countHits.name());
        } else {
            countHitsExpression = new NullLiteralExpr();
        }
        getChainedMethodCallExprFrom("withCountHits", initializer).setArgument(0, countHitsExpression);
        Expression wordSeparatorCharacterREExpression;
        if (textIndex.getWordSeparatorCharacterRE() != null) {
            String wordSeparatorCharacterRE = StringEscapeUtils.escapeJava(textIndex.getWordSeparatorCharacterRE());
            wordSeparatorCharacterREExpression = new StringLiteralExpr(wordSeparatorCharacterRE);
        } else {
            wordSeparatorCharacterREExpression = new NullLiteralExpr();
        }
        getChainedMethodCallExprFrom("withWordSeparatorCharacterRE", initializer).setArgument(0, wordSeparatorCharacterREExpression);
        getChainedMethodCallExprFrom("withTokenize", initializer).setArgument(0, getExpressionForObject(textIndex.isTokenize()));
        getChainedMethodCallExprFrom("asList", initializer).setArguments(arguments);
        textIndexBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
