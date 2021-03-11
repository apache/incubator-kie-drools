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
package org.kie.kogito.codegen.openapi.client.di;

import java.io.File;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.openapi.client.OpenApiClientOperation;
import org.kie.kogito.codegen.openapi.client.OpenApiSpecDescriptor;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * Configures DI in the generated OpenApi Service classes.
 */
public class ServicesConfigurationHandler extends AbstractDependencyInjectionHandler {

    private final static String API_CLIENT_PARAMETER = "ApiClient";

    protected ServicesConfigurationHandler(KogitoBuildContext context) {
        super(context);
    }

    @Override
    public ClassOrInterfaceDeclaration handle(ClassOrInterfaceDeclaration node, OpenApiSpecDescriptor descriptor, File originalGeneratedFile) {
        if (fetchServiceClasses(descriptor).anyMatch(new ClassFileEqualityFilter(originalGeneratedFile))) {
            node.getConstructorByParameterTypes(API_CLIENT_PARAMETER)
                    .ifPresent(c -> this.context.getDependencyInjectionAnnotator().withInjection(c));
            return this.context.getDependencyInjectionAnnotator().withApplicationComponent(node);
        }
        return node;
    }

    private Stream<String> fetchServiceClasses(final OpenApiSpecDescriptor descriptor) {
        return descriptor.getRequiredOperations().stream()
                .map(OpenApiClientOperation::getGeneratedClass);
    }

    private static class ClassFileEqualityFilter implements Predicate<String> {

        private static final String JAVA_EXTENSION = ".java";
        private final File file;

        ClassFileEqualityFilter(final File file) {
            this.file = file;
        }

        @Override
        public boolean test(String canonicalClassName) {
            if (file == null) {
                return false;
            }
            if (canonicalClassName == null || canonicalClassName.isEmpty()) {
                return false;
            }
            return file.getPath().endsWith(canonicalClassName.replace(".", "/") + JAVA_EXTENSION);
        }
    }
}
