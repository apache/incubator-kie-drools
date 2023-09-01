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
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Field;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.Characteristic;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getArraysAsListInvocationMethodCall;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLAttributeFactory.getAttributeVariableDeclaration;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLCharacteristic</code> code-generators
 * out of <code>Characteristic</code>s
 */
public class KiePMMLCharacteristicFactory {

    static final String KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA = "KiePMMLCharacteristicTemplate.tmpl";
    static final String KIE_PMML_CHARACTERISTIC_TEMPLATE = "KiePMMLCharacteristicTemplate";
    static final String GETKIEPMMLCHARACTERISTIC = "getKiePMMLCharacteristic";
    static final String CHARACTERISTIC = "characteristic";
    static final ClassOrInterfaceDeclaration CHARACTERISTIC_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA);
        CHARACTERISTIC_TEMPLATE = cloneCU.getClassByName(KIE_PMML_CHARACTERISTIC_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_CHARACTERISTIC_TEMPLATE));
        CHARACTERISTIC_TEMPLATE.getMethodsByName(GETKIEPMMLCHARACTERISTIC).get(0).clone();
    }

    private KiePMMLCharacteristicFactory() {
        // Avoid instantiation
    }

    static BlockStmt getCharacteristicVariableDeclaration(final String variableName,
                                                          final Characteristic characteristic,
                                                          final List<Field<?>> fields) {
        final MethodDeclaration methodDeclaration =
                CHARACTERISTIC_TEMPLATE.getMethodsByName(GETKIEPMMLCHARACTERISTIC).get(0).clone();
        final BlockStmt characteristicBody =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(characteristicBody, CHARACTERISTIC).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, CHARACTERISTIC, characteristicBody)));
        variableDeclarator.setName(variableName);
        final BlockStmt toReturn = new BlockStmt();
        int counter = 0;
        NodeList<Expression> arguments = new NodeList<>();
        for (Attribute attribute : characteristic.getAttributes()) {
            String attributeVariableName = String.format(VARIABLE_NAME_TEMPLATE, variableName, counter);
            BlockStmt toAdd = getAttributeVariableDeclaration(attributeVariableName, attribute, fields);
            toAdd.getStatements().forEach(toReturn::addStatement);
            arguments.add(new NameExpr(attributeVariableName));
            counter++;
        }
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      CHARACTERISTIC, characteristicBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(variableName));
        builder.setArgument(2, getArraysAsListInvocationMethodCall(arguments));
        getChainedMethodCallExprFrom("withBaselineScore", initializer).setArgument(0,
                                                                                   getExpressionForObject(characteristic.getBaselineScore()));
        getChainedMethodCallExprFrom("withReasonCode", initializer).setArgument(0,
                                                                                getExpressionForObject(characteristic.getReasonCode()));
        characteristicBody.getStatements().forEach(toReturn::addStatement);
        return toReturn;
    }
}
