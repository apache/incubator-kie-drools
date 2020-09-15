/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.prediction;

import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class PredictionRuleMappersGenerator {

    private static final String TEMPLATE_JAVA = "/class-templates/PMMLRuleMappersTemplate.java";

    private PredictionRuleMappersGenerator() {
        // Avoid instantiation
    }

    public static String getPredictionRuleMappersSource(final String packageName, final List<String> generatedRuleMappers) {
        CompilationUnit clazz =
                StaticJavaParser.parse(PredictionRuleMappersGenerator.class.getResourceAsStream(TEMPLATE_JAVA)).clone();
        if (!packageName.isEmpty()) {
            clazz.setPackageDeclaration(packageName);
        }
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) clazz.getTypes().get(0);
        FieldDeclaration ruleNameField =
                typeDeclaration.getFieldByName("predictionRuleMappers").orElseThrow(() -> new RuntimeException("The template " + TEMPLATE_JAVA + " has been modified."));
        final List<Expression> nodeList = generatedRuleMappers.stream()
                .map(generatedRuleMapper -> {
                    ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                    objectCreationExpr.setType(generatedRuleMapper);
                    return objectCreationExpr;
                })
                .collect(Collectors.toList());
        NodeList<Expression> expressions = NodeList.nodeList(nodeList);
        MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("Arrays"), "asList", expressions);
        ruleNameField.getVariables().get(0).setInitializer(methodCallExpr);
        return clazz.toString();
    }
}
