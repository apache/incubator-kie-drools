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

package org.jbpm.kie.services.impl.store;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/*
 * Named queries defined in services orm file
 */
@Entity
@Table(name="DeploymentStore", uniqueConstraints={@UniqueConstraint(columnNames="DEPLOYMENT_ID")})
@SequenceGenerator(name="deploymentStoreIdSeq", sequenceName="DEPLOY_STORE_ID_SEQ", allocationSize=1)
public class DeploymentStoreEntry implements Serializable {

	private static final long serialVersionUID = 6669858787722894023L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="deploymentStoreIdSeq")
    @Column(name = "id")
	private Long id;
	
	@Column(name="DEPLOYMENT_ID")
	private String deploymentId;
	
	@Lob
	@Column(length=65535)
	private String deploymentUnit;
	
	private Integer state;
	
	private Date updateDate;
	
	private String attributes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getDeploymentUnit() {
		return deploymentUnit;
	}

	public void setDeploymentUnit(String deploymentUnit) {
		this.deploymentUnit = deploymentUnit;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((deploymentId == null) ? 0 : deploymentId.hashCode());
		result = prime * result
				+ ((deploymentUnit == null) ? 0 : deploymentUnit.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result
				+ ((updateDate == null) ? 0 : updateDate.hashCode());		
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
		DeploymentStoreEntry other = (DeploymentStoreEntry) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (deploymentId == null) {
			if (other.deploymentId != null)
				return false;
		} else if (!deploymentId.equals(other.deploymentId))
			return false;
		if (deploymentUnit == null) {
			if (other.deploymentUnit != null)
				return false;
		} else if (!deploymentUnit.equals(other.deploymentUnit))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DeploymentStoreEntry [id=" + id + ", deploymentId="
				+ deploymentId + ", state=" + state + ", updateDate="
				+ updateDate + "]";
	}
	
	

}
