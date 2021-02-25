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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import com.github.javaparser.ast.body.MethodDeclaration;

import static org.assertj.core.api.Assertions.assertThat;

class SampleContainerGeneratorTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void compilationUnit(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        Collection<SampleResource> resources = Arrays.asList(
                new SampleResource("sampleFile1", "content1"),
                new SampleResource("sampleFile2", "content2"));

        SampleContainerGenerator sampleContainerGenerator = new SampleContainerGenerator(context, resources);
        Optional<String> optionalLoadContent = sampleContainerGenerator.compilationUnit()
                .findFirst(MethodDeclaration.class, md -> "loadContent".equals(md.getName().asString()))
                .flatMap(MethodDeclaration::getBody)
                .map(Objects::toString);

        assertThat(optionalLoadContent).isNotEmpty();

        String loadContent = optionalLoadContent.get();

        resources.forEach(resource -> assertThat(loadContent)
                .contains(resource.getName())
                .contains(resource.getContent()));
    }
}
