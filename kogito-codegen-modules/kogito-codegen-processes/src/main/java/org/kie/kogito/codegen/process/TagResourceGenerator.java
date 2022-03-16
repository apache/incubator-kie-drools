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
import java.util.Collections;

import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.jbpm.ruleflow.core.Metadata;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
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
    static void addTags(CompilationUnit compilationUnit, KogitoWorkflowProcess process) {
        @SuppressWarnings("unchecked")
        Collection<Tag> tags = (Collection<Tag>) process.getMetaData().getOrDefault(Metadata.TAGS, Collections.emptyList());
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> addTags(tags, cls));
    }

    private static void addTags(Collection<Tag> tags, ClassOrInterfaceDeclaration cls) {
        tags.forEach(tag -> addTag(cls, tag));
    }

    private static void addTag(ClassOrInterfaceDeclaration cls, Tag tag) {
        NodeList<MemberValuePair> attributes = new NodeList<>();

        if (tag.getName() != null) {
            MemberValuePair name = new MemberValuePair("name", new StringLiteralExpr(tag.getName()));
            attributes.add(name);
        }

        if (tag.getDescription() != null) {
            MemberValuePair description = new MemberValuePair("description", new StringLiteralExpr(tag.getDescription()));
            attributes.add(description);
        }

        cls.addAnnotation(new NormalAnnotationExpr(new Name("Tag"), attributes));
    }
}
