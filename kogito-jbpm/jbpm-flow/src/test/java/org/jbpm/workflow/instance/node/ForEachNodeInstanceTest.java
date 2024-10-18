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
package org.jbpm.workflow.instance.node;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.NodeInstance;

import static org.assertj.core.api.Assertions.assertThat;

class ForEachNodeInstanceTest {

    @Test
    void getNodeInstances() {
        ForEachNodeInstance toTest = new ForEachNodeInstance();
        CompositeNodeInstance compositeNodeInstance = new CompositeNodeInstance();
        toTest.addNodeInstance(compositeNodeInstance);
        Collection<NodeInstance> nodeInstances = toTest.getNodeInstances();
        assertThat(nodeInstances)
                .isNotNull()
                .hasSize(1)
                .contains(compositeNodeInstance);
        ForEachNodeInstance.ForEachJoinNodeInstance forEachJoinNodeInstance = toTest.new ForEachJoinNodeInstance();
        toTest.addNodeInstance(forEachJoinNodeInstance);
        nodeInstances = toTest.getNodeInstances();
        assertThat(nodeInstances)
                .isNotNull()
                .hasSize(2)
                .contains(compositeNodeInstance, forEachJoinNodeInstance);
    }

    @Test
    void getSerializableNodeInstances() {
        ForEachNodeInstance toTest = new ForEachNodeInstance();
        CompositeNodeInstance compositeNodeInstance = new CompositeNodeInstance();
        toTest.addNodeInstance(compositeNodeInstance);
        Collection<NodeInstance> serializableNodeInstances = toTest.getSerializableNodeInstances();
        assertThat(serializableNodeInstances)
                .isNotNull()
                .hasSize(1)
                .contains(compositeNodeInstance);
        ForEachNodeInstance.ForEachJoinNodeInstance forEachJoinNodeInstance = toTest.new ForEachJoinNodeInstance();
        toTest.addNodeInstance(forEachJoinNodeInstance);
        serializableNodeInstances = toTest.getSerializableNodeInstances();
        assertThat(serializableNodeInstances)
                .isNotNull()
                .hasSize(1)
                .contains(compositeNodeInstance);
    }

    @Test
    void isSerializable() {
        assertThat(ForEachNodeInstance.isSerializable(new CompositeNodeInstance())).isTrue();
        assertThat(ForEachNodeInstance.isSerializable(new ForEachNodeInstance())).isTrue();
        assertThat(ForEachNodeInstance.isSerializable(new ForEachNodeInstance().new ForEachJoinNodeInstance())).isFalse();
    }

}
