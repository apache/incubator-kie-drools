package org.jbpm.services.ejb.timer;

import org.jbpm.process.core.timer.impl.GlobalTimerService.GlobalJobHandle;

public class EjbGlobalJobHandle extends GlobalJobHandle {
	
	private static final long serialVersionUID = 4254413497038652954L;
	private String uuid;
	private String deploymentId;
	
	public EjbGlobalJobHandle(long id, String uuid, String deploymentId) {
		super(id);
		this.uuid = uuid;
		this.deploymentId = deploymentId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return "EjbGlobalJobHandle [uuid=" + uuid + "]";
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

}
