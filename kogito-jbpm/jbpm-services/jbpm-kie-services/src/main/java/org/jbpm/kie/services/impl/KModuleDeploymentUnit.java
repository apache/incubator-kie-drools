package org.jbpm.kie.services.impl;

import java.io.Serializable;

import org.drools.core.util.StringUtils;
import org.kie.internal.deployment.DeploymentUnit;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;

public class KModuleDeploymentUnit implements DeploymentUnit, Serializable {

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
        this.strategy = strategy;
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

}
