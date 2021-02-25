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
package org.kie.kogito.codegen.sample.generator;

import java.util.Collection;
import java.util.stream.Collectors;

import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class SampleContainerGenerator implements ApplicationSection {

    protected static final String SAMPLE_RUNTIME_CLASSNAME = "SampleRuntime";
    private static final String ADD_CONTENT_STATEMENT = "content.put(\"$name$\", \"$content$\");";

    private final TemplatedGenerator generator;
    private final Collection<SampleResource> sampleResources;

    public SampleContainerGenerator(KogitoBuildContext context, Collection<SampleResource> sampleResources) {
        this.generator = TemplatedGenerator.builder()
                .build(context, "SampleContainer");
        this.sampleResources = sampleResources;
    }

    @Override
    public String sectionClassName() {
        return SAMPLE_RUNTIME_CLASSNAME;
    }

    @Override
    public CompilationUnit compilationUnit() {
        CompilationUnit compilationUnit = generator.compilationUnitOrThrow();
        MethodDeclaration loadContent = compilationUnit.findFirst(MethodDeclaration.class, md -> "loadContent".equals(md.getName().asString()))
                .orElseThrow(() -> new InvalidTemplateException(generator, "Impossible to find method loadContent"));

        BlockStmt loadContentBlock = loadContent.getBody()
                .orElseThrow(() -> new InvalidTemplateException(generator, "loadContent method must have a body"));

        Collection<Statement> loadStatements = sampleResources.stream().map(SampleContainerGenerator::toLoadStatement).collect(Collectors.toList());

        loadContentBlock.getStatements().addAll(loadStatements);

        return compilationUnit;
    }

    private static Statement toLoadStatement(SampleResource resource) {
        String rawStatement = ADD_CONTENT_STATEMENT.replace("$name$", resource.getName()).replace("$content$", resource.getContent());
        return StaticJavaParser.parseStatement(rawStatement);
    }
}
