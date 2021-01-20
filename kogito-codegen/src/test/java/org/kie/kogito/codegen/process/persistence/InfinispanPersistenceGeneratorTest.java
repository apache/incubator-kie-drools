/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.process.persistence;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFileType;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.data.GeneratedPOJO;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.INFINISPAN_PERSISTENCE_TYPE;

class InfinispanPersistenceGeneratorTest {

    private static final String TEST_RESOURCES = "src/test/resources";
    KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
            .withApplicationProperties(new File(TEST_RESOURCES))
            .withPackageName(this.getClass().getPackage().getName())
            .withAddonsConfig(AddonsConfig.builder().withPersistence(true).build())
            .build();

    @Test
    void test() {
        context.setApplicationProperty("kogito.persistence.type", INFINISPAN_PERSISTENCE_TYPE);

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(GeneratedPOJO.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator);
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        assertThat(generatedFiles.stream().filter(gf -> gf.type().equals(ProtoGenerator.PROTO_TYPE)).count()).isEqualTo(2);
        assertThat(generatedFiles.stream().filter(gf -> gf.type().equals(ProtoGenerator.PROTO_TYPE) && gf.relativePath().endsWith(".json")).count()).isEqualTo(1);

        Optional<GeneratedFile> persistenceFactoryImpl = generatedFiles.stream()
                .filter(gf -> gf.relativePath().equals("org/kie/kogito/persistence/KogitoProcessInstancesFactoryImpl.java"))
                .findFirst();
        List<GeneratedFile> marshallerFiles = generatedFiles.stream().filter(gf -> gf.relativePath().endsWith("MessageMarshaller.java")).collect(Collectors.toList());

        String expectedMarshaller = "PersonMessageMarshaller";
        assertThat(persistenceFactoryImpl).isNotEmpty();
        assertThat(marshallerFiles.size()).isEqualTo(1);
        assertThat(marshallerFiles.get(0).relativePath()).endsWith(expectedMarshaller + ".java");

        final CompilationUnit compilationUnit = parse(new ByteArrayInputStream(persistenceFactoryImpl.get().contents()));

        final ClassOrInterfaceDeclaration classDeclaration = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        final MethodDeclaration methodDeclaration = classDeclaration
                .findFirst(MethodDeclaration.class, d -> d.getName().getIdentifier().equals("marshallers"))
                .orElseThrow(() -> new NoSuchElementException("Class declaration doesn't contain a method named \"marshallers\"!"));

        assertThat(methodDeclaration.getBody()).isNotEmpty();
        assertThat(methodDeclaration.getBody().get().toString()).contains(expectedMarshaller);
    }
}