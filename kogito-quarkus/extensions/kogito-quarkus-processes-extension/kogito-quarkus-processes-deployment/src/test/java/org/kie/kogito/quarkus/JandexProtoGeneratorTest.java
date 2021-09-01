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
package org.kie.kogito.quarkus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.junit.jupiter.api.BeforeAll;
import org.kie.kogito.codegen.process.persistence.proto.AbstractProtoGeneratorTest;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.kie.kogito.quarkus.processes.deployment.JandexProtoGenerator;

/**
 * This class is intended to cover only JandexProtoGenerator specific tests (if any)
 *
 * NOTE: Add all tests to AbstractProtoGeneratorTest class to test both JandexProtoGenerator and ReflectionProtoGenerator
 */
class JandexProtoGeneratorTest extends AbstractProtoGeneratorTest<ClassInfo> {

    protected static Index indexWithAllClass;

    @BeforeAll
    protected static void indexOfTestClasses() {
        Indexer indexer = new Indexer();
        testClasses.forEach(clazz -> indexClass(indexer, clazz));
        indexWithAllClass = indexer.complete();
    }

    @Override
    protected ProtoGenerator.Builder<ClassInfo, JandexProtoGenerator> protoGeneratorBuilder() {
        return JandexProtoGenerator.builder(indexWithAllClass);
    }

    @Override
    protected ClassInfo convertToType(Class<?> clazz) {
        return Optional.ofNullable(indexWithAllClass.getClassByName(DotName.createSimple(clazz.getCanonicalName())))
                .orElseThrow(() -> new IllegalStateException("Class " + clazz.getCanonicalName() + " not found in the index, " +
                        "add the class to AbstractProtoGeneratorTest.testClasses collection"));
    }

    private static ClassInfo indexClass(Indexer indexer, Class<?> toIndex) {
        try {
            return indexer.index(Objects.requireNonNull(JandexProtoGenerator.class.getClassLoader()
                    .getResourceAsStream(toPath(toIndex))));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String toPath(Class<?> clazz) {
        return clazz.getCanonicalName().replace('.', '/') + ".class";
    }
}
