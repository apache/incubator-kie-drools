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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;

public class AnnotationGenerator implements ClassGenerator {

    private static final String TEMPLATE_NAME = "ChannelQualifier";

    private TemplatedGenerator template;
    private CompilationUnit generator;

    public AnnotationGenerator(KogitoBuildContext context, String className) {
        template = TemplatedGenerator.builder()
                .withTargetTypeName(className)
                .build(context, TEMPLATE_NAME);
        generator = template.compilationUnitOrThrow("Cannot generate " + TEMPLATE_NAME);
        AnnotationDeclaration clazz = generator.findFirst(AnnotationDeclaration.class).orElseThrow(() -> new InvalidTemplateException(template, "Cannot find class declaration"));
        clazz.setName(className);
    }

    @Override
    public String getCode() {
        return generator.toString();
    }

    @Override
    public String getPath() {
        return template.generatedFilePath();
    }
}
