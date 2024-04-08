/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.codegen.process.persistence;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.data.GeneratedPOJO;
import org.kie.kogito.codegen.process.persistence.marshaller.ReflectionMarshallerGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.INFINISPAN_PERSISTENCE_TYPE;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.KOGITO_PERSISTENCE_TYPE;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasDataIndexProto;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasProtoMarshaller;

class InfinispanPersistenceGeneratorTest extends AbstractPersistenceGeneratorTest {

    @ParameterizedTest
    @MethodSource("persistenceTestContexts")
    void test(KogitoBuildContext context) {
        context.setApplicationProperty(KOGITO_PERSISTENCE_TYPE, persistenceType());

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(GeneratedPOJO.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator,
                new ReflectionMarshallerGenerator(context));
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        if (context.hasDI()) {
            if (hasDataIndexProto(context)) {
                List<GeneratedFile> marshallerFiles = generatedFiles.stream().filter(gf -> gf.relativePath().endsWith("MessageMarshaller.java")).collect(Collectors.toList());

                String expectedMarshaller = "PersonMessageMarshaller";
                assertThat(marshallerFiles).hasSize(1);
                assertThat(marshallerFiles.get(0).relativePath()).endsWith(expectedMarshaller + ".java");
            }
        }
        System.out.println("marshaller " + hasProtoMarshaller(context));
        System.out.println("index " + hasDataIndexProto(context));
        int marshallerFiles = hasProtoMarshaller(context) ? 14 : 0;
        int dataIndexFiles = hasDataIndexProto(context) ? 2 : 0;
        int expectedNumberOfFiles = marshallerFiles + dataIndexFiles;
        assertThat(generatedFiles).hasSize(expectedNumberOfFiles);
    }

    @Override
    protected String persistenceType() {
        return INFINISPAN_PERSISTENCE_TYPE;
    }
}
