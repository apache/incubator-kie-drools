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
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.GeneratedFile;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.*;

class FileSystemPersistenceGeneratorTest {

    private static final String TEST_RESOURCES = "src/test/resources";
    KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
            .withApplicationProperties(new File(TEST_RESOURCES))
            .withPackageName(this.getClass().getPackage().getName())
            .withAddonsConfig(AddonsConfig.builder().withPersistence(true).build())
            .build();

    @Test
    void test() {
        context.setApplicationProperty("kogito.persistence.type", FILESYSTEM_PERSISTENCE_TYPE);

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(GeneratedPOJO.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator);
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        assertThat(generatedFiles).hasSize(1);

        Optional<GeneratedFile> persistenceFactoryImpl = generatedFiles.stream()
                .filter(gf -> gf.relativePath().equals("org/kie/kogito/persistence/KogitoProcessInstancesFactoryImpl.java"))
                .findFirst();

        assertThat(persistenceFactoryImpl).isNotEmpty();

        final CompilationUnit compilationUnit = parse(new ByteArrayInputStream(persistenceFactoryImpl.get().contents()));

        final ClassOrInterfaceDeclaration classDeclaration = compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        final Optional<MethodDeclaration> methodDeclaration = classDeclaration
                .findFirst(MethodDeclaration.class, d -> d.getName().getIdentifier().equals(PATH_NAME));

        assertThat(methodDeclaration).isNotEmpty();

        final Optional<FieldDeclaration> fieldDeclaration = classDeclaration
                .findFirst(FieldDeclaration.class);

        assertThat(fieldDeclaration).isNotEmpty();
        assertThat(fieldDeclaration.get().getVariables()).hasSize(1);
        assertThat(fieldDeclaration.get().getVariables().get(0).getName().asString()).isEqualTo(PATH_NAME);
    }
}