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

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.SimpleSetPredicate;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getObjectsFromArray;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLSimpleSetPredicate</code> code-generators
 * out of <code>SimpleSetPredicate</code>s
 */
public class KiePMMLSimpleSetPredicateFactory {

    static final String KIE_PMML_SIMPLESET_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimpleSetPredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLESET_PREDICATE_TEMPLATE = "KiePMMLSimpleSetPredicateTemplate";
    static final String GETKIEPMMLSIMPLESETPREDICATE = "getKiePMMLSimpleSetPredicate";
    static final String SIMPLESET_PREDICATE = "simpleSetPredicate";
    static final ClassOrInterfaceDeclaration SIMPLESET_PREDICATE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_SIMPLESET_PREDICATE_TEMPLATE_JAVA);
        SIMPLESET_PREDICATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_SIMPLESET_PREDICATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_SIMPLESET_PREDICATE_TEMPLATE));
        SIMPLESET_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLSIMPLESETPREDICATE).get(0).clone();
    }

    private KiePMMLSimpleSetPredicateFactory() {
        // Avoid instantiation
    }

    static BlockStmt getSimpleSetPredicateVariableDeclaration(final String variableName, final SimpleSetPredicate simpleSetPredicate) {
        final MethodDeclaration methodDeclaration =
                SIMPLESET_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLSIMPLESETPREDICATE).get(0).clone();
        final BlockStmt simpleSetPredicateBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(simpleSetPredicateBody, SIMPLESET_PREDICATE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, SIMPLESET_PREDICATE, simpleSetPredicateBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        final NodeList<Expression> arguments = new NodeList<>();
        List<Object> values = getObjectsFromArray(simpleSetPredicate.getArray());
        for (Object value : values) {
            arguments.add(getExpressionForObject(value));
        }
        final ARRAY_TYPE arrayType = ARRAY_TYPE.byName(simpleSetPredicate.getArray().getType().value());
        final NameExpr arrayTypeExpr = new NameExpr(ARRAY_TYPE.class.getName() + "." + arrayType.name());
        final IN_NOTIN inNotIn = IN_NOTIN.byName(simpleSetPredicate.getBooleanOperator().value());
        final NameExpr inNotInExpr = new NameExpr(IN_NOTIN.class.getName() + "." + inNotIn.name());
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, SIMPLESET_PREDICATE, simpleSetPredicateBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(simpleSetPredicate.getField()));
        builder.setArgument(2, arrayTypeExpr);
        builder.setArgument(3, inNotInExpr);
        getChainedMethodCallExprFrom("asList", initializer).setArguments(arguments);
        simpleSetPredicateBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
