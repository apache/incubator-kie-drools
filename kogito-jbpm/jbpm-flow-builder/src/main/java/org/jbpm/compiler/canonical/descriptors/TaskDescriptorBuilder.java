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
package org.jbpm.compiler.canonical.descriptors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.workflow.core.node.WorkItemNode;

import static java.util.Objects.requireNonNull;

/**
 * Creates a new {@link TaskDescriptor} based on it's name and optional parameters.
 */
public final class TaskDescriptorBuilder {

    private static final Set<String> SUPPORTED_DESCRIPTORS = new HashSet<>(Arrays.asList(RestTaskDescriptor.TYPE, ServiceTaskDescriptor.TYPE));
    private final String descriptorName;
    private WorkItemNode workItemNode;
    private ClassLoader classLoader;
    private ProcessMetaData metadata;

    public TaskDescriptorBuilder(final String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public static boolean isBuilderSupported(final String descriptorName) {
        return SUPPORTED_DESCRIPTORS.contains(descriptorName);
    }

    public TaskDescriptorBuilder withClassloader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public TaskDescriptorBuilder withWorkItemNode(final WorkItemNode workItemNode) {
        this.workItemNode = workItemNode;
        return this;
    }

    public TaskDescriptorBuilder withProcessMetadata(final ProcessMetaData metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * Builds the required {@link TaskDescriptor}
     *
     * @return the {@link TaskDescriptor} based on the given arguments
     * @throws IllegalArgumentException if the descriptor name is not implemented. Use
     */
    public TaskDescriptor build() {
        switch (this.descriptorName) {
            case (RestTaskDescriptor.TYPE):
                requireNonNull(this.metadata, "Error creating descriptor " + RestTaskDescriptor.TYPE + " ProcessMetadata can't be null");
                return new RestTaskDescriptor(this.metadata);
            case (ServiceTaskDescriptor.TYPE):
                requireNonNull(this.workItemNode, "Error creating descriptor " + ServiceTaskDescriptor.TYPE + " WorkItemNode can't be null");
                requireNonNull(this.classLoader, "Error creating descriptor " + ServiceTaskDescriptor.TYPE + " Classloader can't be null");
                return new ServiceTaskDescriptor(this.workItemNode, this.classLoader);
        }
        throw new IllegalArgumentException("Unsupported TaskDescriptor: " + this.descriptorName);
    }
}
