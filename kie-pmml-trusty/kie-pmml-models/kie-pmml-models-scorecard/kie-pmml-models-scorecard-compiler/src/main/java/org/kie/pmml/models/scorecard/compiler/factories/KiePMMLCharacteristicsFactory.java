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
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristic;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
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

    public static KiePMMLCharacteristics getKiePMMLCharacteristics(final Characteristics characteristics,
                                                                   final List<DerivedField> derivedFields,
                                                                   final DataDictionary dataDictionary,
                                                                   final String packageName,
                                                                   final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLCharacteristics {} {}", packageName, characteristics);
        String className = KiePMMLModelUtils.getGeneratedClassName("Characteristics");
        String fullClassName = packageName + "." + className;
        final Map<String, String> sourcesMap = getKiePMMLCharacteristicsSourcesMap(characteristics,
                                                                                   derivedFields,
                                                                                   dataDictionary,
                                                                                   className, packageName);
        try {
            Class<?> kiePMMLCharacteristicsClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLCharacteristics) kiePMMLCharacteristicsClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    static Map<String, String> getKiePMMLCharacteristicsSourcesMap(final Characteristics characteristics,
                                                                   final List<DerivedField> derivedFields,
                                                                   final DataDictionary dataDictionary,
                                                                   final String characteristicsClassName,
                                                                   final String packageName) {
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(characteristicsClassName,
                                                                                 packageName,
                                                                                 KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA, KIE_PMML_CHARACTERISTICS_TEMPLATE);
        final ClassOrInterfaceDeclaration characteristicsTemplate =
                cloneCU.getClassByName(characteristicsClassName)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_CHARACTERISTICS_TEMPLATE));
        setCharacteristicsVariableDeclaration(characteristicsClassName, characteristics, derivedFields, dataDictionary, characteristicsTemplate);
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + characteristicsClassName;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setCharacteristicsVariableDeclaration(final String characteristicsClassName,
                                                      final Characteristics characteristics,
                                                      final List<DerivedField> derivedFields,
                                                      final DataDictionary dataDictionary,
                                                      final ClassOrInterfaceDeclaration characteristicsTemplate) {
        int counter = 0;
        NodeList<Expression> arguments = new NodeList<>();
        for (Characteristic characteristic : characteristics.getCharacteristics()) {
            String characteristicVariableName = String.format("%s_%s", characteristicsClassName, counter);
            addGetCharacteristicMethod(characteristicVariableName, characteristic, derivedFields, dataDictionary, characteristicsTemplate);
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
                                           final List<DerivedField> derivedFields,
                                           final DataDictionary dataDictionary,
                                           final ClassOrInterfaceDeclaration characteristicsTemplate) {
        BlockStmt toAdd = getCharacteristicVariableDeclaration(characteristicVariableName, characteristic, derivedFields, dataDictionary);
        toAdd.addStatement(new ReturnStmt(characteristicVariableName));
        final MethodDeclaration methodDeclaration =
                characteristicsTemplate.addMethod("get" + characteristicVariableName).setBody(toAdd);
        methodDeclaration.setType(KiePMMLCharacteristic.class);
        methodDeclaration.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
    }
}
