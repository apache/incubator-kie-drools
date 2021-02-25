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

package org.kie.kogito.persistence.kafka;

import org.apache.kafka.streams.Topology;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.createTopologyForProcesses;
import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.storeName;
import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.topicName;

public class KafkaPersistenceUtilsTest {

    @Test
    public void testTopicName() {
        assertThat(topicName("aProcessId")).isEqualTo("kogito.process.aProcessId");
    }

    @Test
    public void testStoreName() {
        assertThat(storeName("aProcessId")).isEqualTo("kogito-aProcessId-store");
    }

    @Test
    public void testTopology() {
        Topology topology = createTopologyForProcesses(emptyList());
        assertThat(topology.describe().globalStores()).isEmpty();

        topology = createTopologyForProcesses(singletonList("aProcess"));
        assertThat(topology.describe().globalStores()).hasSize(1);
    }
}
