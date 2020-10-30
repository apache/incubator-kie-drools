/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static Map<String, String> getFactorySourceCode(String factoryClassName, String packageName, Set<String> generatedClasses) {
        logger.trace("getFactorySourceCode {} {} {}", factoryClassName, packageName, generatedClasses);
        String fullClassName = packageName + "." + factoryClassName;
        Map<String, String> toReturn = new HashMap<>();
        CompilationUnit templateCU = getFromFileName(KIE_PMML_MODEL_FACTORY_TEMPLATE_JAVA);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(KIE_PMML_MODEL_FACTORY_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        modelTemplate.setName(factoryClassName);
        final FieldDeclaration fieldByName = modelTemplate.getFieldByName(KIE_PMML_MODELS_FIELD)
                .orElseThrow(() -> new KiePMMLException(String.format("Failed to find FieldDeclaration %s in template %s", KIE_PMML_MODELS_FIELD, KIE_PMML_MODEL_FACTORY_TEMPLATE_JAVA)));
        populateKiePmmlFields(fieldByName, generatedClasses);
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    private static void populateKiePmmlFields(final FieldDeclaration toPopulate, Set<String> generatedClasses) {
        final VariableDeclarator variable = toPopulate.getVariable(0);
        Set<Expression> methodCallExpressions = generatedClasses.stream().map(generatedClass -> new MethodCallExpr(String.format("new %s", generatedClass))).collect(Collectors.toSet());
        NodeList<Expression> expressions = NodeList.nodeList(methodCallExpressions);
        MethodCallExpr initializer = new MethodCallExpr(new NameExpr("Arrays"), "asList", expressions);
        variable.setInitializer(initializer);
    }
}
