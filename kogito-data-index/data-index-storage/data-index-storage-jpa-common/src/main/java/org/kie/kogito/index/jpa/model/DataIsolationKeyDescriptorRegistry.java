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

import java.util.Map;

/**
 * This registry maps each AbstractEntity subclass to its data isolation key descriptor,
 * which specifies the processId and processVersion field paths used for data isolation
 * filtering in queries. Supports fallback mechanism where rootProcessId falls back to processId when null.
 *
 * IMPORTANT: When adding a new entity class that extends AbstractEntity,
 * you MUST add its data isolation key descriptor here.
 */
public class DataIsolationKeyDescriptorRegistry {

    private static final Map<Class<? extends AbstractEntity>, DataIsolationKeyDescriptor> DESCRIPTORS = Map.ofEntries(
            Map.entry(ProcessInstanceEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processId")
                            .processVersion("version")
                            .rootProcessId("rootProcessId")
                            .rootProcessVersion("rootProcessVersion")
                            .build()),
            Map.entry(UserTaskInstanceEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processId")
                            .processVersion("processVersion")
                            .rootProcessId("rootProcessId")
                            .rootProcessVersion("rootProcessVersion")
                            .build()),
            Map.entry(JobEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processId")
                            .processVersion("processVersion")
                            .rootProcessId("rootProcessId")
                            .rootProcessVersion("rootProcessVersion")
                            .build()),
            Map.entry(NodeInstanceEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processInstance.processId")
                            .processVersion("processInstance.version")
                            .rootProcessId("processInstance.rootProcessId")
                            .rootProcessVersion("processInstance.rootProcessVersion")
                            .build()),
            Map.entry(MilestoneEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processInstance.processId")
                            .processVersion("processInstance.version")
                            .rootProcessId("processInstance.rootProcessId")
                            .rootProcessVersion("processInstance.rootProcessVersion")
                            .build()),
            Map.entry(AttachmentEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("userTask.processId")
                            .processVersion("userTask.processVersion")
                            .rootProcessId("userTask.rootProcessId")
                            .rootProcessVersion("userTask.rootProcessVersion")
                            .build()),
            Map.entry(CommentEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("userTask.processId")
                            .processVersion("userTask.processVersion")
                            .rootProcessId("userTask.rootProcessId")
                            .rootProcessVersion("userTask.rootProcessVersion")
                            .build()),
            Map.entry(ProcessDefinitionEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("id")
                            .processVersion("version")
                            .build()),
            Map.entry(NodeEntity.class,
                    DataIsolationKeyDescriptor.builder()
                            .processId("processDefinition.id")
                            .processVersion("processDefinition.version")
                            .build()));

    /**
     * Get the data isolation key descriptor for a given entity class.
     *
     * @param entityClass the entity class
     * @return the data isolation key descriptor containing processId and processVersion paths
     * @throws RuntimeException if no descriptor is registered for the entity class
     */
    public static DataIsolationKeyDescriptor getDescriptor(Class<? extends AbstractEntity> entityClass) {
        DataIsolationKeyDescriptor descriptor = DESCRIPTORS.get(entityClass);
        if (descriptor == null) {
            throw new RuntimeException("No data isolation key descriptor registered for entity class: " +
                    entityClass.getName() + ". Add mapping to DataIsolationKeyDescriptorRegistry.DESCRIPTORS");
        }
        return descriptor;
    }

    /**
     * Get all registered descriptors.
     *
     * @return map of entity classes to their data isolation key descriptors
     */
    static Map<Class<? extends AbstractEntity>, DataIsolationKeyDescriptor> getAllDescriptors() {
        return DESCRIPTORS;
    }
}
