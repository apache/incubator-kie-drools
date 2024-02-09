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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Field;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getDataType;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLSimplePredicate</code> code-generators
 * out of <code>SimplePredicate</code>s
 */
public class KiePMMLSimplePredicateFactory {

    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimplePredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE = "KiePMMLSimplePredicateTemplate";
    static final String GETKIEPMMLSIMPLEPREDICATE = "getKiePMMLSimplePredicate";
    static final String SIMPLE_PREDICATE = "simplePredicate";
    static final ClassOrInterfaceDeclaration SIMPLE_PREDICATE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA);
        SIMPLE_PREDICATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_SIMPLE_PREDICATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_SIMPLE_PREDICATE_TEMPLATE));
        SIMPLE_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLSIMPLEPREDICATE).get(0).clone();
    }

    private KiePMMLSimplePredicateFactory() {
        // Avoid instantiation
    }

    static BlockStmt getSimplePredicateVariableDeclaration(final String variableName,
                                                           final SimplePredicate simplePredicate,
                                                           final List<Field<?>> fields) {
        final MethodDeclaration methodDeclaration =
                SIMPLE_PREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLSIMPLEPREDICATE).get(0).clone();
        final BlockStmt simplePredicateBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(simplePredicateBody, SIMPLE_PREDICATE).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, SIMPLE_PREDICATE, simplePredicateBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        final OPERATOR operator = OPERATOR.byName(simplePredicate.getOperator().value());
        final NameExpr operatorExpr = new NameExpr(OPERATOR.class.getName() + "." + operator.name());
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, SIMPLE_PREDICATE, simplePredicateBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(simplePredicate.getField()));
        builder.setArgument(2, operatorExpr);
        DataType dataType = getDataType(fields,simplePredicate.getField());
        Object actualValue = DATA_TYPE.byName(dataType.value()).getActualValue(simplePredicate.getValue());
        getChainedMethodCallExprFrom("withValue", initializer).setArgument(0, getExpressionForObject(actualValue));
        simplePredicateBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
