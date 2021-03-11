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
package org.kie.kogito.codegen.openapi.client.io;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecMockServer;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

class PathResolverTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void verifyClasspathResolver(final KogitoBuildContext.Builder contextBuilder) {
        final String[] resources = new String[] { "specs/__files/petstore.json", "classpath://specs/__files/petstore.json" };
        final KogitoBuildContext context = contextBuilder.build();
        for (String resource : resources) {
            final OpenApiSpecDescriptor openApiSpecDescriptor = new OpenApiSpecDescriptor(resource);
            final PathResolver resolver = PathResolverFactory.newResolver(openApiSpecDescriptor, context);
            assertThat(resolver).isInstanceOf(ClasspathResolver.class);
            final String path = resolver.resolve(openApiSpecDescriptor);
            this.assertResolverPath(path);
        }
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void verifyFileResolver(final KogitoBuildContext.Builder contextBuilder) {
        final String resource = "specs/__files/petstore.json";
        final KogitoBuildContext context = contextBuilder.build();
        final String classpathPath = requireNonNull(getClass().getClassLoader().getResource(resource)).getPath();
        final OpenApiSpecDescriptor openApiSpecDescriptor = new OpenApiSpecDescriptor("file://" + classpathPath);
        final PathResolver resolver = PathResolverFactory.newResolver(openApiSpecDescriptor, context);
        assertThat(resolver).isInstanceOf(FileResolver.class);
        final String path = resolver.resolve(openApiSpecDescriptor);
        this.assertResolverPath(path);
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    @ExtendWith(OpenApiSpecMockServer.class)
    void verifyHTTPResolver(final KogitoBuildContext.Builder contextBuilder) {
        final String resource = "http://localhost:8989/petstore.json";
        final KogitoBuildContext context = contextBuilder.build();
        final OpenApiSpecDescriptor openApiSpecDescriptor = new OpenApiSpecDescriptor(resource);
        final PathResolver resolver = PathResolverFactory.newResolver(openApiSpecDescriptor, context);
        assertThat(resolver).isInstanceOf(HTTPResolver.class);
        final String path = resolver.resolve(openApiSpecDescriptor);
        this.assertResolverPath(path);
    }

    void assertResolverPath(final String actual) {
        assertThat(actual).isNotEmpty();
        assertThat(actual).endsWith("petstore.json");
        assertThat(Files.exists(Paths.get(actual))).isTrue();
    }
}