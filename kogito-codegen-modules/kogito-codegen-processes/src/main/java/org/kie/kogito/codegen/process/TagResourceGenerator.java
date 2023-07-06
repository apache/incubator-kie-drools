/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jbpm.ruleflow.core.Metadata;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;

/**
 * Adds {@code org.eclipse.microprofile.openapi.annotations.tags.Tag} annotations to a {@link CompilationUnit}
 */
final class TagResourceGenerator {

    private TagResourceGenerator() {
    }

    /**
     * Adds {@code org.eclipse.microprofile.openapi.annotations.tags.Tag} annotations to the specified {@link CompilationUnit}
     *
     * @param compilationUnit the compilation unit to add the {@code org.eclipse.microprofile.openapi.annotations.tags.Tag} annotations to
     * @param process the {@link KogitoWorkflowProcess} to get the tags from
     */
    static void addTags(CompilationUnit compilationUnit, KogitoWorkflowProcess process, KogitoBuildContext context) {
        if (context.hasDI()) {
            Map<String, Object> metadata = process.getMetaData();
            @SuppressWarnings("unchecked")
            Collection<String> tags = (Collection<String>) metadata.getOrDefault(Metadata.TAGS, Set.of());
            String description = (String) metadata.get(Metadata.DESCRIPTION);
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class)
                    .forEach(cls -> addTags(process, tags, description, cls, context));
        }
    }

    private static void addTags(KogitoWorkflowProcess process, Collection<String> tags, String description, ClassOrInterfaceDeclaration cls, KogitoBuildContext context) {
        tags.forEach(tag -> addTag(cls, tag, context));
        addDescription(process, description, cls, context);
    }

    private static void addDescription(KogitoWorkflowProcess process, String description, ClassOrInterfaceDeclaration cls, KogitoBuildContext context) {
        NodeList<MemberValuePair> attributes = attributes(process.getId());
        if (description != null) {
            attributes.add(new MemberValuePair("description", new StringLiteralExpr(description)));
        }
        context.getDependencyInjectionAnnotator().withTagAnnotation(cls, attributes);
    }

    private static void addTag(ClassOrInterfaceDeclaration cls, String tag, KogitoBuildContext context) {
        context.getDependencyInjectionAnnotator().withTagAnnotation(cls, attributes(tag));
    }

    private static NodeList<MemberValuePair> attributes(String tag) {
        NodeList<MemberValuePair> attributes = new NodeList<>();
        MemberValuePair name = new MemberValuePair("name", new StringLiteralExpr(tag));
        attributes.add(name);
        return attributes;
    }
}
