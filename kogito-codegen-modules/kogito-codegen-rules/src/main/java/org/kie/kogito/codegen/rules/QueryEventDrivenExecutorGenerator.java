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

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class QueryEventDrivenExecutorGenerator extends AbstractQueryEntrypointGenerator {

    private final String dataType;

    public QueryEventDrivenExecutorGenerator(QueryGenerator queryGenerator) {
        super(queryGenerator, "EventDrivenExecutor", "EventDrivenExecutor");
        this.dataType = ruleUnit.getCanonicalName() + (context.hasDI() ? "" : "DTO");
    }

    @Override
    public GeneratedFile generate() {
        CompilationUnit cu = generator.compilationUnitOrThrow("Could not create CompilationUnit");

        ClassOrInterfaceDeclaration classDecl = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(generator, "Cannot find class declaration"));

        classDecl.setName(targetClassName);
        classDecl.findAll(ClassOrInterfaceType.class).forEach(this::interpolateClassOrInterfaceType);
        classDecl.findAll(ConstructorDeclaration.class).forEach(this::interpolateConstructorDeclaration);
        classDecl.findAll(StringLiteralExpr.class).forEach(this::interpolateStringLiteral);
        classDecl.findAll(MethodReferenceExpr.class).forEach(this::interpolateMethodReference);

        return new GeneratedFile(GeneratedFileType.SOURCE, generatedFilePath(), cu.toString());
    }

    private void interpolateMethodReference(MethodReferenceExpr input) {
        input.setScope(new NameExpr(queryClassName));
    }

    private void interpolateClassOrInterfaceType(ClassOrInterfaceType input) {
        input.setName(interpolatedTypeNameFrom(input.getNameAsString()));
    }

    private void interpolateConstructorDeclaration(ConstructorDeclaration input) {
        input.setName(interpolatedTypeNameFrom(input.getNameAsString()));
    }

    private void interpolateStringLiteral(StringLiteralExpr input) {
        input.setString(input.getValue().replace("$name$", queryName));
    }

    private String interpolatedTypeNameFrom(String input) {
        return input.replace("$QueryType$", queryClassName)
                .replace("$DataType$", dataType);
    }
}
