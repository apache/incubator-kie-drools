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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;

public class PMMLRuleMapperFactory {

    public static final String KIE_PMML_RULE_MAPPER_CLASS_NAME = "PMMLRuleMapperImpl";
    static final String KIE_PMML_RULE_MAPPER_TEMPLATE_JAVA = "PMMLRuleMapperTemplate.tmpl";


    private PMMLRuleMapperFactory() {
        // Avoid instantiation
    }

    public static String getPMMLRuleMapperSource(final String fullRuleName) {
        final String packageName = fullRuleName.contains(".") ? fullRuleName.substring(0,
                                                                                       fullRuleName.lastIndexOf('.')) : "";
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(KIE_PMML_RULE_MAPPER_CLASS_NAME, packageName, KIE_PMML_RULE_MAPPER_TEMPLATE_JAVA, KIE_PMML_RULE_MAPPER_CLASS_NAME);
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) cloneCU.getTypes().get(0);
        FieldDeclaration ruleNameField =
                typeDeclaration.getFieldByName("model").orElseThrow(() -> new RuntimeException("The template " + KIE_PMML_RULE_MAPPER_TEMPLATE_JAVA + " has been modified."));
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(fullRuleName);
        ruleNameField.getVariables().get(0).setInitializer(objectCreationExpr);
        return cloneCU.toString();
    }
}
