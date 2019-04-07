/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.deploy;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.kie.internal.runtime.conf.BuilderHandler;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;

public class DeploymentDescriptorMerger {

	public DeploymentDescriptor merge(List<DeploymentDescriptor> descriptorHierarchy, MergeMode mode) {
		if (descriptorHierarchy == null || descriptorHierarchy.isEmpty()) {
			throw new IllegalArgumentException("Descriptor hierarchy list cannot be empty");
		}

		if (descriptorHierarchy.size() == 1) {
			return descriptorHierarchy.get(0);
		}
		Stack<DeploymentDescriptor> stack = new Stack<DeploymentDescriptor>();
		stack.addAll(descriptorHierarchy);
		if (mode == null) {
			mode = MergeMode.MERGE_COLLECTIONS;
		}

		while (stack.size() > 1) {
			DeploymentDescriptor master = stack.pop();
			DeploymentDescriptor slave = stack.pop();
			DeploymentDescriptor desc = merge(master, slave, mode);
			// add merged one to be next iteration slave
			stack.push(desc);
		}
		// last element from the stack is the one that contains all merged descriptors
		return stack.pop();
	}

	public DeploymentDescriptor merge(DeploymentDescriptor master, DeploymentDescriptor slave, MergeMode mode) {
		if (master == null || slave == null) {
			throw new IllegalArgumentException("Descriptors to merge must be provided");
		}

		DeploymentDescriptor merged = null;
		DeploymentDescriptorBuilder builder = master.getBuilder();
		builder.setBuildHandler(new MergeModeBuildHandler(mode));

		switch (mode) {
			case KEEP_ALL:
				// do nothing as master wins
				merged = master;
				break;
			case OVERRIDE_ALL:
				// do nothing as slave wins
				merged = slave;
				break;
			case OVERRIDE_EMPTY:
				builder.auditMode(slave.getAuditMode());
				builder.auditPersistenceUnit(slave.getAuditPersistenceUnit());
				builder.persistenceMode(slave.getPersistenceMode());
				builder.persistenceUnit(slave.getPersistenceUnit());
				builder.runtimeStrategy(slave.getRuntimeStrategy());
				builder.setConfiguration(slave.getConfiguration());
				builder.setEnvironmentEntries(slave.getEnvironmentEntries());
				builder.setEventListeners(slave.getEventListeners());
				builder.setGlobals(slave.getGlobals());
				builder.setMarshalingStrategies(slave.getMarshallingStrategies());
				builder.setTaskEventListeners(slave.getTaskEventListeners());
				builder.setWorkItemHandlers(slave.getWorkItemHandlers());
				builder.setRequiredRoles(slave.getRequiredRoles());
				builder.setClasses(slave.getClasses());
				builder.setLimitSerializationClasses(slave.getLimitSerializationClasses());

				merged = builder.get();
				break;

			case MERGE_COLLECTIONS:

				builder.auditMode(slave.getAuditMode());
				builder.auditPersistenceUnit(slave.getAuditPersistenceUnit());
				builder.persistenceMode(slave.getPersistenceMode());
				builder.persistenceUnit(slave.getPersistenceUnit());
				builder.runtimeStrategy(slave.getRuntimeStrategy());

				for (ObjectModel model : slave.getEventListeners()) {
					builder.addEventListener(model);
				}
				for (ObjectModel model : slave.getMarshallingStrategies()) {
					builder.addMarshalingStrategy(model);
				}
				for (ObjectModel model : slave.getTaskEventListeners()) {
					builder.addTaskEventListener(model);
				}
				for (NamedObjectModel model : slave.getConfiguration()) {
					builder.addConfiguration(model);
				}
				for (NamedObjectModel model : slave.getEnvironmentEntries()) {
					builder.addEnvironmentEntry(model);
				}
				for (NamedObjectModel model : slave.getGlobals()) {
					builder.addGlobal(model);
				}
				for (NamedObjectModel model : slave.getWorkItemHandlers()) {
					builder.addWorkItemHandler(model);
				}
				for (String requiredRole : slave.getRequiredRoles()) {
					builder.addRequiredRole(requiredRole);
				}
				for (String clazz : slave.getClasses()) {
					builder.addClass(clazz);
				}
				Boolean slaveLimit =  slave.getLimitSerializationClasses();
				Boolean masterLimit =  master.getLimitSerializationClasses();
				if( slaveLimit != null && masterLimit != null &&
				        (!slaveLimit || !masterLimit) ) {
				    builder.setLimitSerializationClasses(false);
				}

				merged = builder.get();
				break;

			default:
				break;
		}


		return merged;
	}

	private class MergeModeBuildHandler implements BuilderHandler {

		private MergeMode mode;

		MergeModeBuildHandler(MergeMode mode) {
			this.mode = mode;
		}

		@Override
		public boolean accepted(Object value) {
			boolean accepted = false;
			switch (mode) {
				case OVERRIDE_ALL:
					accepted = true;
					break;
				case OVERRIDE_EMPTY:
					if (!isEmpty(value)) {
						accepted = true;
					}
					break;

				case MERGE_COLLECTIONS:
					if (!isEmpty(value)) {
						accepted = true;
					}
					break;

				default:
					break;
			}
			return accepted;
		}

		protected boolean isEmpty(Object value) {
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
