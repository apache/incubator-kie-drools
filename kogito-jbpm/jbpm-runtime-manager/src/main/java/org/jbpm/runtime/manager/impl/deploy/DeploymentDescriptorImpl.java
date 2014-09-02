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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.BuilderHandler;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

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
	private Set<ObjectModel> marshallingStrategies = new HashSet<ObjectModel>();
	
	@XmlElement(name="event-listener")
	@XmlElementWrapper(name="event-listeners")
	private Set<ObjectModel> eventListeners = new HashSet<ObjectModel>();
	
	@XmlElement(name="task-event-listener")
	@XmlElementWrapper(name="task-event-listeners")
	private Set<ObjectModel> taskEventListeners = new HashSet<ObjectModel>();
	
	@XmlElement(name="global")
	@XmlElementWrapper(name="globals")
	private Set<NamedObjectModel> globals = new HashSet<NamedObjectModel>();
	
	@XmlElement(name="work-item-handler")
	@XmlElementWrapper(name="work-item-handlers")
	private Set<NamedObjectModel> workItemHandlers = new HashSet<NamedObjectModel>();
	
	@XmlElement(name="environment-entry")
	@XmlElementWrapper(name="environment-entries")
	private Set<NamedObjectModel> environmentEntries = new HashSet<NamedObjectModel>();
	
	@XmlElement(name="configuration")
	@XmlElementWrapper(name="configurations")
	private Set<NamedObjectModel> configuration = new HashSet<NamedObjectModel>();
	
	@XmlElement(name="required-role")
	@XmlElementWrapper(name="required-roles")
	private Set<String> requiredRoles = new HashSet<String>();
	
	@XmlElement(name="remoteable-class")
	@XmlElementWrapper(name="remoteable-classes")
	private List<String> classes = new ArrayList<String>();
	
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
		return new ArrayList<ObjectModel>(marshallingStrategies);
	}

	@Override
	public List<ObjectModel> getEventListeners() {		
		return new ArrayList<ObjectModel>(eventListeners);
	}

	@Override
	public List<NamedObjectModel> getGlobals() {		
		return new ArrayList<NamedObjectModel>(globals);
	}

	@Override
	public List<NamedObjectModel> getWorkItemHandlers() {		
		return new ArrayList<NamedObjectModel>(workItemHandlers);
	}

	@Override
	public List<ObjectModel> getTaskEventListeners() {		
		return new ArrayList<ObjectModel>(taskEventListeners);
	}

	@Override
	public List<NamedObjectModel> getEnvironmentEntries() {		
		return new ArrayList<NamedObjectModel>(environmentEntries);
	}

	@Override
	public List<NamedObjectModel> getConfiguration() {		
		return new ArrayList<NamedObjectModel>(configuration);
	}
	
	@Override
	public List<String> getRequiredRoles() {
		return new ArrayList<String>(requiredRoles);
	}
	
	@Override
	public List<String> getClasses() {
		if (classes == null) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(classes);
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
		if (marshallingStrategies != null) {
			this.marshallingStrategies = new HashSet<ObjectModel>(marshallingStrategies);
		}
	}

	public void setEventListeners(List<ObjectModel> eventListeners) {
		if (eventListeners != null) {
			this.eventListeners = new HashSet<ObjectModel>(eventListeners);
		}
	}

	public void setTaskEventListeners(List<ObjectModel> taskEventListeners) {
		if (taskEventListeners != null) {
			this.taskEventListeners = new HashSet<ObjectModel>(taskEventListeners);
		}
	}

	public void setGlobals(List<NamedObjectModel> globals) {
		if (globals != null) {
			this.globals = new HashSet<NamedObjectModel>(globals);
		}
	}

	public void setWorkItemHandlers(List<NamedObjectModel> workItemHandlers) {
		if (workItemHandlers != null) {
			this.workItemHandlers = new HashSet<NamedObjectModel>(workItemHandlers);
		}
	}

	public void setEnvironmentEntries(List<NamedObjectModel> environmentEntires) {
		if (environmentEntires != null) {
			this.environmentEntries = new HashSet<NamedObjectModel>(environmentEntires);
		}
	}

	public void setConfiguration(List<NamedObjectModel> configuration) {
		if (configuration != null) {
			this.configuration = new HashSet<NamedObjectModel>(configuration);
		}
	}
	
	public void setRequiredRoles(List<String> requiredRoles) {
		if (requiredRoles != null) {
			this.requiredRoles = new HashSet<String>(requiredRoles);
		}
	}
	
	public void setClasses(List<String> classes) {
		if (classes != null) {
			this.classes = new ArrayList<String>(classes);
		}
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
					descriptor.workItemHandlers.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addTaskEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.taskEventListeners.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addMarshalingStrategy(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.marshallingStrategies.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addGlobal(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.globals.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.eventListeners.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addEnvironmentEntry(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.environmentEntries.add(model);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addConfiguration(NamedObjectModel model) {
				if (handler.accepted(model)) {
					descriptor.configuration.add(model);
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addRequiredRole(String role) {
				if (handler.accepted(role)) {
					descriptor.requiredRoles.add(role);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder addClass(String clazz) {
				if (handler.accepted(clazz)) {
					descriptor.classes.add(clazz);
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
			public DeploymentDescriptorBuilder setRequiredRoles(List<String> roles) {
				if (handler.accepted(roles)) {
					descriptor.setRequiredRoles(roles);
				}
				return this;
			}
			
			@Override
			public DeploymentDescriptorBuilder setClasses(List<String> classes) {
				if (handler.accepted(classes)) {
					descriptor.setClasses(classes);
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

		};
	}

	@Override
	public String toXml() {
		return DeploymentDescriptorIO.toXml(this);
	}


}
