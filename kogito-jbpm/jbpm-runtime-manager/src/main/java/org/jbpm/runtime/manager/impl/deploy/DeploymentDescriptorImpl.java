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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;

import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.BuilderHandler;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
/*
 * NOTE: Whenever adding new fields that represent actual item of deployment descriptor always update:
 * - isEmpty method
 * - clearClone method
 * - Builder
 */
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
	private Set<ObjectModel> marshallingStrategies = new LinkedHashSet<ObjectModel>();

	@XmlElement(name="event-listener")
	@XmlElementWrapper(name="event-listeners")
	private Set<ObjectModel> eventListeners = new LinkedHashSet<ObjectModel>();

	@XmlElement(name="task-event-listener")
	@XmlElementWrapper(name="task-event-listeners")
	private Set<ObjectModel> taskEventListeners = new LinkedHashSet<ObjectModel>();

	@XmlElement(name="global")
	@XmlElementWrapper(name="globals")
	private Set<NamedObjectModel> globals = new LinkedHashSet<NamedObjectModel>();

	@XmlElement(name="work-item-handler")
	@XmlElementWrapper(name="work-item-handlers")
	private Set<NamedObjectModel> workItemHandlers = new LinkedHashSet<NamedObjectModel>();

	@XmlElement(name="environment-entry")
	@XmlElementWrapper(name="environment-entries")
	private Set<NamedObjectModel> environmentEntries = new LinkedHashSet<NamedObjectModel>();

	@XmlElement(name="configuration")
	@XmlElementWrapper(name="configurations")
	private Set<NamedObjectModel> configuration = new LinkedHashSet<NamedObjectModel>();

	@XmlElement(name="required-role")
	@XmlElementWrapper(name="required-roles")
	private Set<String> requiredRoles = new LinkedHashSet<String>();

	@XmlElement(name="remoteable-class")
	@XmlElementWrapper(name="remoteable-classes")
	private List<String> classes = new ArrayList<String>();

	@XmlElement(name="limit-serialization-classes")
	private Boolean limitSerializationClasses = true;

	@XmlTransient
	private Map<String, Set<String>> mappedRoles;

	protected void mapRequiredRoles() {
		if (mappedRoles != null) {
			return;
		}
		mappedRoles = new HashMap<String, Set<String>>();

		Set<String> typeAll = new LinkedHashSet<String>();
		Set<String> typeView = new LinkedHashSet<String>();
		Set<String> typeExecute = new LinkedHashSet<String>();

		mappedRoles.put(TYPE_ALL, typeAll);
		mappedRoles.put(TYPE_VIEW, typeView);
		mappedRoles.put(TYPE_EXECUTE, typeExecute);

		if (requiredRoles != null && !requiredRoles.isEmpty()) {

			for (String roleString : requiredRoles) {
				String rolePrefix = null;
				String role = roleString;

				if (roleString.indexOf(":") != -1) {
					String[] roleInfo = roleString.split(":");

					rolePrefix = roleInfo[0];
					role = roleInfo[1];

					mappedRoles.get(rolePrefix).add(role);
					typeAll.add(role);
				} else {
					typeAll.add(role);
					typeView.add(role);
					typeExecute.add(role);
				}
			}

		}

	}


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
		return new ArrayList<ObjectModel>(cleanSet(marshallingStrategies));
	}

	@Override
	public List<ObjectModel> getEventListeners() {
		return new ArrayList<ObjectModel>(cleanSet(eventListeners));
	}

	@Override
	public List<NamedObjectModel> getGlobals() {
		return new ArrayList<NamedObjectModel>(cleanNamedSet(globals));
	}

	@Override
	public List<NamedObjectModel> getWorkItemHandlers() {
		return new ArrayList<NamedObjectModel>(cleanNamedSet(workItemHandlers));
	}

	@Override
	public List<ObjectModel> getTaskEventListeners() {
		return new ArrayList<ObjectModel>(cleanSet(taskEventListeners));
	}

	@Override
	public List<NamedObjectModel> getEnvironmentEntries() {
		return new ArrayList<NamedObjectModel>(cleanNamedSet(environmentEntries));
	}

	@Override
	public List<NamedObjectModel> getConfiguration() {
		return new ArrayList<NamedObjectModel>(cleanNamedSet(configuration));
	}

	@Override
	public List<String> getRequiredRoles() {
		return new ArrayList<String>(requiredRoles);
	}
	@Override
	public List<String> getRequiredRoles(String type) {
		mapRequiredRoles();
		Set<String> roles = mappedRoles.get(type);
		if (roles == null) {
			return new ArrayList<String>();
		} else {
			return new ArrayList<String>(roles);
		}
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

    public Boolean getLimitSerializationClasses() {
        return limitSerializationClasses;
    }

    public void setLimitSerializationClasses( Boolean limitSerializationClasses ) {
        this.limitSerializationClasses = limitSerializationClasses;
    }

    protected Set<NamedObjectModel> cleanNamedSet(Set<NamedObjectModel> input) {
        input.remove(null);

        return input;
	}

    protected Set<ObjectModel> cleanSet(Set<ObjectModel> input) {
        input.remove(null);

        return input;
    }

    protected void removeTransient(Set<?> input) {
        Iterator<?> it = input.iterator();

        while (it.hasNext()) {
            Object object = (Object) it.next();
            if (object instanceof TransientNamedObjectModel
                    || object instanceof TransientObjectModel) {
                it.remove();
            }
        }
    }

    public DeploymentDescriptor clearClone() throws CloneNotSupportedException {
	    DeploymentDescriptorImpl clone = new DeploymentDescriptorImpl();

	     clone.getBuilder()
	    .setClasses(getClasses())
	    .setConfiguration(getConfiguration())
	    .setEnvironmentEntries(getEnvironmentEntries())
	    .setEventListeners(getEventListeners())
	    .setGlobals(getGlobals())
	    .setMarshalingStrategies(getMarshallingStrategies())
	    .setRequiredRoles(getRequiredRoles())
	    .setTaskEventListeners(getTaskEventListeners())
	    .setWorkItemHandlers(getWorkItemHandlers())
	    .auditMode(getAuditMode())
	    .auditPersistenceUnit(getAuditPersistenceUnit())
	    .persistenceMode(getPersistenceMode())
	    .persistenceUnit(getPersistenceUnit())
	    .runtimeStrategy(getRuntimeStrategy())
	    .setLimitSerializationClasses(getLimitSerializationClasses());

	     removeTransient(clone.configuration);
	     removeTransient(clone.environmentEntries);
	     removeTransient(clone.eventListeners);
	     removeTransient(clone.globals);
	     removeTransient(clone.marshallingStrategies);
	     removeTransient(clone.taskEventListeners);
	     removeTransient(clone.workItemHandlers);

	     return clone;
    }
    
    public boolean isEmpty() {
        boolean empty = true;
        
        if (persistenceUnit != null) {
            return false;
        }
        if (auditPersistenceUnit != null) {
            return false;
        }
        if (auditMode != AuditMode.JPA) {
            return false;
        }
        if (persistenceMode != PersistenceMode.JPA) {
            return false;
        }
        if (runtimeStrategy != RuntimeStrategy.SINGLETON) {
            return false;
        }
        if ( marshallingStrategies != null && !marshallingStrategies.isEmpty()) {
            return false;
        }
        if (eventListeners != null && !eventListeners.isEmpty()) {
            return false;
        }
        if (taskEventListeners != null && !taskEventListeners.isEmpty()) {
            return false;
        }
        if (globals != null && !globals.isEmpty()) {
            return false;
        }
        if (workItemHandlers != null && !workItemHandlers.isEmpty()) {
            return false;
        }
        if (environmentEntries != null && !environmentEntries.isEmpty()) {
            return false;
        }
        if (configuration != null && !configuration.isEmpty()) {
            return false;
        }
        if (requiredRoles != null && !requiredRoles.isEmpty()) {
            return false;
        }
        if (classes != null && !classes.isEmpty()) {
            return false;
        }
        if (!limitSerializationClasses) {
            return false;
        }

        return empty;
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
					if (!descriptor.workItemHandlers.add(model)) {
						descriptor.workItemHandlers.remove(model);
						descriptor.workItemHandlers.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addTaskEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.taskEventListeners.add(model)) {
						descriptor.taskEventListeners.remove(model);
						descriptor.taskEventListeners.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addMarshalingStrategy(ObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.marshallingStrategies.add(model)) {
						descriptor.marshallingStrategies.remove(model);
						descriptor.marshallingStrategies.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addGlobal(NamedObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.globals.add(model)) {
						descriptor.globals.remove(model);
						descriptor.globals.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addEventListener(ObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.eventListeners.add(model)) {
						descriptor.eventListeners.remove(model);
						descriptor.eventListeners.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addEnvironmentEntry(NamedObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.environmentEntries.add(model)) {
						descriptor.environmentEntries.remove(model);
						descriptor.environmentEntries.add(model);
					}
				}
				return this;
			}

			@Override
			public DeploymentDescriptorBuilder addConfiguration(NamedObjectModel model) {
				if (handler.accepted(model)) {
					if (!descriptor.configuration.add(model)) {
						descriptor.configuration.remove(model);
						descriptor.configuration.add(model);
					}
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
            public DeploymentDescriptorBuilder setLimitSerializationClasses(Boolean limit) {
				if (handler.accepted(limit)) {
				    descriptor.setLimitSerializationClasses(limit);
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
