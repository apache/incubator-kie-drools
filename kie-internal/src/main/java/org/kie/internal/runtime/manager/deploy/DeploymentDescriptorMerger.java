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
package org.kie.internal.runtime.manager.deploy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.kie.internal.runtime.conf.BuilderHandler;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;

public class DeploymentDescriptorMerger {

    private DeploymentDescriptorMerger() {}

    public static DeploymentDescriptor merge(List<DeploymentDescriptor> descriptorHierarchy, MergeMode mode) {
        if (descriptorHierarchy == null || descriptorHierarchy.isEmpty()) {
            throw new IllegalArgumentException("Descriptor hierarchy list cannot be empty");
        }
        if (descriptorHierarchy.size() == 1) {
            return descriptorHierarchy.get(0);
        }
        Deque<DeploymentDescriptor> stack = new ArrayDeque<>();
        descriptorHierarchy.forEach(stack::push);
       
        while (stack.size() > 1) {
            stack.push(merge(stack.pop(), stack.pop(), mode));
        }
        // last element from the stack is the one that contains all merged descriptors
        return stack.pop();
    }

    public static DeploymentDescriptor merge(DeploymentDescriptor primary,
                                             DeploymentDescriptor secondary,
                                             MergeMode mode) {
        if (primary == null || secondary == null) {
            throw new IllegalArgumentException("Descriptors to merge must be provided");
        }
        if (mode == null) {
            mode = MergeMode.MERGE_COLLECTIONS;
        }
        DeploymentDescriptor merged;
        DeploymentDescriptorBuilder builder = primary.getBuilder();
        builder.setBuildHandler(new MergeModeBuildHandler(mode));

        switch (mode) {
            case KEEP_ALL:
                // do nothing as primary wins
                merged = primary;
                break;
            case OVERRIDE_ALL:
                // do nothing as secondary wins
                merged = secondary;
                break;
            case OVERRIDE_EMPTY:
                builder.auditMode(secondary.getAuditMode());
                builder.auditPersistenceUnit(secondary.getAuditPersistenceUnit());
                builder.persistenceMode(secondary.getPersistenceMode());
                builder.persistenceUnit(secondary.getPersistenceUnit());
                builder.runtimeStrategy(secondary.getRuntimeStrategy());
                builder.setConfiguration(secondary.getConfiguration());
                builder.setEnvironmentEntries(secondary.getEnvironmentEntries());
                builder.setEventListeners(secondary.getEventListeners());
                builder.setGlobals(secondary.getGlobals());
                builder.setMarshalingStrategies(secondary.getMarshallingStrategies());
                builder.setTaskEventListeners(secondary.getTaskEventListeners());
                builder.setWorkItemHandlers(secondary.getWorkItemHandlers());
                builder.setRequiredRoles(secondary.getRequiredRoles());
                builder.setClasses(secondary.getClasses());
                builder.setLimitSerializationClasses(secondary.getLimitSerializationClasses());
                merged = builder.get();
                break;
            case MERGE_COLLECTIONS:
            default:
                builder.auditMode(secondary.getAuditMode());
                builder.auditPersistenceUnit(secondary.getAuditPersistenceUnit());
                builder.persistenceMode(secondary.getPersistenceMode());
                builder.persistenceUnit(secondary.getPersistenceUnit());
                builder.runtimeStrategy(secondary.getRuntimeStrategy());
                for (ObjectModel model : secondary.getEventListeners()) {
                    builder.addEventListener(model);
                }
                for (ObjectModel model : secondary.getMarshallingStrategies()) {
                    builder.addMarshalingStrategy(model);
                }
                // we need to keep the order of task listeners otherwise they will rise in different order
                // so the primary must be the latest one
                List<ObjectModel> taskEventListeners = new ArrayList<>(secondary.getTaskEventListeners());
                for (ObjectModel model : primary.getTaskEventListeners()) {
                    if (!taskEventListeners.contains(model)) {
                        taskEventListeners.add(model);
                    }
                }
                builder.setTaskEventListeners(taskEventListeners);
                for (NamedObjectModel model : secondary.getConfiguration()) {
                    builder.addConfiguration(model);
                }
                for (NamedObjectModel model : secondary.getEnvironmentEntries()) {
                    builder.addEnvironmentEntry(model);
                }
                for (NamedObjectModel model : secondary.getGlobals()) {
                    builder.addGlobal(model);
                }
                for (NamedObjectModel model : secondary.getWorkItemHandlers()) {
                    builder.addWorkItemHandler(model);
                }
                for (String requiredRole : secondary.getRequiredRoles()) {
                    builder.addRequiredRole(requiredRole);
                }
                for (String clazz : secondary.getClasses()) {
                    builder.addClass(clazz);
                }
                Boolean secondaryLimit = secondary.getLimitSerializationClasses();
                Boolean primaryLimit = primary.getLimitSerializationClasses();
                if (secondaryLimit != null && primaryLimit != null &&
                    (!secondaryLimit || !primaryLimit)) {
                    builder.setLimitSerializationClasses(false);
                }
                merged = builder.get();
                break;
        }
        return merged;
    }

    private static class MergeModeBuildHandler implements BuilderHandler {

        private MergeMode mode;

        MergeModeBuildHandler(MergeMode mode) {
            this.mode = mode;
        }

        @Override
        public boolean accepted(Object value) {
            switch (mode) {
                case KEEP_ALL:
                    return false;
                case OVERRIDE_ALL:
                    return true;
                case OVERRIDE_EMPTY:
                case MERGE_COLLECTIONS:
                default:
                    return !isEmpty(value);
            }
        }

        private boolean isEmpty(Object value) {
            if (value == null) {
                return true;
            }
            if (value instanceof String) {
                return ((String) value).isEmpty();
            }
            if (value instanceof Collection<?>) {
                return ((Collection<?>) value).isEmpty();
            }
            return false;
        }
    }
}
