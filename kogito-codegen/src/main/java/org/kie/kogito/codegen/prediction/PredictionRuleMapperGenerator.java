/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class PredictionRuleMapperGenerator {

    private static final String TEMPLATE_JAVA = "/class-templates/PMMLRuleMapperTemplate.java";

    private PredictionRuleMapperGenerator() {
        // Avoid instantiation
    }

    public static String getPredictionRuleMapperSource(final String fullRuleName) {
        final String packageName = fullRuleName.contains(".") ? fullRuleName.substring(0,
                                                                                       fullRuleName.lastIndexOf('.')) : "";
        CompilationUnit clazz =
                StaticJavaParser.parse(PredictionRuleMapperGenerator.class.getResourceAsStream(TEMPLATE_JAVA)).clone();
        if (!packageName.isEmpty()) {
            clazz.setPackageDeclaration(packageName);
        }
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) clazz.getTypes().get(0);
        FieldDeclaration ruleNameField =
                typeDeclaration.getFieldByName("ruleName").orElseThrow(() -> new RuntimeException("The template " + TEMPLATE_JAVA + " has been modified."));
        ruleNameField.getVariables().get(0).setInitializer(new StringLiteralExpr(fullRuleName));
        return clazz.toString();
    }
}
