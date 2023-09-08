/**
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
package org.kie.efesto.common.api.model;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootA;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootB;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedResourcesTest {

    private static String fullClassName;
    private static String model;

    private static LocalUri firstLocalUri;
    private static ModelLocalUriId firstModelLocalUriId;

    private static LocalUri secondLocalUri;

    private static ModelLocalUriId secondModelLocalUriId;

    @BeforeAll
    public static void setup() {
        fullClassName = "full.class.Path";
        model = "foo";
        firstLocalUri = new ReflectiveAppRoot(model)
                .get(ComponentRootB.class)
                .get("this", "is", "localUri")
                .asLocalUri();
        firstModelLocalUriId = new ModelLocalUriId(firstLocalUri);
        secondLocalUri = new ReflectiveAppRoot(model)
                .get(ComponentRootA.class)
                .get("this", "different-localUri")
                .asLocalUri();
        secondModelLocalUriId = new ModelLocalUriId(secondLocalUri);
    }

    @Test
    void addDifferentGeneratedResourcesClasses() {
        GeneratedResource generatedClassResource = new GeneratedClassResource(fullClassName);
        GeneratedResource generatedFinalResource = new GeneratedExecutableResource(firstModelLocalUriId,
                                                                                   Collections.singletonList(fullClassName));
        GeneratedResources generatedResources = new GeneratedResources();
        generatedResources.add(generatedClassResource);
        generatedResources.add(generatedFinalResource);
        assertThat(generatedResources).hasSize(2);
        assertThat(generatedResources.contains(generatedClassResource)).isTrue();
        assertThat(generatedResources.contains(generatedFinalResource)).isTrue();
    }

    @Test
    void addEqualsGeneratedResources() {
        GeneratedResources generatedResources = new GeneratedResources();
        generatedResources.add(new GeneratedExecutableResource(firstModelLocalUriId, Collections.singletonList(fullClassName)));
        generatedResources.add(new GeneratedExecutableResource(firstModelLocalUriId, Collections.singletonList(fullClassName)));
        assertThat(generatedResources).hasSize(1);
    }

    @Test
    void addDifferentGeneratedResourcesIds() {
        GeneratedResource firstExecutableResource = new GeneratedExecutableResource(firstModelLocalUriId, Collections.singletonList(fullClassName));
        GeneratedResource secondExecutableResource = new GeneratedExecutableResource(secondModelLocalUriId, Collections.singletonList(fullClassName));
        GeneratedResources generatedResources = new GeneratedResources();
        generatedResources.add(firstExecutableResource);
        generatedResources.add(secondExecutableResource);
        assertThat(generatedResources).hasSize(2);
        assertThat(generatedResources.contains(firstExecutableResource)).isTrue();
        assertThat(generatedResources.contains(secondExecutableResource)).isTrue();
    }

}