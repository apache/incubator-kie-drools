/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal.runtime.conf;

import java.util.List;

/**
 * Fluent API style builder to simplify construction (or modification)
 * of descriptor instances.
 *
 */
public interface DeploymentDescriptorBuilder {

	DeploymentDescriptor get();

	DeploymentDescriptorBuilder persistenceUnit(String pu);

	DeploymentDescriptorBuilder auditPersistenceUnit(String pu);

	DeploymentDescriptorBuilder auditMode(AuditMode mode);

	DeploymentDescriptorBuilder persistenceMode(PersistenceMode mode);

	DeploymentDescriptorBuilder addConfiguration(NamedObjectModel model);

	DeploymentDescriptorBuilder addEnvironmentEntry(NamedObjectModel model);

	DeploymentDescriptorBuilder addWorkItemHandler(NamedObjectModel model);

	DeploymentDescriptorBuilder addGlobal(NamedObjectModel model);

	DeploymentDescriptorBuilder addEventListener(ObjectModel model);

	DeploymentDescriptorBuilder addTaskEventListener(ObjectModel model);

	DeploymentDescriptorBuilder addMarshalingStrategy(ObjectModel model);

	DeploymentDescriptorBuilder addRequiredRole(String role);

	DeploymentDescriptorBuilder addClass(String clazz);

	DeploymentDescriptorBuilder runtimeStrategy(RuntimeStrategy strategy);

	DeploymentDescriptorBuilder setConfiguration(List<NamedObjectModel> models);

	DeploymentDescriptorBuilder setEnvironmentEntries(List<NamedObjectModel> models);

	DeploymentDescriptorBuilder setWorkItemHandlers(List<NamedObjectModel> models);

	DeploymentDescriptorBuilder setGlobals(List<NamedObjectModel> models);

	DeploymentDescriptorBuilder setEventListeners(List<ObjectModel> models);

	DeploymentDescriptorBuilder setTaskEventListeners(List<ObjectModel> models);

	DeploymentDescriptorBuilder setMarshalingStrategies(List<ObjectModel> models);

	DeploymentDescriptorBuilder setRequiredRoles(List<String> roles);

	DeploymentDescriptorBuilder setClasses(List<String> classes);

	DeploymentDescriptorBuilder setLimitSerializationClasses(Boolean limit);

	void setBuildHandler(BuilderHandler handler);
}