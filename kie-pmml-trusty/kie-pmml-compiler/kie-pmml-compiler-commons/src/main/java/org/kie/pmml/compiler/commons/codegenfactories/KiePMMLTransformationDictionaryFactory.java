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
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDefineFunctionFactory.getDefineFunctionVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLDerivedFieldFactory.getDerivedFieldVariableDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getArraysAsListInvocation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLTransformationDictionary</code> code-generators
 * out of <code>TransformationDictionary</code>s
 */
public class KiePMMLTransformationDictionaryFactory {

    public static final String TRANSFORMATION_DICTIONARY = "transformationDictionary";
    static final String KIE_PMML_TRANSFORMATION_DICTIONARY_TEMPLATE_JAVA = "KiePMMLTransformationDictionaryTemplate.tmpl";
    static final String KIE_PMML_TRANSFORMATION_DICTIONARY_TEMPLATE = "KiePMMLTransformationDictionaryTemplate";
    static final String GETKIEPMMLTRANSFORMATIONDICTIONARY = "getKiePMMLTransformationDictionary";
    static final ClassOrInterfaceDeclaration TRANSFORMATION_DICTIONARY_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_TRANSFORMATION_DICTIONARY_TEMPLATE_JAVA);
        TRANSFORMATION_DICTIONARY_TEMPLATE = cloneCU.getClassByName(KIE_PMML_TRANSFORMATION_DICTIONARY_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_TRANSFORMATION_DICTIONARY_TEMPLATE));
        TRANSFORMATION_DICTIONARY_TEMPLATE.getMethodsByName(GETKIEPMMLTRANSFORMATIONDICTIONARY).get(0).clone();
    }

    private KiePMMLTransformationDictionaryFactory() {
        // Avoid instantiation
    }

    /**
     * @param transformationDictionary
     * @return
     */
    static BlockStmt getKiePMMLTransformationDictionaryVariableDeclaration(final TransformationDictionary transformationDictionary) {
        final MethodDeclaration methodDeclaration =
                TRANSFORMATION_DICTIONARY_TEMPLATE.getMethodsByName(GETKIEPMMLTRANSFORMATIONDICTIONARY).get(0).clone();
        final BlockStmt transformationDictionaryBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE,
                                                                                        methodDeclaration)));
        final VariableDeclarator variableDeclarator = getVariableDeclarator(transformationDictionaryBody, TRANSFORMATION_DICTIONARY) .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TRANSFORMATION_DICTIONARY, transformationDictionaryBody)));
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE, TRANSFORMATION_DICTIONARY, methodDeclaration)))
                .asMethodCallExpr();
        final BlockStmt toReturn = new BlockStmt();
        if (transformationDictionary.hasDefineFunctions()) {
            NodeList<Expression> defineFunctions = addDefineFunctions(toReturn, transformationDictionary.getDefineFunctions());
            getChainedMethodCallExprFrom("withDefineFunctions", initializer).setArguments(defineFunctions);
        }
        if (transformationDictionary.hasDerivedFields()) {
            NodeList<Expression> derivedFields = addDerivedField(toReturn, transformationDictionary.getDerivedFields());
            getChainedMethodCallExprFrom("withDerivedFields", initializer).setArguments(derivedFields);
        }
        transformationDictionaryBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }

    static NodeList<Expression> addDefineFunctions(final BlockStmt body, final List<DefineFunction> defineFunctions) {
        NodeList<Expression> arguments = new NodeList<>();
        for (DefineFunction defineFunction : defineFunctions) {
            arguments.add(new NameExpr(defineFunction.getName()));
            BlockStmt toAdd = getDefineFunctionVariableDeclaration(defineFunction);
            toAdd.getStatements().forEach(body::addStatement);
        }
        return getArraysAsListInvocation(arguments);
    }

    static NodeList<Expression> addDerivedField(final BlockStmt body, final List<DerivedField> derivedFields) {
        NodeList<Expression> arguments = new NodeList<>();
        int counter = 0;
        for (DerivedField derivedField : derivedFields) {
            String nestedVariableName = String.format("transformationDictionaryDerivedField_%s", counter);
            arguments.add(new NameExpr(nestedVariableName));
            BlockStmt toAdd = getDerivedFieldVariableDeclaration(nestedVariableName, derivedField);
            toAdd.getStatements().forEach(body::addStatement);
            counter ++;
        }
        return getArraysAsListInvocation(arguments);
    }

}
