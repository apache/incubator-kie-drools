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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author salaboy
 */

@Entity
public class ProcessInstanceDesc implements Serializable{

    @Id
    @GeneratedValue()
    private long pk;
    
    private long id;
    private String processId;
    private String processName;
    private int state;
    private String[] eventTypes;
    
    private String processDefId;
    
    private int sessionId;
    
    private String domainName;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataTimeStamp;

    public ProcessInstanceDesc() {
        this.dataTimeStamp = new Date();
    }

    public ProcessInstanceDesc(long id, String processId, String processName, 
                                int state, String[] eventTypes, String processDefId, 
                                String domainName, int sessionId) {
        this.id = id;
        this.processId = processId;
        this.processName = processName;
        this.state = state;
        this.eventTypes = eventTypes;
        this.processDefId = processDefId;
        this.sessionId = sessionId;
        this.domainName = domainName;
        this.dataTimeStamp = new Date();
    }
    
    public String getProcessId() {
        return processId;
    }

    public String getProcessDefId() {
        return processDefId;
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

    public String[] getEventTypes() {
        return eventTypes;
    }

    public int getSessionId() {
        return sessionId;
    }

    public Date getDataTimeStamp() {
        return dataTimeStamp;
    }

    public String getDomainName() {
        return domainName;
    }

    @Override
    public String toString() {
        return "ProcessInstanceDesc["+dataTimeStamp.toString()+"]{" + "pk=" + pk + ", id=" + id + ", processId=" + processId + ", processName=" + processName + ", state=" + state + ", eventTypes=" + eventTypes + ", processDefId=" + processDefId + ", sessionId=" + sessionId + ", domainName=" + domainName + '}';
    }

    
    
  

}
