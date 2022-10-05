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
package org.kie.kogito.codegen.process.svg;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.core.io.CollectedResourceProducer;
import org.kie.kogito.codegen.process.ProcessCodegen;

import static org.assertj.core.api.Assertions.assertThat;

public class CodegenSvgTest {

    private static final Path BASE_PATH = Paths.get("src/test/resources/svg").toAbsolutePath();
    private static final String TEST_PROCESS_SOURCE = "hiring.bpmn";
    private static final String TEST_PROCESS_GENERATED_SVG_SOURCE = "hiring-svg.svg";
    private static final String TEST_PROCESS_ID = "hiringProcessId";

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void testExportedSVGRenamedAndMoved(KogitoBuildContext.Builder contextBuilder) {

        KogitoBuildContext context = contextBuilder.withAddonsConfig(AddonsConfig.builder().withProcessSVG(true).build()).build();
        Collection<CollectedResource> collectedResources = CollectedResourceProducer.fromPaths(BASE_PATH);
        assertThat(collectedResources.stream().anyMatch(f -> f.resource().getSourcePath().endsWith(TEST_PROCESS_SOURCE))).isTrue();
        assertThat(collectedResources.stream().anyMatch(f -> f.resource().getSourcePath().endsWith(TEST_PROCESS_GENERATED_SVG_SOURCE))).isTrue();

        assertThat(collectedResources.stream().anyMatch(f -> f.resource().getSourcePath().endsWith(String.format(ProcessCodegen.SVG_EXPORT_NAME_EXPRESION, "hiring")))).isTrue();

        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(context, collectedResources);

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.type().equals(GeneratedFileType.INTERNAL_RESOURCE))
                .collect(Collectors.toList());
        assertThat(resources).hasSize(1);
        assertThat("META-INF/processSVG/" + TEST_PROCESS_ID + ".svg").isEqualTo(resources.get(0).relativePath());
    }

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void testWithoutSVGAddonNoRenamedAndMoved(KogitoBuildContext.Builder contextBuilder) {

        KogitoBuildContext context = contextBuilder.withAddonsConfig(AddonsConfig.builder().withProcessSVG(false).build()).build();
        Collection<CollectedResource> collectedResources = CollectedResourceProducer.fromPaths(BASE_PATH);
        assertThat(collectedResources.stream().anyMatch(f -> f.resource().getSourcePath().endsWith(TEST_PROCESS_SOURCE))).isTrue();
        assertThat(collectedResources.stream().anyMatch(f -> f.resource().getSourcePath().endsWith(TEST_PROCESS_GENERATED_SVG_SOURCE))).isTrue();
        ProcessCodegen codeGenerator = ProcessCodegen.ofCollectedResources(context, collectedResources);

        Collection<GeneratedFile> generatedFiles = codeGenerator.generate();
        assertThat(generatedFiles).isNotEmpty();

        List<GeneratedFile> resources = generatedFiles.stream()
                .filter(generatedFile -> generatedFile.type().equals(GeneratedFileType.INTERNAL_RESOURCE))
                .collect(Collectors.toList());
        assertThat(resources).isEmpty();
    }

    @Test
    public void TestIsFilenameValid() {
        assertThat(ProcessCodegen.isFilenameValid("processId\0.svg")).isFalse();
        assertThat(ProcessCodegen.isFilenameValid("processId.svg")).isTrue();
    }
}
