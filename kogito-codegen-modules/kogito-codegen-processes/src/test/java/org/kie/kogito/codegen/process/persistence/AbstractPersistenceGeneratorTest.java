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
package org.kie.kogito.codegen.process.persistence;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.data.GeneratedPOJO;
import org.kie.kogito.codegen.process.persistence.marshaller.ReflectionMarshallerGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.codegen.api.utils.KogitoContextTestUtils.contextBuilders;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.KOGITO_PERSISTENCE_PROTO_MARSHALLER;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.KOGITO_PERSISTENCE_TYPE;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasDataIndexProto;
import static org.kie.kogito.codegen.process.persistence.PersistenceGenerator.hasProtoMarshaller;

public abstract class AbstractPersistenceGeneratorTest {

    protected static final String TEST_RESOURCES = "src/test/resources";

    protected abstract String persistenceType();

    public static Stream<Arguments> persistenceTestContexts() {
        return contextBuilders()
                .map(args -> args.get()[0])
                .map(KogitoBuildContext.Builder.class::cast)
                .map(contextBuilder -> contextBuilder.withApplicationProperties(new File(TEST_RESOURCES))
                        .withPackageName(AbstractPersistenceGeneratorTest.class.getPackage().getName())
                        .withAddonsConfig(AddonsConfig.builder().withPersistence(true).build()))
                .flatMap(AbstractPersistenceGeneratorTest::initDifferentOptions)
                .map(Arguments::of);
    }

    private static Stream<KogitoBuildContext> initDifferentOptions(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext allOptionsContext = contextBuilder.build();
        allOptionsContext.setApplicationProperty(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION, "true");
        allOptionsContext.setApplicationProperty(KOGITO_PERSISTENCE_PROTO_MARSHALLER, "true");

        KogitoBuildContext noDataIndexContext = contextBuilder.build();
        noDataIndexContext.setApplicationProperty(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION, "false");
        noDataIndexContext.setApplicationProperty(KOGITO_PERSISTENCE_PROTO_MARSHALLER, "true");

        KogitoBuildContext noMarshallerContext = contextBuilder.build();
        noMarshallerContext.setApplicationProperty(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION, "true");
        noMarshallerContext.setApplicationProperty(KOGITO_PERSISTENCE_PROTO_MARSHALLER, "false");

        KogitoBuildContext noOptionsContext = contextBuilder.build();
        noOptionsContext.setApplicationProperty(KOGITO_PERSISTENCE_DATA_INDEX_PROTO_GENERATION, "false");
        noOptionsContext.setApplicationProperty(KOGITO_PERSISTENCE_PROTO_MARSHALLER, "false");

        return Stream.of(allOptionsContext, noDataIndexContext, noMarshallerContext, noOptionsContext);
    }

    @ParameterizedTest
    @MethodSource("persistenceTestContexts")
    void persistenceGeneratorSanityCheck(KogitoBuildContext context) {
        context.setApplicationProperty(KOGITO_PERSISTENCE_TYPE, persistenceType());

        ReflectionProtoGenerator protoGenerator = ReflectionProtoGenerator.builder().build(Collections.singleton(GeneratedPOJO.class));
        PersistenceGenerator persistenceGenerator = new PersistenceGenerator(
                context,
                protoGenerator,
                new ReflectionMarshallerGenerator(context, null));
        Collection<GeneratedFile> generatedFiles = persistenceGenerator.generate();

        int expectedDataIndexProto = hasDataIndexProto(context) ? 2 : 0;
        int expectedListDataIndexProto = hasDataIndexProto(context) ? 1 : 0;
        assertThat(generatedFiles.stream().filter(gf -> gf.type().equals(ProtoGenerator.PROTO_TYPE)).count()).isEqualTo(expectedDataIndexProto);
        assertThat(generatedFiles.stream().filter(gf -> gf.type().equals(ProtoGenerator.PROTO_TYPE) && gf.relativePath().endsWith(".json")).count()).isEqualTo(expectedListDataIndexProto);

        int expectedProtoMarshaller = hasProtoMarshaller(context) ? 10 : 0;
        assertThat(generatedFiles.stream().filter(gf -> gf.type().equals(GeneratedFileType.SOURCE) && gf.relativePath().endsWith("Marshaller.java"))).hasSize(expectedProtoMarshaller);
    }
}
