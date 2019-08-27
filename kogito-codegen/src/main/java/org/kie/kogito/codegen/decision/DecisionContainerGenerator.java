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

package org.kie.kogito.codegen.decision;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.decision.DecisionModels;

public class DecisionContainerGenerator extends AbstractApplicationSection {

    public DecisionContainerGenerator() {
        super("DecisionModels", "decisionModels", DecisionModels.class);
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {
        //        FieldDeclaration dmnRuntimeField = new FieldDeclaration().addModifier(Modifier.Keyword.STATIC)
        //                                                                 .addVariable(new VariableDeclarator().setType(DMNRuntime.class.getCanonicalName())
        //                                                                                                      .setName("dmnRuntime")
        //                                                                                                      .setInitializer(new MethodCallExpr("org.kie.dmn.kogito.rest.quarkus.DMNKogitoQuarkus.createGenericDMNRuntime")));
        //        ClassOrInterfaceDeclaration cls = super.classDeclaration();
        //        cls.addModifier(Modifier.Keyword.STATIC);
        //        cls.addMember(dmnRuntimeField);
        //
        //        MethodDeclaration getDecisionMethod = new MethodDeclaration().setName("getDecision")
        //                                                                     .setType(Decision.class.getCanonicalName())
        //                                                                     .addParameter(new Parameter(StaticJavaParser.parseType(String.class.getCanonicalName()), "namespace"))
        //                                                                     .addParameter(new Parameter(StaticJavaParser.parseType(String.class.getCanonicalName()), "name"))
        //        ;
        //        cls.addMember(getDecisionMethod);
        CompilationUnit clazz = StaticJavaParser.parse(this.getClass().getResourceAsStream("/class-templates/DMNApplicationClassDeclTemplate.java"));
        ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) clazz.getTypes().get(0);
        typeDeclaration.addModifier(Keyword.STATIC);
        return typeDeclaration;
    }

}
