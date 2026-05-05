/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.decision;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.dmn.AbstractDecisionModels.DMN_MODEL_PATHS_FILE;

class DecisionCodegenUtilsTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    void loadModelsAndValidate(KogitoBuildContext.Builder contextBuilder) {
        KogitoBuildContext context = contextBuilder.build();
        final Collection<CollectedResource> cResources = CollectedResourceProducer.fromPaths(
                Paths.get("src/test/resources/decision/models/vacationDays").toAbsolutePath());
        Map<Resource, CollectedResource> r2cr = cResources.stream().collect(Collectors.toMap(CollectedResource::resource, Function.identity()));
        Map.Entry<String, GeneratedResources> retrieved = DecisionCodegenUtils.loadModelsAndValidate(context, r2cr, Collections.emptySet(), new RuntimeTypeCheckOption(false));
        assertThat(retrieved).isNotNull();
    }

    @Test
    void generateModelPathsFile() {
        final Collection<CollectedResource> cResources = CollectedResourceProducer.fromPaths(
                Paths.get("src/test/resources/decision/models/vacationDays").toAbsolutePath(),
                Paths.get("src/test/resources/decision/alltypes").toAbsolutePath());
        Collection<GeneratedFile> generatedFiles = new ArrayList<>();
        DecisionCodegenUtils.generateModelPathsFile(generatedFiles, cResources);
        assertThat(generatedFiles).hasSize(1);
        GeneratedFile retrieved = generatedFiles.iterator().next();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.type()).isEqualTo(GeneratedFileType.INTERNAL_RESOURCE);
        assertThat(retrieved.path().toString()).isEqualTo(DMN_MODEL_PATHS_FILE);
        List<String> lines = new String(retrieved.contents()).lines().toList();
        assertThat(lines).hasSize(2);
        assertThat(lines)
                .anyMatch(line -> line.equals("/vacationDays.dmn:UTF-8"))
                .anyMatch(line -> line.equals("/OneOfEachType.dmn:UTF-8"));
    }

}
