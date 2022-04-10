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
package org.kie.kogito.codegen.sample.generator.config;

import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

class SampleConfigGeneratorTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void generate(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();

        SampleConfigGenerator sampleConfigGenerator = new SampleConfigGenerator(context);

        GeneratedFile generate = sampleConfigGenerator.generate();
        CompilationUnit compilationUnit = StaticJavaParser.parse(new String(generate.contents()));

        Optional<ClassOrInterfaceDeclaration> optionalClassDeclaration = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class);

        assertThat(optionalClassDeclaration).isNotEmpty();

        ClassOrInterfaceDeclaration classDeclaration = optionalClassDeclaration.get();

        if (context.hasDI()) {
            assertThat(classDeclaration.getAnnotations()).isNotEmpty();
        } else {
            assertThat(classDeclaration.getAnnotations()).isEmpty();
        }
    }
}
