/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.kie.services.impl.model;

import java.io.Serializable;
import java.util.Date;


public class ProcessInstanceDesc implements org.jbpm.services.api.model.ProcessInstanceDesc, Serializable{

    private static final long serialVersionUID = 7310019271033570922L;

    private long id;
    private String processId;
    private String processName;
    private String processVersion;
    private int state;
    private String deploymentId;    
    private String initiator;
    
    private Date dataTimeStamp;

    public ProcessInstanceDesc() {
    
    }

    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion, 
                                int state, String deploymentId, Date dataTimeStamp, String initiator) {
        this.id = id;
        this.processId = processId;
        this.processName = processName;
        this.processVersion = processVersion==null?"":processVersion;
        this.state = state;
        this.deploymentId = deploymentId;
        this.dataTimeStamp = dataTimeStamp;
        this.initiator = initiator;
    }
    
    public String getProcessId() {
        return processId;
    }

    public Long getId() {
        return id;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getState() {
        return state;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDesc{" + "id=" + id + ", processId=" + processId + ", processName=" + processName + ", processVersion=" + processVersion + ", state=" + state + ", deploymentId=" + deploymentId + ", initiator=" + initiator + ", dataTimeStamp=" + dataTimeStamp + '}';
    }

    public String getProcessVersion() {
        return processVersion;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

}
