/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.process.AbstractResourceGenerator;
import org.kie.kogito.codegen.process.ReactiveResourceGenerator;
import org.kie.kogito.codegen.process.ResourceGenerator;
import org.kie.kogito.codegen.process.SpringResourceGenerator;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ResourceGeneratorFactoryTest {

    public static final String MODEL_FQCN = "modelfqcn";
    public static final String PROCESS_FQCN = "processfqcn";
    public static final String APP_CANONICAL_NAME = "appCanonicalName";

    private ResourceGeneratorFactory tested;

    @Mock
    private WorkflowProcess process;

    @BeforeEach
    public void setUp() {
        lenient().when(process.getId()).thenReturn("process.id");
        lenient().when(process.getPackageName()).thenReturn("name.process");
        tested = new ResourceGeneratorFactory();
    }

    @Test
    void testCreateQuarkus() {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder().build();
        Optional<AbstractResourceGenerator> optionalAbstractResourceGenerator =
                tested.create(context,
                        process,
                        MODEL_FQCN,
                        PROCESS_FQCN,
                        APP_CANONICAL_NAME);
        assertThat(optionalAbstractResourceGenerator.isPresent()).isTrue();
        assertThat(optionalAbstractResourceGenerator.get()).isExactlyInstanceOf(ResourceGenerator.class);
    }

    @Test
    void testCreateQuarkusReactive() {
        Properties properties = new Properties();
        properties.put(GeneratorConfig.KOGITO_REST_RESOURCE_TYPE_PROP, "reactive");

        KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
                .withApplicationProperties(properties)
                .build();

        Optional<AbstractResourceGenerator> optionalAbstractResourceGenerator =
                tested.create(context,
                        process,
                        MODEL_FQCN,
                        PROCESS_FQCN,
                        APP_CANONICAL_NAME);
        assertThat(optionalAbstractResourceGenerator.isPresent()).isTrue();
        assertThat(optionalAbstractResourceGenerator.get()).isExactlyInstanceOf(ReactiveResourceGenerator.class);
    }

    @Test
    void testCreateSpring() {
        KogitoBuildContext context = SpringBootKogitoBuildContext.builder().build();
        Optional<AbstractResourceGenerator> optionalAbstractResourceGenerator =
                tested.create(context,
                        process,
                        MODEL_FQCN,
                        PROCESS_FQCN,
                        APP_CANONICAL_NAME);
        assertThat(optionalAbstractResourceGenerator.isPresent()).isTrue();
        assertThat(optionalAbstractResourceGenerator.get()).isExactlyInstanceOf(SpringResourceGenerator.class);
    }

    @Test
    void testCreateSpringReactive() {
        Properties properties = new Properties();
        properties.put(GeneratorConfig.KOGITO_REST_RESOURCE_TYPE_PROP, "reactive");

        KogitoBuildContext context = SpringBootKogitoBuildContext.builder()
                .withApplicationProperties(properties)
                .build();

        assertThrows(NoSuchElementException.class, () -> tested.create(context,
                                                                       process,
                                                                       MODEL_FQCN,
                                                                       PROCESS_FQCN,
                                                                       APP_CANONICAL_NAME));
    }
}