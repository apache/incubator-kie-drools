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
package org.kie.efesto.common.api.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedResourcesTest {

    @Test
    void add() {
        String fullClassName = "full.class.Path";
        GeneratedResource generatedClassResource = new GeneratedClassResource(fullClassName);
        String model = "foo";
        FRI fri = new FRI("this/is/fri", model);
        GeneratedResource generatedFinalResource = new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName));
        GeneratedResources generatedResources = new GeneratedResources();
        generatedResources.add(generatedClassResource);
        generatedResources.add(generatedFinalResource);
        assertThat(generatedResources).hasSize(2);

        generatedResources = new GeneratedResources();
        generatedResources.add(new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName)));
        generatedResources.add(new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName)));
        assertThat(generatedResources).hasSize(1);

        generatedResources = new GeneratedResources();
        generatedResources.add(new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName)));
        generatedResources.add(new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName)));
        assertThat(generatedResources).hasSize(1);

        generatedResources = new GeneratedResources();
        generatedResources.add(new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName)));
        generatedResources.add(new GeneratedExecutableResource(new FRI("different-fri", model), Collections.singletonList(fullClassName)));
        assertThat(generatedResources).hasSize(2);

        generatedClassResource = new GeneratedClassResource(fullClassName);
        generatedFinalResource = new GeneratedExecutableResource(fri, Collections.singletonList(fullClassName));
        generatedResources = new GeneratedResources();
        generatedResources.add(generatedClassResource);
        generatedResources.add(generatedFinalResource);
        assertThat(generatedResources).hasSize(2);
        assertThat(generatedResources.contains(generatedClassResource)).isTrue();
        assertThat(generatedResources.contains(generatedFinalResource)).isTrue();
    }

}