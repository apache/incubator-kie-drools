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
package org.kie.kogito.index.jpa.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the centralized DataIsolationKeyDescriptorRegistry for entity data isolation key descriptors.
 * This validates that all entity classes have their processId and processVersion paths properly registered.
 */
class DataIsolationKeyDescriptorRegistryTest {

    @Test
    void testProcessInstanceEntityDescriptor() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(ProcessInstanceEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("version");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("rootProcessVersion");
    }

    @Test
    void testUserTaskInstanceEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(UserTaskInstanceEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("processVersion");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("rootProcessVersion");
    }

    @Test
    void testJobEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(JobEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("processVersion");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("rootProcessVersion");
    }

    @Test
    void testAttachmentEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(AttachmentEntity.class);
        assertThat(descriptor.processId()).isEqualTo("userTask.processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("userTask.rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("userTask.processVersion");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("userTask.rootProcessVersion");
    }

    @Test
    void testCommentEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(CommentEntity.class);
        assertThat(descriptor.processId()).isEqualTo("userTask.processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("userTask.rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("userTask.processVersion");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("userTask.rootProcessVersion");
    }

    @Test
    void testNodeInstanceEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(NodeInstanceEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processInstance.processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("processInstance.rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("processInstance.version");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("processInstance.rootProcessVersion");
    }

    @Test
    void testMilestoneEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(MilestoneEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processInstance.processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("processInstance.rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("processInstance.version");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("processInstance.rootProcessVersion");
    }

    @Test
    void testNodeEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(NodeEntity.class);
        assertThat(descriptor.processId()).isEqualTo("processDefinition.id");
        assertThat(descriptor.rootProcessId()).isNull();
        assertThat(descriptor.processVersion()).isEqualTo("processDefinition.version");
        assertThat(descriptor.rootProcessVersion()).isNull();
    }

    @Test
    void testProcessDefinitionEntityPath() {
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptorRegistry.getDescriptor(ProcessDefinitionEntity.class);
        assertThat(descriptor.processId()).isEqualTo("id");
        assertThat(descriptor.rootProcessId()).isNull();
        assertThat(descriptor.processVersion()).isEqualTo("version");
        assertThat(descriptor.rootProcessVersion()).isNull();
    }

    @Test
    void testBuilderPattern() {
        // Test builder with all fields
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptor.builder()
                .processId("processId")
                .rootProcessId("rootProcessId")
                .processVersion("processVersion")
                .rootProcessVersion("rootProcessVersion")
                .build();

        assertThat(descriptor.processId()).isEqualTo("processId");
        assertThat(descriptor.rootProcessId()).isEqualTo("rootProcessId");
        assertThat(descriptor.processVersion()).isEqualTo("processVersion");
        assertThat(descriptor.rootProcessVersion()).isEqualTo("rootProcessVersion");
    }

    @Test
    void testBuilderPatternMinimal() {
        // Test builder with only required field
        DataIsolationKeyDescriptor descriptor = DataIsolationKeyDescriptor.builder()
                .processId("processId")
                .build();

        assertThat(descriptor.processId()).isEqualTo("processId");
        assertThat(descriptor.rootProcessId()).isNull();
        assertThat(descriptor.processVersion()).isNull();
        assertThat(descriptor.rootProcessVersion()).isNull();
    }
}
