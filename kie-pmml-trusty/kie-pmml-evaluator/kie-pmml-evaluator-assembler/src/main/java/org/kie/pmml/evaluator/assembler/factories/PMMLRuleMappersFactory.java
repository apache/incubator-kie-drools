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

package org.kie.pmml.evaluator.assembler.factories;

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
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

public class PMMLRuleMappersFactory {

    public static final String KIE_PMML_RULE_MAPPERS_CLASS_NAME = "PMMLRuleMappersImpl";
    static final String KIE_PMML_RULE_MAPPERS_TEMPLATE_JAVA = "PMMLRuleMappersTemplate.tmpl";

    private PMMLRuleMappersFactory() {
        // Avoid instantiation
    }

    public static String getPMMLRuleMappersSource(final String packageName, final List<String> generatedRuleMappers) {
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(KIE_PMML_RULE_MAPPERS_CLASS_NAME, packageName, KIE_PMML_RULE_MAPPERS_TEMPLATE_JAVA, KIE_PMML_RULE_MAPPERS_CLASS_NAME);
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) cloneCU.getTypes().get(0);
        FieldDeclaration ruleNameField =
                typeDeclaration.getFieldByName("pmmlRuleMappers").orElseThrow(() -> new RuntimeException("The template " + KIE_PMML_RULE_MAPPERS_TEMPLATE_JAVA + " has been modified."));
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
        return cloneCU.toString();
    }
}
