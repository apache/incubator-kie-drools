/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.event.model;

import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.ModelDomain;
import org.kie.kogito.event.ModelMetadata;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelEventTest {

    @Test
    public void testGetters() {
        final KogitoGAV gav = new KogitoGAV("groupID", "artifactId", "version");
        final ModelEvent e = new ModelEvent(gav,
                "name", new ModelMetadata(ModelDomain.DECISION) {
                }, ModelDomain.DECISION) {

        };

        assertThat(e.getGav().getGroupId()).isEqualTo(gav.getGroupId());
        assertThat(e.getGav().getArtifactId()).isEqualTo(gav.getArtifactId());
        assertThat(e.getGav().getVersion()).isEqualTo(gav.getVersion());
        assertThat(e.getName()).isEqualTo("name");
    }
}
