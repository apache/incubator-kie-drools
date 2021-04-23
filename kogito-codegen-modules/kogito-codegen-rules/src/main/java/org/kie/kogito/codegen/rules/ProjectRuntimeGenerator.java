/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.rules;

import java.util.List;
import java.util.NoSuchElementException;

import org.drools.modelcompiler.builder.ModelSourceClass;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.kie.kogito.codegen.rules.IncrementalRuleCodegen.TEMPLATE_RULE_FOLDER;

public class ProjectRuntimeGenerator {

    private final ModelSourceClass.KieModuleModelMethod modelMethod;
    private final KogitoBuildContext context;
    private final TemplatedGenerator generator;

    public ProjectRuntimeGenerator(ModelSourceClass.KieModuleModelMethod modelMethod, KogitoBuildContext context) {
        this.modelMethod = modelMethod;
        this.context = context;
        this.generator = TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withPackageName("org.drools.project.model")
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .build(context, "ProjectRuntime");
    }

    public String generate() {

        CompilationUnit cu = generator.compilationUnitOrThrow("Could not create CompilationUnit");
        ClassOrInterfaceDeclaration clazz = cu
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(generator, "Compilation unit doesn't contain a class or interface declaration!"));

        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withApplicationComponent(clazz);
        }

        writeInitKieBasesMethod(clazz);
        toMethods(modelMethod.toGetKieBaseMethods()).forEach(clazz::addMember);
        toMethods(modelMethod.toNewKieSessionMethods()).forEach(clazz::addMember);
        toMethods(modelMethod.toGetKieBaseForSessionMethod()).forEach(clazz::addMember);
        toMethods(modelMethod.toKieSessionConfMethod()).forEach(clazz::addMember);

        return cu.toString();
    }

    private void writeInitKieBasesMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration initKieBasesMethod = clazz.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.getNameAsString().equals("initKieBases"))
                .findFirst()
                .orElseThrow(() -> new InvalidTemplateException(generator, "Cannot find initKieBases method"));

        IfStmt ifStmt = initKieBasesMethod.findFirst(IfStmt.class).orElseThrow(() -> new NoSuchElementException());
        BlockStmt ifBlock = ifStmt.getThenStmt().asBlockStmt();
        for (String kbaseName : modelMethod.getKieBaseNames()) {
            ifBlock.addStatement("kbaseMap.put( \"" + kbaseName + "\", " +
                    "KieBaseBuilder.createKieBaseFromModel( model.getModelsForKieBase( \"" + kbaseName + "\" ), " +
                    "model.getKieModuleModel().getKieBaseModels().get( \"" + kbaseName + "\" ) ) );\n");
        }
    }

    public String getName() {
        return generator.generatedFilePath();
    }

    private List<MethodDeclaration> toMethods(String s) {
        return parse("public class MyClass { " + s + " }").findAll(MethodDeclaration.class);
    }
}
