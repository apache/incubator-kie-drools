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
package org.kie.kogito.codegen.api.template;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.codegen.api.template.TemplatedGenerator.Builder;
import static org.kie.kogito.codegen.api.template.TemplatedGenerator.TEMPLATE_SUFFIX;
import static org.kie.kogito.codegen.api.template.TemplatedGenerator.builder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplatedGeneratorTest {

    static final String JAVA = "Java";
    static final String QUARKUS = "Quarkus";
    static final String SPRING = "Spring";
    static final KogitoBuildContext context = mock(KogitoBuildContext.class);
    static final String templateName = "TestResource";

    @BeforeAll
    public static void init() {
        when(context.name()).thenReturn(JAVA);
        when(context.getPackageName()).thenReturn(KogitoBuildContext.DEFAULT_PACKAGE_NAME);
    }

    @Test
    public void baseCheck() {
        Builder templateBuilder = builder();
        assertThatThrownBy(() -> templateBuilder.build(null, templateName))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("context");
        assertThatThrownBy(() -> templateBuilder.build(context, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("templateName");

        TemplatedGenerator generator = templateBuilder
                .build(context, templateName);

        assertThat(generator).isNotNull();
        assertThat(generator.packageName).isEqualTo(context.getPackageName());

        assertThat(generator.templateBasePath).isEqualTo(templateBuilder.templateBasePath);

        assertThat(generator.fallbackContext).isNull();
        assertThat(generator.templateName()).isEqualTo(templateName);
        assertThat(generator.targetTypeName()).isEqualTo(templateName);

        assertThat(generator.context).isEqualTo(context);
        assertThat(generator.targetTypeName).isEqualTo(templateName);
        assertThat(generator.generatedFilePath()).endsWith(".java");
        assertThat(generator.generatedFilePath()).contains(templateName);
    }

    @Test
    public void templateBasePath() {
        String templateBasePath = "myPath";
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withTemplateBasePath(templateBasePath)
                .build(context, templateName);

        assertThat(generator).isNotNull();

        assertThat(generator.templateBasePath)
                .contains(templateBasePath)
                .startsWith("/")
                .endsWith("/");
    }

    @Test
    public void packageName() {
        String packageName = "packageName";
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withPackageName(packageName)
                .build(context, templateName);

        assertThat(generator).isNotNull();

        assertThat(generator.packageName).isEqualTo(packageName);
        assertThat(generator.generatedFilePath()).startsWith(packageName);
    }

    @Test
    public void targetTypeName() {
        String targetTypeName = "typeName";
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withTargetTypeName(targetTypeName)
                .build(context, templateName);

        assertThat(generator).isNotNull();

        assertThat(generator.generatedFilePath()).contains(targetTypeName);
        assertThat(generator.generatedFilePath()).doesNotContain(templateName);
        assertThat(generator.targetTypeName()).isEqualTo(targetTypeName);
        assertThat(generator.templateName()).isNotEqualTo(targetTypeName);
    }

    @Test
    public void fallbackContext() {
        String fallbackContext = SPRING;
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withFallbackContext(fallbackContext)
                .build(context, templateName);

        assertThat(generator).isNotNull();

        assertThat(fallbackContext).isNotEqualTo(context.name());
        assertThat(generator.fallbackContext).isEqualTo(fallbackContext);
    }

    @Test
    public void templatePath() {
        // no fallback but resource exist
        String existingTemplate = "Test";
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .build(context, existingTemplate);

        assertThat(generator).isNotNull();

        String selectResource = generator.templatePath();

        assertThat(selectResource)
                .isNotNull()
                .endsWith(TEMPLATE_SUFFIX)
                .contains(context.name())
                .contains(existingTemplate)
                .startsWith(TemplatedGenerator.builder().templateBasePath);

        // with fallback
        String fallbackContext = SPRING;
        TemplatedGenerator generatorWithFallback = TemplatedGenerator.builder()
                .withFallbackContext(fallbackContext)
                .build(context, templateName);

        assertThat(generatorWithFallback).isNotNull();
        assertThat(context.name()).isNotEqualTo(fallbackContext);

        String selectResourceWithFallback = generatorWithFallback.templatePath();

        assertThat(selectResourceWithFallback)
                .isNotNull()
                .endsWith(TEMPLATE_SUFFIX)
                .doesNotContain(context.name())
                .contains(fallbackContext)
                .contains(templateName)
                .startsWith(TemplatedGenerator.builder().templateBasePath);

        // no fallback no resource
        TemplatedGenerator generatorNotExist = TemplatedGenerator.builder()
                .build(context, templateName);

        assertThat(generatorNotExist).isNotNull();

        String selectResourceNotExist = generatorNotExist.templatePath();

        assertThat(selectResourceNotExist).isNull();
    }

    @Test
    public void compilationUnit() {
        // template not found
        TemplatedGenerator generator = TemplatedGenerator.builder().build(context, templateName);
        String errorMessage = "error message";

        assertThat(generator.compilationUnit()).isEmpty();
        assertThatThrownBy(generator::compilationUnitOrThrow)
                .isInstanceOf(InvalidTemplateException.class);
        assertThatThrownBy(() -> generator.compilationUnitOrThrow(errorMessage))
                .isInstanceOf(InvalidTemplateException.class)
                .hasMessageContaining(errorMessage);

        // template valid
        TemplatedGenerator generatorTemplateValid = TemplatedGenerator.builder()
                .withFallbackContext(SPRING)
                .build(context, templateName);

        assertThat(context.name()).isNotEqualTo(SPRING);

        assertThat(generatorTemplateValid.compilationUnit()).isNotEmpty();
        assertThat(generatorTemplateValid.compilationUnitOrThrow()).isNotNull();
        assertThat(generatorTemplateValid.compilationUnitOrThrow(errorMessage)).isNotNull();

        // template not valid
        TemplatedGenerator generatorTemplateNotValid = TemplatedGenerator.builder()
                .withFallbackContext(QUARKUS)
                .build(context, templateName);

        assertThat(context.name()).isNotEqualTo(QUARKUS);

        assertThatThrownBy(generatorTemplateNotValid::compilationUnit)
                .isInstanceOf(TemplateInstantiationException.class)
                .hasMessageContaining(templateName);
        assertThatThrownBy(generatorTemplateNotValid::compilationUnitOrThrow)
                .isInstanceOf(TemplateInstantiationException.class)
                .hasMessageContaining(templateName);
        assertThatThrownBy(() -> generatorTemplateNotValid.compilationUnitOrThrow(errorMessage))
                .isInstanceOf(TemplateInstantiationException.class)
                .hasMessageContaining(templateName);
    }
}