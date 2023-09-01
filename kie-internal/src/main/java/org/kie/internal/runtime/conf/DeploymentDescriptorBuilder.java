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