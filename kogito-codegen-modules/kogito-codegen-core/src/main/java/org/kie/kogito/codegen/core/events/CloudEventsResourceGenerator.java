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
package org.kie.kogito.codegen.core.events;

import java.util.NoSuchElementException;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;
import org.kie.kogito.codegen.core.BodyDeclarationComparator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class CloudEventsResourceGenerator extends AbstractEventResourceGenerator {

    private static final String CLASS_NAME = "CloudEventListenerResource";

    public CloudEventsResourceGenerator(KogitoBuildContext context) {
        super(TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_EVENT_FOLDER)
                .build(context, CLASS_NAME));
    }

    /**
     * Generates the source code for a CloudEventListenerResource
     *
     * @return
     */
    public String generate() {
        final CompilationUnit clazz = generator.compilationUnitOrThrow("Cannot generate CloudEvents REST Resource");
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }
}
