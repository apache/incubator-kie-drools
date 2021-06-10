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
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.False;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFalsePredicate</code> code-generators
 * out of <code>False</code>s
 */
public class KiePMMLFalsePredicateFactory {

    private KiePMMLFalsePredicateFactory() {
        // Avoid instantiation
    }

    static final String KIE_PMML_FALSEPREDICATE_TEMPLATE_TEMPLATE_JAVA = "KiePMMLFalsePredicateTemplate.tmpl";
    static final String KIE_PMML_FALSEPREDICATE_TEMPLATE = "KiePMMLFalsePredicateTemplate";
    static final String GETKIEPMMLFALSEPREDICATE = "getKiePMMLFalsePredicate";
    static final String FALSEPREDICATE = "falsePredicate";
    static final ClassOrInterfaceDeclaration FALSEPREDICATE_TEMPLATE;


    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_FALSEPREDICATE_TEMPLATE_TEMPLATE_JAVA);
        FALSEPREDICATE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_FALSEPREDICATE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_FALSEPREDICATE_TEMPLATE));
        FALSEPREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLFALSEPREDICATE).get(0).clone();
    }

    static BlockStmt getFalsePredicateVariableDeclaration(final String variableName, final False falsePredicate) {
        final MethodDeclaration methodDeclaration = FALSEPREDICATE_TEMPLATE.getMethodsByName(GETKIEPMMLFALSEPREDICATE).get(0).clone();
        final BlockStmt toReturn = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(toReturn, FALSEPREDICATE) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, FALSEPREDICATE, toReturn)));
        variableDeclarator.setName(variableName);
        final ObjectCreationExpr objectCreationExpr = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, FALSEPREDICATE, toReturn)))
        .asObjectCreationExpr();

        final StringLiteralExpr nameExpr = new StringLiteralExpr(variableName);
        objectCreationExpr.getArguments().set(0, nameExpr);
        return toReturn;
    }
}
