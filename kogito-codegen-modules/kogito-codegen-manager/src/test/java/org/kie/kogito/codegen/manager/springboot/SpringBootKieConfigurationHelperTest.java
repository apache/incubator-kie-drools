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
package org.kie.kogito.codegen.manager.springboot;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.manager.exceptions.KogitoCodegenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.codegen.manager.CompilerHelper.RESOURCES;
import static org.kie.kogito.codegen.manager.CompilerHelper.SOURCES;
import static org.kie.kogito.codegen.manager.springboot.SpringBootKieConfigurationHelper.generateKieSpringBootConfiguration;

public class SpringBootKieConfigurationHelperTest {

    @Test
    public void testCodegenForSpringBootContext() {
        KogitoBuildContext buildContext = SpringBootKogitoBuildContext.builder()
                .build();

        Map<String, Collection<GeneratedFile>> generatedModelFiles = generateKieSpringBootConfiguration(buildContext);

        assertThat(generatedModelFiles)
                .hasSize(2)
                .hasEntrySatisfying(SOURCES, generatedFiles -> assertThat(generatedFiles).hasSize(1)
                        .satisfiesOnlyOnce(this::assertSpringBootKieConfigurationClass))
                .hasEntrySatisfying(RESOURCES, generatedFiles -> assertThat(generatedFiles).hasSize(1)
                        .satisfiesOnlyOnce(this::assertSpringBootKieConfigProperties));
    }

    private void assertSpringBootKieConfigurationClass(GeneratedFile generatedFile) {
        assertThat(generatedFile)
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", GeneratedFileType.SOURCE)
                .hasFieldOrPropertyWithValue("path", Path.of("org/kie/kogito/app/SpringBootKieConfiguration.java"))
                .hasFieldOrProperty("contents");
    }

    private void assertSpringBootKieConfigProperties(GeneratedFile generatedFile) {
        assertThat(generatedFile)
                .isNotNull()
                .hasFieldOrPropertyWithValue("type", GeneratedFileType.INTERNAL_RESOURCE)
                .hasFieldOrPropertyWithValue("path", Path.of("kie-spring-boot-config.properties"))
                .hasFieldOrProperty("contents");
    }

    @Test
    public void testCodegenForQuarkusContext() {
        KogitoBuildContext buildContext = QuarkusKogitoBuildContext.builder()
                .build();

        assertThatThrownBy(() -> generateKieSpringBootConfiguration(buildContext))
                .isInstanceOf(KogitoCodegenException.class)
                .hasMessage("Attempting to add Spring Boot KIE Configuration outside of a Spring Boot Project");
    }

    @Test
    public void testCodegenForJavaContext() {
        KogitoBuildContext buildContext = JavaKogitoBuildContext.builder()
                .build();

        assertThatThrownBy(() -> generateKieSpringBootConfiguration(buildContext))
                .isInstanceOf(KogitoCodegenException.class)
                .hasMessage("Attempting to add Spring Boot KIE Configuration outside of a Spring Boot Project");
    }
}
