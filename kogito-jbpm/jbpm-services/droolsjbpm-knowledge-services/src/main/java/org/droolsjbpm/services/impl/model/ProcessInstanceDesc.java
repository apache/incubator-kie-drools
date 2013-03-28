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
package org.droolsjbpm.services.impl.model;

import java.io.Serializable;
import java.util.Date;


public class ProcessInstanceDesc implements Serializable{

    private long id;
    private String processId;
    private String processName;
    private String processVersion;
    private int state;
    private String domainId;    
    private String initiator;
    
    private Date dataTimeStamp;

    public ProcessInstanceDesc() {
    
    }

    public ProcessInstanceDesc(long id, String processId, String processName, String processVersion, 
                                int state, String domainId, Date dataTimeStamp, String initiator) {
        this.id = id;
        this.processId = processId;
        this.processName = processName;
        this.processVersion = processVersion==null?"":processVersion;
        this.state = state;
        this.domainId = domainId;
        this.dataTimeStamp = dataTimeStamp;
        this.initiator = initiator;
    }
    
    public String getProcessId() {
        return processId;
    }

    public long getId() {
        return id;
    }

    public String getProcessName() {
        return processName;
    }

    public int getState() {
        return state;
    }

    public String getDomainId() {
        return domainId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDesc{" + "id=" + id + ", processId=" + processId + ", processName=" + processName + ", processVersion=" + processVersion + ", state=" + state + ", domainId=" + domainId + ", initiator=" + initiator + ", dataTimeStamp=" + dataTimeStamp + '}';
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

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

}
