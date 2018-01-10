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
package org.jbpm.kie.services.impl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.model.ProcessDefinition;

/**
 *
 */
public class ProcessAssetDesc implements ProcessDefinition {

    private static final long serialVersionUID = -9059086115873165296L;

    private String id;
    private String name;
    private String version;
    private String packageName;
    private String type;
    private String knowledgeType;
    private String namespace;
    private String originalPath;
    private String deploymentId;
    private String encodedProcessSource;
    private Map<String, String> forms = new HashMap<String, String>();
    private List<String> roles = new ArrayList<String>();
    private Collection<String> signals = Collections.emptyList();
    private Collection<String> globals = Collections.emptyList();
    private Collection<String> rules = Collections.emptyList();

    private Map<String, Collection<String>> associatedEntities = new HashMap<String, Collection<String>>();
    private Map<String, String> serviceTasks = new HashMap<String, String>();
    private Map<String, String> processVariables = new HashMap<String, String>();
    private Collection<String> reusableSubProcesses = new ArrayList<String>();
    private boolean dynamic = true;
    
    private boolean active = true;

	public ProcessAssetDesc() {
    }

	public ProcessAssetDesc(String id, String name, String version, String packageName, String type, String knowledgeType, String namespace, String deploymentId) {
	    this(id, name, version, packageName, type, knowledgeType, namespace, deploymentId, false);
	}

    public ProcessAssetDesc(String id, String name, String version, String packageName, String type, String knowledgeType, String namespace, String deploymentId, boolean dynamic) {
        this.id = safeValue(id);
        this.name = safeValue(name);
        this.version = safeValue(version);
        this.packageName = safeValue(packageName);
        this.type = safeValue(type);
        this.knowledgeType = safeValue(knowledgeType);
        this.namespace = safeValue(namespace);
        this.deploymentId = safeValue(deploymentId);
        this.dynamic = dynamic;
    }

    private String safeValue(String value) {
    	if (value == null) {
    		return "";
    	}

    	return value;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getKnowledgeType() {
        return knowledgeType;
    }


    @Override
    public String getOriginalPath() {
        return originalPath;
    }


    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }


    @Override
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDeploymentId() {
        return deploymentId;
    }


    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }


    @Override
    public Map<String, Collection<String>> getAssociatedEntities() {
    	return associatedEntities;
    }


    public void setAssociatedEntities(
    		Map<String, Collection<String>> associatedEntities) {
    	this.associatedEntities = associatedEntities;
    }


    @Override
    public Map<String, String> getServiceTasks() {
    	return serviceTasks;
    }


    public void setServiceTasks(Map<String, String> serviceTasks) {
    	this.serviceTasks = serviceTasks;
    }


    @Override
    public Map<String, String> getProcessVariables() {
    	return processVariables;
    }


    public void setProcessVariables(Map<String, String> processVariables) {
    	this.processVariables = processVariables;
    }


    @Override
    public Collection<String> getReusableSubProcesses() {
    	return reusableSubProcesses;
    }


    public void setReusableSubProcesses(Collection<String> reusableSubProcesses) {
    	this.reusableSubProcesses = reusableSubProcesses;
    }


    @Override
    public Collection<String> getSignals() {
        return signals;
    }

    public void setSignals(Collection<String> signals) {
        this.signals = signals;
    }

    @Override
    public Collection<String> getGlobals() {
        return globals;
    }

    public void setGlobals(Collection<String> globals) {
        this.globals = globals;
    }

    @Override
    public Collection<String> getReferencedRules() {
        return rules;
    }

    public void setReferencedRules(Collection<String> rules) {
        this.rules = rules;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEncodedProcessSource() {
        return encodedProcessSource;
    }


    public void setEncodedProcessSource(String processString) {
        this.encodedProcessSource = processString;
    }


    public Map<String, String> getForms() {
        return forms;
    }


    public void setForms(Map<String, String> forms) {
        this.forms = forms;
    }

    public void addForm(String id, String formContent) {
        this.forms.put(id, formContent);
    }

	public List<String> getRoles() {
		return roles;
	}


	public void setRoles(List<String> roles) {
		this.roles = roles;
	}


	public boolean isActive() {
		return active;
	}


	public void setActive(boolean active) {
		this.active = active;
	}

    @Override
    public boolean isDynamic() {
        return dynamic;
    }


    @Override
    public String toString() {
        return "ProcessDesc{id=" + id + ", name=" + name + ", version=" + version + ", packageName=" + packageName
        		+ ", type=" + type + ", knowledgeType=" + knowledgeType + ", namespace=" + namespace + "active=" + active + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + ((associatedEntities == null) ? 0 : associatedEntities.hashCode());
        result = prime * result + ((deploymentId == null) ? 0 : deploymentId.hashCode());
        result = prime * result + ((encodedProcessSource == null) ? 0 : encodedProcessSource.hashCode());
        result = prime * result + ((forms == null) ? 0 : forms.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((knowledgeType == null) ? 0 : knowledgeType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        result = prime * result + ((processVariables == null) ? 0 : processVariables.hashCode());
        result = prime * result + ((reusableSubProcesses == null) ? 0 : reusableSubProcesses.hashCode());
        result = prime * result + ((roles == null) ? 0 : roles.hashCode());
        result = prime * result + ((serviceTasks == null) ? 0 : serviceTasks.hashCode());
        result = prime * result + ((signals == null) ? 0 : signals.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + (dynamic ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessAssetDesc other = (ProcessAssetDesc) obj;
        if (active != other.active)
            return false;
        if (associatedEntities == null) {
            if (other.associatedEntities != null)
                return false;
        } else if (!associatedEntities.equals(other.associatedEntities))
            return false;
        if (deploymentId == null) {
            if (other.deploymentId != null)
                return false;
        } else if (!deploymentId.equals(other.deploymentId))
            return false;
        if (encodedProcessSource == null) {
            if (other.encodedProcessSource != null)
                return false;
        } else if (!encodedProcessSource.equals(other.encodedProcessSource))
            return false;
        if (forms == null) {
            if (other.forms != null)
                return false;
        } else if (!forms.equals(other.forms))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (knowledgeType == null) {
            if (other.knowledgeType != null)
                return false;
        } else if (!knowledgeType.equals(other.knowledgeType))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (originalPath == null) {
            if (other.originalPath != null)
                return false;
        } else if (!originalPath.equals(other.originalPath))
            return false;
        if (packageName == null) {
            if (other.packageName != null)
                return false;
        } else if (!packageName.equals(other.packageName))
            return false;
        if (processVariables == null) {
            if (other.processVariables != null)
                return false;
        } else if (!processVariables.equals(other.processVariables))
            return false;
        if (reusableSubProcesses == null) {
            if (other.reusableSubProcesses != null)
                return false;
        } else if (!reusableSubProcesses.equals(other.reusableSubProcesses))
            return false;
        if (roles == null) {
            if (other.roles != null)
                return false;
        } else if (!roles.equals(other.roles))
            return false;
        if (serviceTasks == null) {
            if (other.serviceTasks != null)
                return false;
        } else if (!serviceTasks.equals(other.serviceTasks))
            return false;
        if (signals == null) {
            if (other.signals != null)
                return false;
        } else if (!signals.equals(other.signals))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (dynamic != other.dynamic) {
            return false;
        }
        return true;
    }

    public ProcessAssetDesc copy() {
        ProcessAssetDesc copied = new ProcessAssetDesc(id, name, version, packageName, type, knowledgeType, namespace, deploymentId, dynamic);
  
        copied.originalPath = this.originalPath;
        
        copied.encodedProcessSource = this.encodedProcessSource;
        copied.forms = new HashMap<String, String>(this.forms);
        copied.roles = new ArrayList<String>(this.roles);
        copied.signals = new ArrayList<String>(this.signals);
        copied.globals = new ArrayList<String>(this.globals);
        copied.rules = new ArrayList<String>(this.rules);

        copied.associatedEntities = new HashMap<String, Collection<String>>(this.associatedEntities);
        copied.serviceTasks = new HashMap<String, String>(this.serviceTasks);
        copied.processVariables = new HashMap<String, String>(this.processVariables);
        copied.reusableSubProcesses = new ArrayList<String>(this.reusableSubProcesses);
        copied.active = this.active;
        
        return copied;
        
    }

}