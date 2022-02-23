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
package org.kie.kogito.codegen.process.persistence;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.data.GeneratedPOJO;
import org.kie.kogito.codegen.process.persistence.marshaller.ReflectionMarshallerGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.KOGITO_PERSISTENCE_TYPE;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.POSTGRESQL_PERSISTENCE_TYPE;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasDataIndexProto;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasProtoMarshaller;

class PostgrePersistenceGeneratorTest extends AbstractPersistenceGeneratorTest {

    @ParameterizedTest
    @MethodSource("persistenceTestContexts")
    void testGeneratedFiles(KogitoBuildContext context) {
        context.setApplicationProperty(KOGITO_PERSISTENCE_TYPE, persistenceType());

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(GeneratedPOJO.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator,
                new ReflectionMarshallerGenerator(context));
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        int factoryFiles = context instanceof SpringBootKogitoBuildContext ? 2 : 1;
        int marshallerFiles = hasProtoMarshaller(context) ? 14 : 0;
        int dataIndexFiles = hasDataIndexProto(context) ? 2 : 0;
        int expectedNumberOfFiles = factoryFiles + marshallerFiles + dataIndexFiles;

        assertThat(generatedFiles).hasSize(expectedNumberOfFiles);

        Optional<GeneratedFile> persistenceFactoryImpl = generatedFiles.stream()
                .filter(gf -> gf.relativePath().equals("org/kie/kogito/persistence/KogitoProcessInstancesFactoryImpl.java"))
                .findFirst();

        assertThat(persistenceFactoryImpl).isPresent();

        validateClassInCompilationUnit(persistenceFactoryImpl.get().contents());

        Optional<GeneratedFile> pgClientProducer = generatedFiles.stream()
                .filter(gf -> gf.relativePath().equals("org/kie/kogito/persistence/PgClientProducer.java"))
                .findFirst();

        if (context instanceof SpringBootKogitoBuildContext) {
            assertThat(pgClientProducer).isPresent();
            validateClassInCompilationUnit(pgClientProducer.get().contents());
        } else {
            assertThat(pgClientProducer).isEmpty();
        }
    }

    private void validateClassInCompilationUnit(byte[] contents) {
        final CompilationUnit compilationUnit = parse(new ByteArrayInputStream(contents));

        compilationUnit
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
    }

    @Override
    protected String persistenceType() {
        return POSTGRESQL_PERSISTENCE_TYPE;
    }
}
