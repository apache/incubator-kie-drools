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
package org.kie.kogito.dmn;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.ModelDomain;
import org.kie.kogito.decision.DecisionModelMetadata;
import org.kie.kogito.decision.DecisionModelResource;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultDecisionModelResourceTest {

    private static final KogitoGAV GAV = new KogitoGAV("groupID", "artifactId", "version");

    private static final String CONTENT = "content";

    @Test
    public void testGetters() {
        final DecisionModelResource resource = new DefaultDecisionModelResource(GAV,
                "namespace",
                "name",
                new DecisionModelMetadata("http://www.omg.org/spec/DMN/20151101/dmn.xsd"),
                new InputStreamReader(new ByteArrayInputStream(CONTENT.getBytes())));
        assertThat(resource.getGav()).isEqualTo(GAV);
        assertThat(resource.getModelName()).isEqualTo("name");
        assertThat(resource.getNamespace()).isEqualTo("namespace");
        assertThat(resource.getModelMetadata().getModelDomain()).isEqualTo(ModelDomain.DECISION);
        assertThat(resource.getModelMetadata().getSpecVersion()).isEqualTo("http://www.omg.org/spec/DMN/20151101/dmn.xsd");
    }

    @Test
    public void testLoad() {
        final DecisionModelResource resource = new DefaultDecisionModelResource(GAV,
                "namespace",
                "name",
                new DecisionModelMetadata("http://www.omg.org/spec/DMN/20151101/dmn.xsd"),
                new InputStreamReader(new ByteArrayInputStream(CONTENT.getBytes())));
        assertThat(resource.get().trim()).isEqualTo(CONTENT);
    }
}
