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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.dmg.pmml.Field;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.compiler.ScorecardCompilationDTO;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getArraysAsListInvocationMethodCall;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicFactory.getCharacteristicVariableDeclaration;

public class KiePMMLCharacteristicsFactory {

    static final String KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA = "KiePMMLCharacteristicsTemplate.tmpl";
    static final String KIE_PMML_CHARACTERISTICS_TEMPLATE = "KiePMMLCharacteristicsTemplate";
    static final String GETKIEPMMLCHARACTERISTICS = "getKiePMMLCharacteristics";
    static final String CHARACTERISTICS = "characteristics";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCharacteristicsFactory.class.getName());

    private KiePMMLCharacteristicsFactory() {
        // Avoid instantiation
    }

    static Map<String, String> getKiePMMLCharacteristicsSourcesMap(
            final ScorecardCompilationDTO compilationDTO) {

        final String characteristicsClassName = compilationDTO.getCharacteristicsClassName();
        final Characteristics characteristics = compilationDTO.getCharacteristics();
        final List<Field<?>> fields = compilationDTO.getFields();
        final String packageName = compilationDTO.getPackageName();
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(characteristicsClassName,
                                                                                 packageName,
                                                                                 KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA, KIE_PMML_CHARACTERISTICS_TEMPLATE);
        final ClassOrInterfaceDeclaration characteristicsTemplate =
                cloneCU.getClassByName(characteristicsClassName)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_CHARACTERISTICS_TEMPLATE));
        setCharacteristicsVariableDeclaration(characteristicsClassName, characteristics, fields,
                                              characteristicsTemplate);
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + characteristicsClassName;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setCharacteristicsVariableDeclaration(final String characteristicsClassName,
                                                      final Characteristics characteristics,
                                                      final List<Field<?>> fields,
                                                      final ClassOrInterfaceDeclaration characteristicsTemplate) {
        int counter = 0;
        NodeList<Expression> arguments = new NodeList<>();
        for (Characteristic characteristic : characteristics.getCharacteristics()) {
            String characteristicVariableName = String.format(VARIABLE_NAME_TEMPLATE, characteristicsClassName, counter);
            addGetCharacteristicMethod(characteristicVariableName, characteristic, fields, characteristicsTemplate);
            MethodCallExpr toAdd = new MethodCallExpr();
            toAdd.setScope(new NameExpr(characteristicsClassName));
            toAdd.setName("get" + characteristicVariableName);
            arguments.add(toAdd);
            counter++;
        }
        final ConstructorDeclaration constructorDeclaration =
                characteristicsTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, characteristicsTemplate.getName())));

        constructorDeclaration.setName(characteristicsClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        superStatement.setArgument(0, new StringLiteralExpr(characteristicsClassName));
        superStatement.setArgument(2, getArraysAsListInvocationMethodCall(arguments));
    }

    static void addGetCharacteristicMethod(final String characteristicVariableName,
                                           final Characteristic characteristic,
                                           final List<Field<?>> fields,
                                           final ClassOrInterfaceDeclaration characteristicsTemplate) {
        BlockStmt toAdd = getCharacteristicVariableDeclaration(characteristicVariableName, characteristic, fields);
        toAdd.addStatement(new ReturnStmt(characteristicVariableName));
        final MethodDeclaration methodDeclaration =
                characteristicsTemplate.addMethod("get" + characteristicVariableName).setBody(toAdd);
        methodDeclaration.setType(KiePMMLCharacteristic.class);
        methodDeclaration.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
    }
}
