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
package org.kie.kogito.codegen.rules;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.io.ByteArrayResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;

import static java.util.stream.Collectors.joining;

public class AnnotatedClassPostProcessor {

    private final List<CompilationUnit> annotatedUnits;

    public static AnnotatedClassPostProcessor of(Collection<Path> files) {
        return scan(files.stream());
    }

    public static AnnotatedClassPostProcessor scan(Stream<Path> files) {
        List<CompilationUnit> annotatedUnits = files
                .peek(System.out::println)
                .map(p -> {
                    try {
                        return StaticJavaParser.parse(p);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }).filter(cu -> cu.findFirst(AnnotationExpr.class, ann -> ann.getNameAsString().endsWith("When")).isPresent())
                .collect(Collectors.toList());

        return new AnnotatedClassPostProcessor(annotatedUnits);
    }

    public AnnotatedClassPostProcessor(List<CompilationUnit> annotatedUnits) {
        this.annotatedUnits = annotatedUnits;
    }

    public List<Resource> generate() {
        return annotatedUnits.stream().map(UnitGenerator::new).map(g -> {
            String source = g.generate();
            System.out.println(source);
            ByteArrayResource r = new ByteArrayResource(source.getBytes(StandardCharsets.UTF_8));
            r.setSourcePath(g.unitClass.getPackageDeclaration().get().getNameAsString().replace('.', '/') + '/' + g.fileName());
            r.setResourceType(ResourceType.DRL);
            return r;
        }).collect(Collectors.toList());
    }

    static class UnitGenerator {

        private final CompilationUnit unitClass;

        public UnitGenerator(CompilationUnit unitClass) {
            this.unitClass = unitClass;
        }

        String packageName() {
            return unitClass.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("");
        }

        String fileName() {
            return String.format("%s.drl", unitClass.getPrimaryTypeName().orElse(""));
        }

        String generate() {
            String imports = unitClass.getImports().stream()
                    .map(i -> String.format(
                            "import %s %s;",
                            (i.isStatic() ? "static" : ""),
                            i.getName()))
                    .collect(joining("\n"));
            TypeDeclaration<?> typeDeclaration = unitClass.getPrimaryType()
                    .orElseThrow(() -> new IllegalArgumentException("Java class should have a primary type"));
            String rules = typeDeclaration.getMethods().stream()
                    .filter(m -> m.getParameters().stream().flatMap(p -> p.getAnnotations().stream()).anyMatch(a -> a.getNameAsString().endsWith("When")))
                    .map(this::generateRule).collect(joining());
            return String.format(

                    "package %s;\n" +
                            "unit %s;\n" +
                            "%s\n" +
                            "%s\n",

                    packageName(), // package
                    typeDeclaration.getName(),
                    imports,
                    rules);
        }

        String generateRule(MethodDeclaration method) {
            String methodName = method.getName().asString();
            String patterns = method.getParameters().stream()
                    .map(this::formatPattern)
                    .collect(joining());

            String methodArgs = method.getParameters().stream()
                    .map(NodeWithSimpleName::getNameAsString)
                    .collect(joining(", "));

            return String.format(
                    "rule %s when\n" +
                            "%s" +
                            "then\n" +
                            "  unit.%s(%s);\n" +
                            "end\n",
                    methodName,
                    patterns,
                    methodName,
                    methodArgs);
        }

        private String formatPattern(Parameter el) {
            Optional<AnnotationExpr> w = el.getAnnotationByName("When");
            AnnotationExpr when = w.orElseThrow(() -> new IllegalArgumentException("No When annotation"));
            return String.format(
                    "  %s : %s\n",
                    el.getNameAsString(),
                    when.asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr().getValue());
        }
    }
}
