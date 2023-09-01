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
package org.kie.pmml.compiler.commons.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

/**
 * This class is meant to create source code for PMML file-specific <b>Factory</b>
 */
public class KiePMMLFactoryFactory {

    private static final String KIE_PMML_MODEL_FACTORY_TEMPLATE_JAVA = "KiePMMLModelFactoryTemplate.tmpl";
    private static final String KIE_PMML_MODEL_FACTORY_TEMPLATE = "KiePMMLModelFactoryTemplate";
    private static final String KIE_PMML_MODELS_FIELD = "KIE_PMML_MODELS";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLFactoryFactory.class.getName());

    private KiePMMLFactoryFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getFactorySourceCode(String factoryClassName, String packageName, Map<String,
            Boolean> generatedClassesModelTypeMap) {
        logger.trace("getFactorySourceCode {} {} {}", factoryClassName, packageName, generatedClassesModelTypeMap);
        String fullClassName = packageName + "." + factoryClassName;
        Map<String, String> toReturn = new HashMap<>();
        CompilationUnit templateCU = getFromFileName(KIE_PMML_MODEL_FACTORY_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_MODEL_FACTORY_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        modelTemplate.setName(factoryClassName);
        final FieldDeclaration fieldByName = modelTemplate.getFieldByName(KIE_PMML_MODELS_FIELD)
                .orElseThrow(() -> new KiePMMLException(String.format("Failed to find FieldDeclaration %s in template" +
                                                                              " %s", KIE_PMML_MODELS_FIELD,
                                                                      KIE_PMML_MODEL_FACTORY_TEMPLATE_JAVA)));
        populateKiePmmlFields(fieldByName, generatedClassesModelTypeMap);
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    public static Expression getInstantiationExpression(String kiePMMLModelClass, boolean isInterpreted) {
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(kiePMMLModelClass);
        if (isInterpreted) {
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(new NameExpr(kiePMMLModelClass));
            methodCallExpr.setName(GET_MODEL);
            return methodCallExpr;
        } else {
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(classOrInterfaceType);
            return objectCreationExpr;
        }
    }

    static void populateKiePmmlFields(final FieldDeclaration toPopulate,
                                      Map<String, Boolean> generatedClassesModelTypeMap) {
        final VariableDeclarator variable = toPopulate.getVariable(0);
        Set<Expression> methodCallExpressions = generatedClassesModelTypeMap
                .entrySet()
                .stream()
                .map(entry -> getInstantiationExpression(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        NodeList<Expression> expressions = NodeList.nodeList(methodCallExpressions);
        MethodCallExpr initializer = new MethodCallExpr(new NameExpr("Arrays"), "asList", expressions);
        variable.setInitializer(initializer);
    }
}
