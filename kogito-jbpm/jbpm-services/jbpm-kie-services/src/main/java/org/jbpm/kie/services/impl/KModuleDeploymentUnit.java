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

package org.jbpm.kie.services.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.api.AttributesAware;
import org.jbpm.services.api.model.DeploymentUnit;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

public class KModuleDeploymentUnit implements DeploymentUnit, AttributesAware, Serializable {

	private static final long serialVersionUID = 1L;
	
	private String artifactId;
    private String groupId;
    private String version;
    private String kbaseName;
    private String ksessionName;

    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
    private MergeMode mergeMode = MergeMode.MERGE_COLLECTIONS;
    
    private DeploymentDescriptor deploymentDescriptor;
    private boolean deployed = false;
    private boolean strategyUnset = true;
    
    private boolean active = true;
    
    private transient KieContainer kieContainer;
    
    private Map<String, String> attributes = new HashMap<String, String>();
    
    public KModuleDeploymentUnit(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
        this(groupId, artifactId, version);
        this.kbaseName = kbaseName;
        this.ksessionName = ksessionName;
    }

    public KModuleDeploymentUnit(String groupId, String artifactId, String version, String kbaseName, String ksessionName,
            String strategy) {
        this(groupId, artifactId, version, kbaseName, ksessionName);
        this.strategy = RuntimeStrategy.valueOf(strategy);
        this.strategyUnset = false;
    }

    @Override
    public String getIdentifier() {
        String id = getGroupId() + ":" + getArtifactId() + ":" + getVersion();
        boolean kbaseFilled = !StringUtils.isEmpty(kbaseName);
        boolean ksessionFilled = !StringUtils.isEmpty(ksessionName);
        if( kbaseFilled || ksessionFilled) {
            id = id.concat(":");
            if( kbaseFilled ) {
                id = id.concat(kbaseName);
            }
            if( ksessionFilled ) {
                id = id.concat(":" + ksessionName);
            }
        }
        return id;
    }

    @Override
    public RuntimeStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(RuntimeStrategy strategy) {
    	if (strategyUnset) {
    		this.strategy = strategy;
    		this.strategyUnset = false;
    	}
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKsessionName() {
        return ksessionName;
    }

    public void setKsessionName(String ksessionName) {
        this.ksessionName = ksessionName;
    }

    public String getKbaseName() {
        return kbaseName;
    }

    public void setKbaseName(String kbaseName) {
        this.kbaseName = kbaseName;
    }

    @Override
    public String toString() {
        return getIdentifier() + " [strategy=" + strategy + "]";
    }

	public MergeMode getMergeMode() {
		if (mergeMode == null) {
			mergeMode = MergeMode.MERGE_COLLECTIONS;
		}
		return mergeMode;
	}

	public void setMergeMode(MergeMode mergeMode) {
		this.mergeMode = mergeMode;
	}

	public DeploymentDescriptor getDeploymentDescriptor() {
		return deploymentDescriptor;
	}

	public void setDeploymentDescriptor(DeploymentDescriptor deploymentDescriptor) {
		this.deploymentDescriptor = deploymentDescriptor;
	}

	public boolean isDeployed() {
		return deployed;
	}

	public void setDeployed(boolean deployed) {
		this.deployed = deployed;
	}
	
	public void resetStrategy() {
		this.strategyUnset = true;
	}

	@Override
	public void addAttribute(String name, String value) {
		this.attributes.put(name, value);
	}

	@Override
	public String removeAttribute(String name) {
		return this.attributes.remove(name);
	}

	@Override
	public Map<String, String> getAttributes() {
	    if (this.attributes == null){
	        return Collections.EMPTY_MAP;
	    }
		return Collections.unmodifiableMap(this.attributes);
	}

	public KieContainer getKieContainer() {
		return kieContainer;
	}

	public void setKieContainer(KieContainer kieContainer) {
		this.kieContainer = kieContainer;
	}

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
