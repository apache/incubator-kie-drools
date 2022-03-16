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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.jbpm.ruleflow.core.Metadata;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagResourceGeneratorTest {

    @Test
    void addTags() {
        CompilationUnit compilationUnit = createCompilationUnit();

        KogitoWorkflowProcess process = mockProcessWithTags("knowledge", "is", "everything");

        TagResourceGenerator.addTags(compilationUnit, process);

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).get(0);

        Stream<AnnotationExpr> tagAnnotations = classOrInterfaceDeclaration.getAnnotations().stream()
                .filter(a -> a.getNameAsString().equals("Tag"));

        assertThat(tagAnnotations).containsExactly(
                createTagAnnotation("knowledge"),
                createTagAnnotation("is"),
                createTagAnnotation("everything"));
    }

    private static KogitoWorkflowProcess mockProcessWithTags(String... tagNames) {
        KogitoWorkflowProcess process = mock(KogitoWorkflowProcess.class);

        Collection<Tag> tags = Arrays.stream(tagNames)
                .map(tag -> OASFactory.createObject(Tag.class).name(tag))
                .collect(Collectors.toUnmodifiableList());

        when(process.getMetaData()).thenReturn(Collections.singletonMap(Metadata.TAGS, tags));

        return process;
    }

    private static CompilationUnit createCompilationUnit() {
        InputStream file = Objects.requireNonNull(TagResourceGeneratorTest.class.getResourceAsStream("/openapi/SimpleResource.java"));
        return StaticJavaParser.parse(file);
    }

    @Test
    void emptyTagsShouldResultInNoAnnotations() {
        CompilationUnit compilationUnit = createCompilationUnit();

        TagResourceGenerator.addTags(compilationUnit, mockProcessWithTags());

        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class).get(0);

        Stream<AnnotationExpr> tagAnnotations = classOrInterfaceDeclaration.getAnnotations().stream()
                .filter(a -> a.getNameAsString().equals("Tag"));

        assertThat(tagAnnotations).isEmpty();
    }

    private static AnnotationExpr createTagAnnotation(String name) {
        NodeList<MemberValuePair> attributes = new NodeList<>(new MemberValuePair("name", new StringLiteralExpr(name)));
        return new NormalAnnotationExpr(new Name("Tag"), attributes);
    }
}
