/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision.event.model;

import org.junit.jupiter.api.Test;
import org.kie.api.management.GAV;
import org.kie.kogito.decision.DecisionModelType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelEventTest {

    @Test
    public void testGetters() {
        final GAV gav = new GAV("groupID", "artifactId", "version");
        final ModelEvent e = new ModelEvent(ModelEvent.GAV.from(gav), "name", "namespace", DecisionModelType.DMN, "definition");

        assertEquals(gav.getGroupId(), e.getGav().getGroupId());
        assertEquals(gav.getArtifactId(), e.getGav().getArtifactId());
        assertEquals(gav.getVersion(), e.getGav().getVersion());
        assertEquals("name", e.getName());
        assertEquals("namespace", e.getNamespace());
        assertEquals(DecisionModelType.DMN, e.getType());
        assertEquals("definition", e.getDefinition());
    }
}
