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
package org.kie.kogito.codegen.process;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.AbstractCodegenIT;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.context.ContextAttributesConstants;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.io.CollectedResource;
import org.kie.kogito.codegen.openapi.client.OpenApiClientCodegen;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenApiClientServerlessWorkflowIT extends AbstractCodegenIT {

    @ParameterizedTest
    @ValueSource(strings = { "openapi/petstore-classpath.sw.json" })
    public void openApiSpecInClasspath(final String resource) {
        final KogitoBuildContext context = this.newContext();
        final Collection<CollectedResource> resources = toCollectedResources(Collections.singletonList(resource));
        // OpenApi Generation
        final OpenApiClientCodegen openApiClientCodegen = OpenApiClientCodegen.ofCollectedResources(context, resources);
        assertThat(openApiClientCodegen.getOpenAPISpecResources()).isNotEmpty();
        Collection<GeneratedFile> openApiGeneratedFiles = openApiClientCodegen.generate();
        assertThat(openApiGeneratedFiles).isNotEmpty();
        assertThat(context.getContextAttribute(ContextAttributesConstants.OPENAPI_DESCRIPTORS, List.class)).isNotEmpty();
        // Process Code Generation
        final ProcessCodegen processCodegen = ProcessCodegen.ofCollectedResources(context, resources);
        Collection<GeneratedFile> processGeneratedFiles = processCodegen.generate();
        assertThat(processGeneratedFiles).isNotEmpty();
    }

    @Test
    public void testPetstoreOpenApiCodeGeneration() throws Exception {
        Map<TYPE, List<String>> resourcesTypeMap = new HashMap<>();
        resourcesTypeMap.put(TYPE.OPENAPI, Collections.singletonList("openapi/petstore-classpath.sw.json"));
        Application app = generateCode(resourcesTypeMap);
        assertThat(app).isNotNull();
    }
}
