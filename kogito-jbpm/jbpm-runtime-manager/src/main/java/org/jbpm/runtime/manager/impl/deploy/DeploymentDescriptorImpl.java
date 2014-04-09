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

package org.jbpm.runtime.manager.impl.deploy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.deployment.DeploymentUnit.RuntimeStrategy;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.BuilderHandler;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;

@XmlRootElement(name="deployment-descriptor")
@XmlAccessorType(XmlAccessType.NONE)
public class DeploymentDescriptorImpl implements DeploymentDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name="persistence-unit")
	@XmlSchemaType(name="string")
	private String persistenceUnit;
	
	@XmlElement(name="audit-persistence-unit")
	@XmlSchemaType(name="string")
	private String auditPersistenceUnit;
	
	@XmlElement(name="audit-mode")
	private AuditMode auditMode = AuditMode.JPA;
	
	@XmlElement(name="persistence-mode")
	private PersistenceMode persistenceMode = PersistenceMode.JPA;
	
	@XmlElement(name="runtime-strategy")
	private RuntimeStrategy runtimeStrategy = RuntimeStrategy.SINGLETON;
	
	@XmlElement(name="marshalling-strategy")
	@XmlElementWrapper(name="marshalling-strategies")
	private List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
	
	@XmlElement(name="event-listener")
	@XmlElementWrapper(name="event-listeners")
	private List<ObjectModel> eventListeners = new ArrayList<ObjectModel>();
	
	@XmlElement(name="task-event-listener")
	@XmlElementWrapper(name="task-event-listeners")
	private List<ObjectModel> taskEventListeners = new ArrayList<ObjectModel>();
	
	@XmlElement(name="global")
	@XmlElementWrapper(name="globals")
	private List<NamedObjectModel> globals = new ArrayList<NamedObjectModel>();
	
	@XmlElement(name="work-item-handler")
	@XmlElementWrapper(name="work-item-handlers")
	private List<NamedObjectModel> workItemHandlers = new ArrayList<NamedObjectModel>();
	
	@XmlElement(name="environment-entry")
	@XmlElementWrapper(name="environment-entries")
	private List<NamedObjectModel> environmentEntries = new ArrayList<NamedObjectModel>();
	
	@XmlElement(name="configuration")
	@XmlElementWrapper(name="configurations")
	private List<NamedObjectModel> configuration = new ArrayList<NamedObjectModel>();
	
	@XmlElement(name="required-role")
	@XmlElementWrapper(name="required-roles")
	private List<String> requiredRoles = new ArrayList<String>();
	
	public DeploymentDescriptorImpl() {
		// fox jaxb only
	}
	
	public DeploymentDescriptorImpl(String defaultPU) {
		this.persistenceUnit = defaultPU;
		this.auditPersistenceUnit = defaultPU;
	}

	@Override
	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	@Override
	public String getAuditPersistenceUnit() {
		return auditPersistenceUnit;
	}

	@Override
	public AuditMode getAuditMode() {		
		return auditMode;
	}

	@Override
	public PersistenceMode getPersistenceMode() {		
		return persistenceMode;
	}

	@Override
	public RuntimeStrategy getRuntimeStrategy() {		
		return runtimeStrategy;
	}

	@Override
	public List<ObjectModel> getMarshallingStrategies() {		
		return marshallingStrategies;
	}

	@Override
	public List<ObjectModel> getEventListeners() {		
		return eventListeners;
	}

	@Override
	public List<NamedObjectModel> getGlobals() {		
		return globals;
	}

	@Override
	public List<NamedObjectModel> getWorkItemHandlers() {		
		return workItemHandlers;
	}

	@Override
	public List<ObjectModel> getTaskEventListeners() {		
		return taskEventListeners;
	}

	@Override
	public List<NamedObjectModel> getEnvironmentEntries() {		
		return environmentEntries;
	}

	@Override
	public List<NamedObjectModel> getConfiguration() {		
		return configuration;
	}
	
	@Override
	public List<String> getRequiredRoles() {
		return requiredRoles;
	}


	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public void setAuditPersistenceUnit(String auditPersistenceUnit) {
		this.auditPersistenceUnit = auditPersistenceUnit;
	}

	public void setAuditMode(AuditMode auditMode) {
		this.auditMode = auditMode;
	}

	public void setPersistenceMode(PersistenceMode persistenceMode) {
		this.persistenceMode = persistenceMode;
	}
	
	public void setRuntimeStrategy(RuntimeStrategy runtimeStrategy) {
		this.runtimeStrategy = runtimeStrategy;
	}

	public void setMarshallingStrategies(List<ObjectModel> marshallingStrategies) {
		this.marshallingStrategies = marshallingStrategies;
	}

	public void setEventListeners(List<ObjectModel> eventListeners) {
		this.eventListeners = eventListeners;
	}

	public void setTaskEventListeners(List<ObjectModel> taskEventListeners) {
		this.taskEventListeners = taskEventListeners;
	}

	public void setGlobals(List<NamedObjectModel> globals) {
		this.globals = globals;
	}

	public void setWorkItemHandlers(List<NamedObjectModel> workItemHandlers) {
		this.workItemHandlers = workItemHandlers;
	}

	public void setEnvironmentEntries(List<NamedObjectModel> environmentEntires) {
		this.environmentEntries = environmentEntires;
	}

	public void setConfiguration(List<NamedObjectModel> configuration) {
		this.configuration = configuration;
	}
	
	public void setRequiredRoles(List<String> requiredRoles) {
		this.requiredRoles = requiredRoles;
	}


	@Override
	public DeploymentDescriptorBuilder getBuilder() {
		
		return new DeploymentDescriptorBuilder() {
			
			private BuilderHandler handler = new BuilderHandler() {
				
				@Override
				public boolean accepted(Object value) {
					return true;
				}
			};
			
			private DeploymentDescriptorImpl descriptor = DeploymentDescriptorImpl.this;
			
			@Override
			public DeploymentDescriptorBuilder runtimeStrategy(RuntimeStrategy strategy) {
				if (handler.accepted(strategy)) {
					descriptor.setRuntimeStrategy(strategy);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder persistenceUnit(String pu) {
				if (handler.accepted(pu)) {
					descriptor.setPersistenceUnit(pu);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder persistenceMode(PersistenceMode mode) {
				if (handler.accepted(mode)) {
					descriptor.setPersistenceMode(mode);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder auditPersistenceUnit(String pu) {
				if (handler.accepted(pu)) {
					descriptor.setAuditPersistenceUnit(pu);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder auditMode(AuditMode mode) {
				if (handler.accepted(mode)) {
					descriptor.setAuditMode(mode);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addWorkItemHandler(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getWorkItemHandlers().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addTaskEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getTaskEventListeners().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addMarshalingStrategy(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getMarshallingStrategies().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addGlobal(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getGlobals().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getEventListeners().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addEnvironmentEntry(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getEnvironmentEntries().add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addConfiguration(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.getConfiguration().add(model);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setConfiguration(List<NamedObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setConfiguration(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setEnvironmentEntries(List<NamedObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setEnvironmentEntries(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setWorkItemHandlers(List<NamedObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setWorkItemHandlers(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setGlobals(List<NamedObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setGlobals(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setEventListeners(List<ObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setEventListeners(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setTaskEventListeners(List<ObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setTaskEventListeners(models);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setMarshalingStrategies(List<ObjectModel> models) {
				if (handler.accepted(models)) {
					descriptor.setMarshallingStrategies(models);
				}
				return this;
			}

			@Override
			public void setBuildHandler(BuilderHandler handler) {
				this.handler = handler;
			}
			
			@Override
			public DeploymentDescriptor get() {

				return descriptor;
			}

			@Override
			public DeploymentDescriptorBuilder addRequiredRole(String role) {
				if (handler.accepted(role)) {
					descriptor.getRequiredRoles().add(role);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder setRequiredRoles(List<String> roles) {
				if (handler.accepted(roles)) {
					descriptor.setRequiredRoles(roles);
				}
				return this;
			}

		};
	}

	@Override
	public String toXml() {
		return DeploymentDescriptorIO.toXml(this);
	}

}
