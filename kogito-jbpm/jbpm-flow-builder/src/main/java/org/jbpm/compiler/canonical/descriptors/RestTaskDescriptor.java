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
package org.jbpm.compiler.canonical.descriptors;

import org.jbpm.compiler.canonical.ProcessMetaData;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;

import static com.github.javaparser.StaticJavaParser.parse;

public class RestTaskDescriptor implements TaskDescriptor {

    public static final String TYPE = "Rest Task";

    private final ProcessMetaData processMetadata;

    protected RestTaskDescriptor(final ProcessMetaData processMetadata) {
        this.processMetadata = processMetadata;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getName() {
        return processMetadata.getProcessId() + "RestWorkItemHandler";
    }

    @Override
    public CompilationUnit generateHandlerClassForService() {
        final String className = this.getName();
        CompilationUnit compilationUnit =
                parse(RestTaskDescriptor.class.getResourceAsStream("/class-templates/RestWorkItemHandlerTemplate.java"));
        compilationUnit.setPackageDeclaration("org.kie.kogito.handlers");
        compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(c -> c.setName(className));
        compilationUnit.findAll(ConstructorDeclaration.class).forEach(c -> c.setName(className));
        return compilationUnit;
    }
}
